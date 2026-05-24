package com.sacredflow.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sacredflow.app.data.local.entity.Template
import kotlinx.coroutines.flow.Flow

@Dao
interface TemplateDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: Template): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(templates: List<Template>)

    @Query("DELETE FROM templates WHERE id = :id AND isBuiltIn = 0")
    suspend fun deleteUserTemplate(id: Long)

    @Query("SELECT * FROM templates ORDER BY isBuiltIn DESC, createdAt DESC")
    fun observeAll(): Flow<List<Template>>

    @Query("SELECT * FROM templates WHERE useCase = :useCase ORDER BY isBuiltIn DESC, createdAt DESC")
    fun observeByUseCase(useCase: String): Flow<List<Template>>

    @Query("SELECT * FROM templates WHERE id = :id")
    suspend fun getById(id: Long): Template?

    @Query("SELECT COUNT(*) FROM templates WHERE isBuiltIn = 1")
    suspend fun countBuiltIn(): Int
}
