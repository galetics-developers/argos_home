package com.example.argos_home.domain

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import kotlin.math.roundToInt

/** Part of the day a greeting belongs to; carried as a value so a greeting never depends on color alone. */
enum class Greeting { MORNING, AFTERNOON, EVENING }

/** Read-only summary of the home's day for the Home screen; pure so it is unit-tested on the JVM. */
data class HomeSummary(
    val total: Int,
    val pending: Int,
    val completed: Int,
    val nextEventTitle: String?,
    val upcoming: List<ScheduledActivity>
) {
    /** Completed fraction in [0,1]; zero when nothing counts, so the ring never divides by zero. */
    val fraction: Float get() = if (total == 0) 0f else completed.toFloat() / total

    /** Whole-percent form of [fraction] for the ring label. */
    val percent: Int get() = (fraction * 100f).roundToInt()

    companion object {
        /** Builds the day summary; CANCELED is ignored as if it did not exist (same base for ring and counters). */
        fun of(
            activities: List<ScheduledActivity>,
            today: LocalDate = LocalDate.now(),
            now: LocalDateTime = LocalDateTime.now()
        ): HomeSummary {
            val todays = activities.filter { it.date == today && it.state != ActivityState.CANCELED }
            val total = todays.size
            val pending = todays.count { it.state == ActivityState.PENDING }
            val completed = todays.count { it.state == ActivityState.DONE }

            // Upcoming list: every still-open activity from today on, soonest first.
            val upcoming = activities
                .filter {
                    (it.state == ActivityState.PENDING || it.state == ActivityState.CONFIRMED) &&
                        !it.date.isBefore(today)
                }
                .sortedWith(compareBy({ it.date }, { it.startTime }))

            // Next event: the soonest pending activity whose start is still ahead of now (today included).
            val nextEventTitle = activities
                .filter {
                    it.state == ActivityState.PENDING &&
                        !LocalDateTime.of(it.date, it.startTime).isBefore(now)
                }
                .minWithOrNull(compareBy({ it.date }, { it.startTime }))
                ?.title

            return HomeSummary(total, pending, completed, nextEventTitle, upcoming)
        }
    }
}

/** Avatar initials: first letter of the first name + first letter of the first surname, uppercase. */
fun userInitials(fullName: String): String {
    val parts = fullName.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
    return when {
        parts.isEmpty() -> ""
        parts.size == 1 -> parts[0].take(1).uppercase()
        else -> (parts[0].take(1) + parts[1].take(1)).uppercase()
    }
}

/** Greeting bucket by device time: morning before noon, afternoon before 18:00, evening otherwise. */
fun greetingFor(time: LocalTime): Greeting = when {
    time.hour < 12 -> Greeting.MORNING
    time.hour < 18 -> Greeting.AFTERNOON
    else -> Greeting.EVENING
}
