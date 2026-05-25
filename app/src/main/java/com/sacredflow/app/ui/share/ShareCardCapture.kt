package com.sacredflow.app.ui.share

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.content.FileProvider
import androidx.core.view.drawToBitmap
import com.sacredflow.app.domain.model.Tone
import com.sacredflow.app.ui.theme.SacredFlowTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

/**
 * Captures a [ShareCard] composable to a PNG and returns a content:// URI that
 * other apps can read via our [androidx.core.content.FileProvider].
 *
 * Implementation: builds an off-screen [ComposeView] inside the activity's
 * content root, sets its content to the share-card composable, gives it one
 * frame to compose + layout, then uses
 * [androidx.core.view.drawToBitmap] to read the pixel buffer.
 *
 * We position the view at a strong negative X so it never appears on screen.
 * Once captured, we remove it from the view tree. Total visible time: zero.
 */
object ShareCardCapture {

    private const val WIDTH_PX = 1080
    private const val HEIGHT_PX = 1920
    private const val FILE_NAME = "today_reflection.png"
    private const val DIRECTORY = "share_cards"

    /**
     * Renders + saves the share card. Returns a shareable content:// URI.
     * Throws if the activity is no longer valid.
     */
    suspend fun render(
        activity: ComponentActivity,
        text: String,
        recipient: String,
        tone: Tone,
        forTarget: String? = null
    ): Uri {
        val bitmap = withContext(Dispatchers.Main) {
            renderBitmap(activity, text, recipient, tone, forTarget)
        }
        return withContext(Dispatchers.IO) { saveAndProvide(activity, bitmap) }
    }

    private suspend fun renderBitmap(
        activity: ComponentActivity,
        text: String,
        recipient: String,
        tone: Tone,
        forTarget: String?
    ): Bitmap {
        val composeView = ComposeView(activity).apply {
            // Dispose immediately when our hosting container detaches — we'll
            // remove the container ourselves below, but defensive nonetheless.
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                SacredFlowTheme {
                    ShareCard(
                        text = text,
                        recipient = recipient,
                        tone = tone,
                        forTarget = forTarget
                    )
                }
            }
        }

        // Host container — sized exactly to the share card. Positioned offscreen
        // by a very negative translationX so the user never sees it during capture.
        val container = FrameLayout(activity).apply {
            translationX = -10000f
            addView(
                composeView,
                FrameLayout.LayoutParams(WIDTH_PX, HEIGHT_PX)
            )
        }

        val root = activity.findViewById<ViewGroup>(android.R.id.content)
            ?: error("Activity has no content root.")
        root.addView(container, ViewGroup.LayoutParams(WIDTH_PX, HEIGHT_PX))

        try {
            // Explicit measure + layout — don't depend on the view tree's normal pass.
            composeView.measure(
                View.MeasureSpec.makeMeasureSpec(WIDTH_PX, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(HEIGHT_PX, View.MeasureSpec.EXACTLY)
            )
            composeView.layout(0, 0, WIDTH_PX, HEIGHT_PX)

            // Give Compose a couple of frames to finish first composition + apply
            // any LaunchedEffects (e.g. fonts loaded via Google Fonts provider).
            // Empirically 120ms is enough on a mid-range device.
            delay(180)

            return composeView.drawToBitmap(config = Bitmap.Config.ARGB_8888)
        } finally {
            root.removeView(container)
        }
    }

    private fun saveAndProvide(context: Context, bitmap: Bitmap): Uri {
        val dir = File(context.cacheDir, DIRECTORY).apply { mkdirs() }
        val file = File(dir, FILE_NAME)
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 95, out)
        }
        bitmap.recycle()
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}

/**
 * Builds the share intent for a rendered card URI. Sends image/png; Instagram,
 * WhatsApp, Messages, Drive, Photos all handle this format.
 *
 * We deliberately do NOT include the full prayer as EXTRA_TEXT — the prayer is
 * already on the card. Apps that show both (RCS, WhatsApp) would otherwise
 * render the prayer twice. A short caption is included so apps that surface
 * caption text show something tasteful, varied by share type.
 *
 * @param prayerText kept for API symmetry; not embedded in the intent.
 * @param forTarget when set, this is a "prayer for someone you love" share —
 *   use a warmer, gift-flavored caption.
 */
fun buildShareCardIntent(
    uri: Uri,
    prayerText: String,
    forTarget: String? = null
): Intent {
    val caption = if (!forTarget.isNullOrBlank()) {
        "I'm holding you in light today. · connectyourself.app"
    } else {
        "from Connect Yourself · connectyourself.app"
    }
    return Intent(Intent.ACTION_SEND).apply {
        type = "image/png"
        putExtra(Intent.EXTRA_STREAM, uri)
        putExtra(Intent.EXTRA_TEXT, caption)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
    }
}
