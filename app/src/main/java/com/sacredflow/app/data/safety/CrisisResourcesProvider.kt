package com.sacredflow.app.data.safety

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CrisisResourcesProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {

    @Volatile private var cached: CrisisResourcesFile? = null
    private val mutex = Mutex()
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun resourcesForCurrentLocale(): CrisisResources {
        val file = load()
        val country = Locale.getDefault().country.takeIf { it.isNotBlank() } ?: file.default
        val regional = file.regions[country] ?: file.regions[file.default] ?: emptyList()
        return CrisisResources(country = country, regional = regional, global = file.global)
    }

    private suspend fun load(): CrisisResourcesFile {
        cached?.let { return it }
        return mutex.withLock {
            cached?.let { return@withLock it }
            val text = withContext(Dispatchers.IO) {
                context.assets.open(ASSET_PATH).bufferedReader().use { it.readText() }
            }
            val parsed = json.decodeFromString<CrisisResourcesFile>(text)
            cached = parsed
            parsed
        }
    }

    companion object {
        private const val ASSET_PATH = "crisis_resources.json"
    }
}

@Serializable
data class CrisisResource(
    val name: String,
    val description: String,
    val phone: String? = null,
    val sms: String? = null,
    val url: String? = null
)

@Serializable
data class GlobalResource(
    val name: String,
    val description: String,
    val url: String
)

@Serializable
private data class CrisisResourcesFile(
    val version: String,
    val default: String,
    val regions: Map<String, List<CrisisResource>>,
    val global: GlobalResource
)

data class CrisisResources(
    val country: String,
    val regional: List<CrisisResource>,
    val global: GlobalResource
)
