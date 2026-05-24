package com.sacredflow.app.data.local.seed

import com.sacredflow.app.data.local.entity.Template

object BuiltInTemplates {

    fun all(now: Long): List<Template> = listOf(
        Template(
            name = "Morning gratitude",
            useCase = "Gratitude",
            recipient = "Universe",
            needs = listOf("gratitude", "peace"),
            tone = "thankful",
            length = "short",
            contextSeed = null,
            isBuiltIn = true,
            createdAt = now
        ),
        Template(
            name = "Evening release",
            useCase = "Reflection",
            recipient = "Higher Self",
            needs = listOf("peace", "forgiveness"),
            tone = "surrendering",
            length = "medium",
            contextSeed = null,
            isBuiltIn = true,
            createdAt = now
        ),
        Template(
            name = "Healing wish",
            useCase = "Healing",
            recipient = "Universe",
            needs = listOf("healing", "hope"),
            tone = "gentle",
            length = "medium",
            contextSeed = null,
            isBuiltIn = true,
            createdAt = now
        ),
        Template(
            name = "Strength before a hard day",
            useCase = "Intention",
            recipient = "Higher Self",
            needs = listOf("strength", "courage"),
            tone = "grounded",
            length = "short",
            contextSeed = null,
            isBuiltIn = true,
            createdAt = now
        ),
        Template(
            name = "A gentle forgiveness",
            useCase = "Reflection",
            recipient = "Higher Self",
            needs = listOf("forgiveness", "peace"),
            tone = "gentle",
            length = "medium",
            contextSeed = null,
            isBuiltIn = true,
            createdAt = now
        ),
        Template(
            name = "Setting an intention",
            useCase = "Intention",
            recipient = "Universe",
            needs = listOf("clarity", "hope"),
            tone = "hopeful",
            length = "short",
            contextSeed = null,
            isBuiltIn = true,
            createdAt = now
        )
    )
}
