package com.example.argos_home.ui.notifications

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

/** Local day of a timestamp, used to group the notifications into date sections. */
fun dayOf(millis: Long): LocalDate =
    Instant.ofEpochMilli(millis).atZone(ZoneId.systemDefault()).toLocalDate()

/** Section header of a day: TODAY, YESTERDAY, or the date itself for older days. */
@Composable
fun sectionLabel(date: LocalDate): String {
    val today = LocalDate.now()
    return when (date) {
        today -> stringResource(R.string.section_today)
        today.minusDays(1) -> stringResource(R.string.section_yesterday)
        else -> ActivityFormats.dayMonth(date).uppercase()
    }
}

/** Relative age of a notification: "now", minutes, hours or days ago. */
@Composable
fun relativeTime(millis: Long): String {
    val diff = System.currentTimeMillis() - millis
    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
    val hours = TimeUnit.MILLISECONDS.toHours(diff)
    val days = TimeUnit.MILLISECONDS.toDays(diff)
    return when {
        minutes < 1 -> stringResource(R.string.time_now)
        minutes < 60 -> stringResource(R.string.time_minutes_ago, minutes.toInt())
        hours < 24 -> stringResource(R.string.time_hours_ago, hours.toInt())
        else -> stringResource(R.string.time_days_ago, days.toInt())
    }
}
