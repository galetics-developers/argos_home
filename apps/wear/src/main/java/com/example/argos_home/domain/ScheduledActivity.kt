package com.example.argos_home.domain

import java.time.LocalDate
import java.time.LocalTime

/** One care activity of the home; dates and times are structured and turned into text by [ActivityFormats]. */
data class ScheduledActivity(
    val id: String,
    val type: ItemType,
    val petName: String,
    val title: String,
    val category: String,            // Free text task/event type, e.g. "Walking", "Appointment", "Feeding".
    val date: LocalDate,
    val startTime: LocalTime,
    val endTime: LocalTime? = null,  // Only events carry a range; a task keeps a single start time.
    val recurrence: Recurrence,
    val reminderAmount: Int,
    val reminderUnit: ReminderUnit,
    val assignedTo: String,
    val note: String,
    val state: ActivityState = ActivityState.PENDING,

    // Both stay empty until the phone accepts a transition, which is what makes a duplicate confirmation explainable.
    val resolvedBy: String? = null,
    val resolvedAtMillis: Long? = null
) {
    /** "type · pet" subtitle, derived so the card and the watch never drift from the structured fields. */
    val subtitle: String get() = "$category · $petName"
}
