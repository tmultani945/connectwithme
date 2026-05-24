package com.sacredflow.app.ui.practice

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sacredflow.app.BuildConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import kotlin.coroutines.resume

// Voice + chunking constants — keep in sync with the Worker default in wrangler.toml.
private const val TTS_VOICE = "nova"
private const val TTS_MODEL = "tts-1"
// OpenAI TTS speed range: 0.25 (very slow) .. 4.0 (very fast). 1.0 is default.
// 0.85 = ~15% slower; suits chant pacing without sounding sluggish.
// Changing this value invalidates the on-device audio cache automatically
// because TTS_SPEED is part of the cache key.
private const val TTS_SPEED = 0.85f

// How many lines ahead of the current one to keep downloaded.
// 2 means: current + next + after-next are kept ready. As the user advances, a
// new line at the front of the window starts downloading.
private const val PREFETCH_WINDOW = 2

// Chunking — short, bite-sized lines for "chant-along" pacing.
private const val TARGET_WORDS = 12
private const val MAX_WORDS = 16
private const val MERGE_TINY_THRESHOLD = 3
private val PUNCT_DELIM = Regex("(?<=[.!?,;:])\\s+|\\n+")
private val WS = Regex("\\s+")

/**
 * Phase of the user-driven chant flow.
 *
 *   Idle       — sheet not shown
 *   Preparing  — fetching audio (cache miss / network)
 *   Speaking   — MediaPlayer is playing the audio
 *   Waiting    — finished playing; sheet shows "TAP NEXT WHEN READY"
 *   Finished   — finished the last line; Next disabled
 */
enum class PracticePhase { Idle, Preparing, Speaking, Waiting, Finished }

internal fun segmentSentences(text: String): List<String> {
    if (text.isBlank()) return emptyList()

    val pieces = text
        .replace("\r\n", "\n")
        .split(PUNCT_DELIM)
        .map { it.trim() }
        .filter { it.isNotEmpty() }

    val merged = mutableListOf<String>()
    for (piece in pieces) {
        val pieceWords = piece.wordCount()
        val last = merged.lastOrNull()
        val lastWords = last?.wordCount() ?: 0
        val combined = lastWords + pieceWords
        val canMerge = last != null && (
            combined <= TARGET_WORDS ||
                (pieceWords <= MERGE_TINY_THRESHOLD && combined <= MAX_WORDS)
            )
        if (canMerge) {
            merged[merged.size - 1] = "$last $piece"
        } else {
            merged.add(piece)
        }
    }

    return merged.flatMap { chunk ->
        val words = chunk.split(WS)
        if (words.size <= MAX_WORDS) listOf(chunk)
        else words.chunked(TARGET_WORDS).map { it.joinToString(" ") }
    }
}

private fun String.wordCount(): Int = split(WS).count { it.isNotBlank() }

