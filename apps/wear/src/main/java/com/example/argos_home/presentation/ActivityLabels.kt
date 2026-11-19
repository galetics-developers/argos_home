package com.example.argos_home.presentation

import androidx.compose.ui.graphics.Color
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ItemType
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.presentation.theme.Primary500
import com.example.argos_home.presentation.theme.Primary700
import com.example.argos_home.presentation.theme.Primary800
import com.example.argos_home.presentation.theme.Quaternary950
import com.example.argos_home.presentation.theme.Quaternary1000
import com.example.argos_home.presentation.theme.Secondary500
import com.example.argos_home.presentation.theme.Secondary700
import com.example.argos_home.presentation.theme.Secondary800
import com.example.argos_home.presentation.theme.Tertiary700
import com.example.argos_home.presentation.theme.Tertiary800

/** Short English word for an activity kind, shared by the Home card and the Actions list. */
fun kindLabel(type: ItemType): String = if (type == ItemType.TASK) "Task" else "Event"

/** Label color of the activity kind; Task and Event read as two different brand tones. */
fun activityTypeColor(type: ItemType): Color = if (type == ItemType.TASK) Primary500 else Secondary500

// Dark, saturated tones across the four families that keep white label text legible; they exclude the two kind tones.
private val CATEGORY_COLORS: List<Color> = listOf(
    Tertiary700, Quaternary950, Primary700, Secondary700, Tertiary800, Quaternary1000, Primary800, Secondary800
)

/** A stable label color per category, so each category reads apart across the four palette families. */
fun categoryColor(category: String): Color {
    val index = ((category.hashCode() % CATEGORY_COLORS.size) + CATEGORY_COLORS.size) % CATEGORY_COLORS.size
    return CATEGORY_COLORS[index]
}

// A task shows a single start time; an event shows the full start–end range, each side with its own AM/PM.
/** The time text of an activity: one time for a task, a two-ended range for an event. */
fun activityTimeText(activity: ScheduledActivity): String =
    if (activity.endTime != null) {
        "${ActivityFormats.time(activity.startTime)} – ${ActivityFormats.time(activity.endTime)}"
    } else {
        ActivityFormats.time(activity.startTime)
    }
