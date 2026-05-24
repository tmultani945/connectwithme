package com.sacredflow.app.core.installid

import com.sacredflow.app.data.datastore.AppDataStore
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class InstallIdProvider @Inject constructor(
    private val dataStore: AppDataStore
) {

    @Volatile private var cached: String? = null
    private val mutex = Mutex()

    suspend fun get(): String {
        cached?.let { return it }
        return mutex.withLock {
            cached?.let { return@withLock it }
            val existing = dataStore.getInstallId()
            val resolved = existing ?: UUID.randomUUID().toString().also {
                dataStore.setInstallId(it)
            }
            cached = resolved
            resolved
        }
    }
}
