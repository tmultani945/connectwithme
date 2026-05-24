package com.sacredflow.app.data.local.seed

import com.sacredflow.app.data.local.dao.TemplateDao
import com.sacredflow.app.data.local.dao.UserPreferenceDao
import com.sacredflow.app.data.local.entity.UserPreference
import javax.inject.Inject

/**
 * Runs on first DB open (and on every cold start — operations are idempotent).
 * Inserts the singleton UserPreference row if missing and the built-in templates if missing.
 */
class DatabaseSeeder @Inject constructor(
    private val templateDao: TemplateDao,
    private val preferenceDao: UserPreferenceDao
) {

    suspend fun seedIfNeeded() {
        if (preferenceDao.get() == null) {
            preferenceDao.upsert(UserPreference())
        }
        if (templateDao.countBuiltIn() == 0) {
            templateDao.insertAll(BuiltInTemplates.all(System.currentTimeMillis()))
        }
    }
}