class PracticeState internal constructor(
    appContext: Context,
    private val scope: CoroutineScope,
) {
    var phase: PracticePhase by mutableStateOf(PracticePhase.Idle)
        private set
    var currentIndex: Int by mutableIntStateOf(0)
        private set
    var lastError: String? by mutableStateOf(null)
        private set

    private var sentences: List<String> = emptyList()
    val totalCount: Int get() = sentences.size
    val currentSentence: String? get() = sentences.getOrNull(currentIndex)
    val hasNext: Boolean get() = currentIndex < sentences.size - 1
    val hasPrevious: Boolean get() = currentIndex > 0

    private var speakJob: Job? = null
    private var mediaPlayer: MediaPlayer? = null

    // Content-addressed disk cache. Files are keyed by SHA-1 of (voice|model|text),
    // so the same line in two prayers shares the same audio. Survives across launches.
    private val cacheDir: File = File(appContext.cacheDir, "chant_audio").apply { mkdirs() }

    // In-flight downloads keyed by cache key — lets speakCurrent() join an existing
    // prefetch instead of starting a duplicate request.
    private val inflight: MutableMap<String, Job> = mutableMapOf()

    fun setText(text: String) {
        cancelSpeak()
        cancelPrefetch()
        sentences = segmentSentences(text)
        currentIndex = 0
        phase = PracticePhase.Idle
        lastError = null
    }

    /** Speak the first line, then wait for user to tap Next.
     *  Also kicks off background downloads of the next [PREFETCH_WINDOW] lines
     *  so subsequent Next/Replay taps play instantly. The window slides on
     *  every navigation call. */
    fun start() {
        if (sentences.isEmpty()) return
        currentIndex = 0
        prefetchWindow()
        speakCurrent()
    }

    /** Fire-and-forget: ensure audio is downloading (or already cached) for
     *  the current line plus the next [size] lines. Idempotent — already-cached
     *  or in-flight sentences are skipped. Doesn't cancel out-of-window
     *  downloads already in progress (they finish and stay in cache). */
    private fun prefetchWindow(size: Int = PREFETCH_WINDOW) {
        if (sentences.isEmpty()) return
        val last = (currentIndex + size).coerceAtMost(sentences.lastIndex)
        for (i in currentIndex..last) {
            val sentence = sentences[i]
            val key = cacheKey(sentence)
            val file = File(cacheDir, "$key.mp3")
            if (file.exists() && file.length() > 0) continue
            val alreadyInflight = synchronized(inflight) { inflight.containsKey(key) }
            if (alreadyInflight) continue

            val job = scope.launch(Dispatchers.IO) {
                try {
                    downloadAudio(sentence, key)
                } catch (_: Exception) { /* swallow — speakCurrent will retry */ }
                finally {
                    synchronized(inflight) { inflight.remove(key) }
                }
            }
            synchronized(inflight) { inflight[key] = job }
        }
    }

    private fun cancelPrefetch() {
        synchronized(inflight) {
            inflight.values.forEach { it.cancel() }
            inflight.clear()
        }
    }

    fun next() {
        if (!hasNext) return
        currentIndex++
        prefetchWindow()
        speakCurrent()
    }

    fun previous() {
        if (!hasPrevious) return
        currentIndex--
        prefetchWindow()
        speakCurrent()
    }

    fun replay() {
        prefetchWindow()
        speakCurrent()
    }

    fun restart() {
        currentIndex = 0
        prefetchWindow()
        speakCurrent()
    }

    fun stop() {
        cancelSpeak()
        releasePlayer()
        phase = PracticePhase.Idle
    }

    internal fun dispose() {
        cancelSpeak()
        releasePlayer()
    }

    private fun speakCurrent() {
        val sentence = sentences.getOrNull(currentIndex) ?: return
        cancelSpeak()
        releasePlayer()
        lastError = null
        speakJob = scope.launch {
            try {
                phase = PracticePhase.Preparing
                val audioFile = ensureAudio(sentence)
                if (audioFile == null) {
                    lastError = "Couldn't fetch audio. Check your connection."
                    phase = if (hasNext) PracticePhase.Waiting else PracticePhase.Finished
                    return@launch
                }
                phase = PracticePhase.Speaking
                playFile(audioFile)
                phase = if (hasNext) PracticePhase.Waiting else PracticePhase.Finished
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                lastError = e.message ?: "Audio playback failed."
                phase = if (hasNext) PracticePhase.Waiting else PracticePhase.Finished
            }
        }
    }

    private suspend fun ensureAudio(text: String): File? {
        val key = cacheKey(text)
        val file = File(cacheDir, "$key.mp3")
        if (file.exists() && file.length() > 0) return file

        // If a prefetch is downloading this same line, wait for it to finish.
        val existing = synchronized(inflight) { inflight[key] }
        if (existing != null) {
            existing.join()
            return if (file.exists() && file.length() > 0) file else null
        }

        // No prefetch in flight — download synchronously now.
        return withContext(Dispatchers.IO) { downloadAudio(text, key) }
    }

    /** Performs the actual HTTP POST + MP3 write. Returns the cached file or null. */
    private fun downloadAudio(text: String, key: String): File? {
        val file = File(cacheDir, "$key.mp3")
        if (file.exists() && file.length() > 0) return file

        val tmp = File(cacheDir, "$key-${System.nanoTime()}.tmp")
        val urlStr = BuildConfig.API_BASE_URL.trimEnd('/') + "/v1/speak"

        var conn: HttpURLConnection? = null
        try {
            conn = (URL(urlStr).openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                connectTimeout = 15_000
                readTimeout = 30_000
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "audio/mpeg, application/json")
            }
            conn.outputStream.use { out ->
                val payload = """{"text":${jsonString(text)},"voice":"$TTS_VOICE","model":"$TTS_MODEL","speed":$TTS_SPEED}"""
                out.write(payload.toByteArray(Charsets.UTF_8))
            }
            val code = conn.responseCode
            if (code !in 200..299) {
                conn.errorStream?.bufferedReader()?.use { it.readText() }
                return null
            }
            conn.inputStream.use { input ->
                tmp.outputStream().use { out -> input.copyTo(out) }
            }
            if (!tmp.exists() || tmp.length() == 0L) return null
            // Only commit if no other parallel writer beat us — race-safe.
            if (!file.exists()) tmp.renameTo(file)
            return file
        } catch (e: IOException) {
            return null
        } finally {
            try { if (tmp.exists()) tmp.delete() } catch (_: Exception) {}
            conn?.disconnect()
        }
    }

    private suspend fun playFile(file: File): Unit = suspendCancellableCoroutine { cont ->
        val mp = MediaPlayer()
        mediaPlayer = mp

        fun finish() {
            try { mp.release() } catch (_: Exception) {}
            if (mediaPlayer === mp) mediaPlayer = null
            if (cont.isActive) cont.resume(Unit)
        }

        try {
            mp.setDataSource(file.absolutePath)
            mp.setOnPreparedListener { player ->
                try { player.start() } catch (_: Exception) { finish() }
            }
            mp.setOnCompletionListener { finish() }
            mp.setOnErrorListener { _, _, _ -> finish(); true }
            mp.prepareAsync()
        } catch (e: Exception) {
            finish()
        }

        cont.invokeOnCancellation {
            try { mp.stop() } catch (_: Exception) {}
            try { mp.release() } catch (_: Exception) {}
            if (mediaPlayer === mp) mediaPlayer = null
        }
    }

    private fun releasePlayer() {
        mediaPlayer?.let { mp ->
            try { mp.stop() } catch (_: Exception) {}
            try { mp.release() } catch (_: Exception) {}
        }
        mediaPlayer = null
    }

    private fun cancelSpeak() {
        speakJob?.cancel()
        speakJob = null
    }

    private fun cacheKey(text: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        val input = "$TTS_VOICE|$TTS_MODEL|$TTS_SPEED|$text".toByteArray(Charsets.UTF_8)
        return md.digest(input).joinToString("") { "%02x".format(it) }
    }

    private fun jsonString(s: String): String {
        val sb = StringBuilder("\"")
        for (c in s) {
            when (c) {
                '\\', '"' -> sb.append('\\').append(c)
                '\n' -> sb.append("\\n")
                '\r' -> sb.append("\\r")
                '\t' -> sb.append("\\t")
                else -> if (c.code < 0x20) sb.append("\\u%04x".format(c.code)) else sb.append(c)
            }
        }
        return sb.append('"').toString()
    }
}

