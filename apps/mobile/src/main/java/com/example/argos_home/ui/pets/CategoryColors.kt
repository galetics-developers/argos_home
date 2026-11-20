package com.example.argos_home.ui.pets

import androidx.compose.ui.graphics.Color
import com.example.argos_home.ui.theme.Primary700
import com.example.argos_home.ui.theme.Primary800
import com.example.argos_home.ui.theme.Quaternary950
import com.example.argos_home.ui.theme.Quaternary1000
import com.example.argos_home.ui.theme.Secondary700
import com.example.argos_home.ui.theme.Secondary800
import com.example.argos_home.ui.theme.Tertiary700
import com.example.argos_home.ui.theme.Tertiary800

// Dark, saturated tones across the four families that keep white label text legible; mirrors the watch criterion.
private val CATEGORY_COLORS: List<Color> = listOf(
    Tertiary700, Quaternary950, Primary700, Secondary700, Tertiary800, Quaternary1000, Primary800, Secondary800
)

/** A stable label color per category, so each category reads apart across the four palette families. */
fun categoryColor(category: String): Color {
    val index = ((category.hashCode() % CATEGORY_COLORS.size) + CATEGORY_COLORS.size) % CATEGORY_COLORS.size
    return CATEGORY_COLORS[index]
}
