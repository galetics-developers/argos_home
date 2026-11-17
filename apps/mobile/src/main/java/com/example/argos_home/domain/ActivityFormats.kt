package com.example.argos_home.domain

import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

/** Single source of every date/time string on both devices; US locale keeps an English UI from drifting to es. */
object ActivityFormats {

    private val TIME: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm a", Locale.US)
    private val TIME_NO_PERIOD: DateTimeFormatter = DateTimeFormatter.ofPattern("h:mm", Locale.US)
    private val DAY_MONTH: DateTimeFormatter = DateTimeFormatter.ofPattern("MMM d", Locale.US)
    private val WEEKDAY: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE", Locale.US)
    private val WEEKDAY_FULL: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE", Locale.US)
    private val MONTH_YEAR: DateTimeFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.US)

    fun time(t: LocalTime): String = t.format(TIME)

    /** Event range compressed like the prototype: drops the first period when both sides share AM or PM. */
    fun timeRange(start: LocalTime, end: LocalTime?): String {
        if (end == null) return time(start)
        val samePeriod = (start.hour < 12) == (end.hour < 12)
        val left = if (samePeriod) start.format(TIME_NO_PERIOD) else start.format(TIME)
        return "$left–${end.format(TIME)}"
    }

    /** The time or range a card shows, picked by item type. */
    fun scheduleLabel(activity: ScheduledActivity): String =
        if (activity.type == ItemType.EVENT) timeRange(activity.startTime, activity.endTime)
        else time(activity.startTime)

    fun monthTitle(date: LocalDate): String = date.format(MONTH_YEAR)

    /** Date chip of a card: "Today" on the current day, otherwise "Sep 21". */
    fun dateChip(date: LocalDate, today: LocalDate = LocalDate.now()): String =
        if (date == today) "Today" else date.format(DAY_MONTH)

    /** Day navigator label: "Today · Sep 21" on the current day, "Mon · Sep 21" otherwise. */
    fun dayNavigatorLabel(date: LocalDate, today: LocalDate = LocalDate.now()): String {
        val lead = if (date == today) "Today" else date.format(WEEKDAY)
        return "$lead · ${date.format(DAY_MONTH)}"
    }

    /** Watch header: "Home - Sep 21" with the real current day. */
    fun watchDateLabel(today: LocalDate = LocalDate.now()): String = "Home - ${today.format(DAY_MONTH)}"

    fun dayMonth(date: LocalDate): String = date.format(DAY_MONTH)

    fun weekdayFull(date: LocalDate): String = date.format(WEEKDAY_FULL)

    fun recurrenceLabel(recurrence: Recurrence): String = when (recurrence) {
        Recurrence.ONCE -> "Once"
        Recurrence.DAILY -> "Daily"
        Recurrence.WEEKLY -> "Weekly"
        Recurrence.MONTHLY -> "Monthly"
    }

    /** Reminder value: a clock time for minute/hour leads (as in the prototype), a plain lead otherwise. */
    fun reminderValue(activity: ScheduledActivity): String = when (activity.reminderUnit) {
        ReminderUnit.MINUTES -> time(activity.startTime.minusMinutes(activity.reminderAmount.toLong()))
        ReminderUnit.HOURS -> time(activity.startTime.minusHours(activity.reminderAmount.toLong()))
        ReminderUnit.DAYS -> "${activity.reminderAmount} days before"
        ReminderUnit.MONTHS -> "${activity.reminderAmount} months before"
    }

    fun unitLabel(unit: ReminderUnit): String = when (unit) {
        ReminderUnit.MINUTES -> "minutes"
        ReminderUnit.HOURS -> "hours"
        ReminderUnit.DAYS -> "days"
        ReminderUnit.MONTHS -> "months"
    }
}
