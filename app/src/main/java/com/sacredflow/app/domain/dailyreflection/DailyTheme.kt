package com.sacredflow.app.domain.dailyreflection

import com.sacredflow.app.domain.model.Tone
import java.util.Calendar
import java.util.TimeZone

/**
 * Deterministic theme picker for the auto-generated daily reflection. Same input
 * date returns the same tone + topic — so a re-open within the same day shows the
 * same theme even if we ever regenerate.
 *
 * Tone rotates by weekday; topic rotates by day-of-year through a small curated
 * pool so the reader perceives variety without us asking them what to bring.
 */
object DailyTheme {

    data class Theme(val tone: Tone, val topic: String)

    private val toneByWeekday = mapOf(
        Calendar.MONDAY to Tone.Grounded,
        Calendar.TUESDAY to Tone.Hopeful,
        Calendar.WEDNESDAY to Tone.Powerful,
        Calendar.THURSDAY to Tone.Gentle,
        Calendar.FRIDAY to Tone.Thankful,
        Calendar.SATURDAY to Tone.Surrendering,
        Calendar.SUNDAY to Tone.Gentle
    )

    private val topics = listOf(
        "the day ahead",
        "what I'm carrying",
        "the people I love",
        "the work that's in front of me",
        "the part of me that's tired",
        "something small that mattered yesterday",
        "the version of me I'm becoming",
        "the things I can't control",
        "what wants to be released",
        "a quiet kindness I can offer myself",
        "the breath I almost forgot to take",
        "the place where I am, right now",
        "what would help me feel held today",
        "what I'd like to remember by tonight"
    )

    fun forDate(nowMillis: Long): Theme {
        val cal = Calendar.getInstance(TimeZone.getDefault()).apply { timeInMillis = nowMillis }
        val tone = toneByWeekday[cal.get(Calendar.DAY_OF_WEEK)] ?: Tone.Gentle
        val topic = topics[cal.get(Calendar.DAY_OF_YEAR) % topics.size]
        return Theme(tone = tone, topic = topic)
    }
}
