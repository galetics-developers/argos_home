package com.example.argos_home.ui.calendar

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.ui.theme.Primary600

/** Bottom navigation destinations; only Pets and Calendar are wired, the rest are drawn for fidelity. */
enum class Tab(@DrawableRes val iconRes: Int, @StringRes val labelRes: Int, val actionable: Boolean) {
    HOME(R.drawable.ic_nav_home, R.string.nav_home, false),
    PETS(R.drawable.ic_nav_pets, R.string.nav_pets, true),
    CALENDAR(R.drawable.ic_nav_calendar, R.string.nav_calendar, true),
    HOUSEHOLD(R.drawable.ic_nav_household, R.string.nav_household, false),
    PROFILE(R.drawable.ic_nav_profile, R.string.nav_profile, false)
}

/** Bottom navigation of the phone app; a tap on a wired tab switches the active section. */
@Composable
fun BottomNavBar(selected: Tab, onSelect: (Tab) -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Tab.entries.forEach { tab ->
                NavItem(
                    tab = tab,
                    selected = tab == selected,
                    onClick = if (tab.actionable) ({ onSelect(tab) }) else null
                )
            }
        }
    }
}

@Composable
private fun NavItem(tab: Tab, selected: Boolean, onClick: (() -> Unit)?, modifier: Modifier = Modifier) {
    // The active entry differs by color and a heavier label, and every entry keeps its name: color is never alone.
    val tint = if (selected) Primary600 else MaterialTheme.colorScheme.onSurfaceVariant
    val style = if (selected) MaterialTheme.typography.labelMedium else MaterialTheme.typography.labelSmall
    Column(
        modifier = modifier
            .padding(horizontal = 4.dp)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(tab.iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(text = stringResource(tab.labelRes), style = style, color = tint)
    }
}
