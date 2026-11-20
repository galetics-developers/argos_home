package com.example.argos_home.ui.home

import androidx.annotation.DrawableRes
import com.example.argos_home.R

/** A representative icon per activity category; an unknown category falls back to the calendar glyph. */
@DrawableRes
fun categoryIconOf(category: String): Int = when (category.trim().lowercase()) {
    "walking" -> R.drawable.ic_bone
    "play" -> R.drawable.ic_bone
    "feeding" -> R.drawable.ic_bowl
    "training" -> R.drawable.ic_dumbbell
    "grooming" -> R.drawable.ic_scissors
    "vaccination" -> R.drawable.ic_syringe
    "deworming" -> R.drawable.ic_pill
    "checkup" -> R.drawable.ic_clipboard
    "dental" -> R.drawable.ic_tooth
    else -> R.drawable.ic_calendar
}