@Composable
fun rememberPracticeState(text: String): PracticeState {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()
    val state = remember { PracticeState(ctx.applicationContext, scope) }
    LaunchedEffect(text) { state.setText(text) }
    DisposableEffect(state) {
        onDispose { state.dispose() }
    }
    return state
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PracticeSheet(state: PracticeState) {
    if (state.phase == PracticePhase.Idle) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = { state.stop() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ── Header ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chant with me",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Surface(
                    color = MaterialTheme.colorScheme.secondaryContainer,
                    shape = RoundedCornerShape(50),
                    modifier = Modifier.padding(start = 8.dp)
                ) {
                    Text(
                        text = "${(state.currentIndex + 1).coerceAtMost(state.totalCount)} / ${state.totalCount}",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Phase indicator ──
            val phaseLabel = when (state.phase) {
                PracticePhase.Preparing -> "Loading voice…"
                PracticePhase.Speaking -> "Listen…"
                PracticePhase.Waiting -> "Tap next when ready"
                PracticePhase.Finished -> "Done"
                PracticePhase.Idle -> ""
            }
            Text(
                text = phaseLabel.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ── Current sentence ──
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 600.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = state.currentSentence ?: "",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Show error if audio fetch failed
            state.lastError?.let { err ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = err,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ── Controls ──
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { state.restart() },
                    enabled = state.currentIndex != 0 || state.phase == PracticePhase.Finished
                ) {
                    Icon(
                        Icons.Outlined.RestartAlt,
                        contentDescription = "Restart from beginning",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(
                    onClick = { state.previous() },
                    enabled = state.hasPrevious
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = "Previous line",
                        tint = if (state.hasPrevious)
                            MaterialTheme.colorScheme.onSurface
                        else
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                }

                FilledIconButton(
                    onClick = { state.next() },
                    enabled = state.hasNext,
                    modifier = Modifier.size(64.dp),
                    shape = CircleShape,
                    colors = IconButtonDefaults.filledIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                        contentDescription = "Next line",
                        modifier = Modifier.size(32.dp)
                    )
                }

                IconButton(onClick = { state.replay() }) {
                    Icon(
                        Icons.Outlined.Replay,
                        contentDescription = "Say this line again",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }

                IconButton(onClick = { state.stop() }) {
                    Icon(
                        Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}
