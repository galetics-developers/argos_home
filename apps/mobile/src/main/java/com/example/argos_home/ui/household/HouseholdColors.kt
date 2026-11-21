package com.example.argos_home.ui.household

import androidx.compose.ui.graphics.Color
import com.example.argos_home.domain.HouseholdColor
import com.example.argos_home.domain.MemberRole
import com.example.argos_home.ui.theme.Black800
import com.example.argos_home.ui.theme.Gray500
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary200
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Quaternary200
import com.example.argos_home.ui.theme.Quaternary400
import com.example.argos_home.ui.theme.Quaternary900
import com.example.argos_home.ui.theme.Secondary100
import com.example.argos_home.ui.theme.Secondary200
import com.example.argos_home.ui.theme.Secondary600
import com.example.argos_home.ui.theme.Tertiary100
import com.example.argos_home.ui.theme.Tertiary200
import com.example.argos_home.ui.theme.Tertiary700

/** Header and avatar tones for a household's assigned color: soft background, mid circles, strong avatar. */
data class HouseholdTones(
    val headerBackground: Color,
    val headerCircle: Color,
    val avatar: Color,
    val title: Color,
    val subtitle: Color
)

/** Maps a household color family to its tones; no new colors, only tokens already in the palette. */
fun householdTones(color: HouseholdColor): HouseholdTones = when (color) {
    HouseholdColor.PRIMARY -> HouseholdTones(Primary100, Primary200, Primary600, Black800, Gray500)
    HouseholdColor.SECONDARY -> HouseholdTones(Secondary100, Secondary200, Secondary600, Black800, Gray500)
    HouseholdColor.TERTIARY -> HouseholdTones(Tertiary100, Tertiary200, Tertiary700, Black800, Gray500)
    HouseholdColor.QUATERNARY -> HouseholdTones(Quaternary200, Quaternary400, Quaternary900, Black800, Gray500)
}

/** Badge background + content for a role; admin = Secondary, member = Tertiary (always paired with its text). */
data class BadgeColors(val background: Color, val content: Color)

fun roleBadgeColors(role: MemberRole): BadgeColors = when (role) {
    MemberRole.ADMIN -> BadgeColors(Secondary100, Secondary600)
    MemberRole.MEMBER -> BadgeColors(Tertiary100, Tertiary700)
}
