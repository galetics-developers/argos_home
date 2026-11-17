package com.example.argos_home.ui.calendar

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.ui.theme.Argos_homeTheme
import com.example.argos_home.ui.theme.Primary600

// Calendar is the only shipped section; the other four carry no click handler on purpose, not an empty lambda.
/** Bottom navigation of the phone app, drawn whole for faithfulness to the design. */
@Composable
fun BottomNavBar(modifier: Modifier = Modifier) {
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
            NavItem(R.drawable.ic_nav_home, R.string.nav_home, selected = false)
            NavItem(R.drawable.ic_nav_pets, R.string.nav_pets, selected = false)
            NavItem(R.drawable.ic_nav_calendar, R.string.nav_calendar, selected = true)
            NavItem(R.drawable.ic_nav_household, R.string.nav_household, selected = false)
            NavItem(R.drawable.ic_nav_profile, R.string.nav_profile, selected = false)
        }
    }
}

@Composable
private fun NavItem(
    iconRes: Int,
    labelRes: Int,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    // The active entry differs by color and by a heavier label, and every entry keeps its name: color is never alone.
    val tint = if (selected) Primary600 else MaterialTheme.colorScheme.onSurfaceVariant
    val style = if (selected) {
        MaterialTheme.typography.labelMedium
    } else {
        MaterialTheme.typography.labelSmall
    }
    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = stringResource(labelRes),
            style = style,
            color = tint
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun BottomNavBarPreview() {
    Argos_homeTheme {
        BottomNavBar()
    }
}
