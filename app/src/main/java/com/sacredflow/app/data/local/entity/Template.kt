package com.sacredflow.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "templates",
    indices = [
        Index(value = ["isBuiltIn"]),
        Index(value = ["useCase"])
    ]
)
data class Template(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val useCase: String,
    val recipient: String,
    val needs: List<String>,
    val tone: String,
    val length: String,
    val contextSeed: String?,
    val isBuiltIn: Boolean,
    val createdAt: Long
)
