package com.example.argos_home.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Branded top bar shared by every calendar view. */
@Composable
fun CalendarTopBar(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary600)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.calendar_title),
            style = MaterialTheme.typography.titleLarge,
            color = White50
        )
        Spacer(modifier = Modifier.weight(1f))
        NotificationBell()
    }
}

/** Bell with its unread badge. Notifications are a later module, so the bell is not actionable. */
@Composable
private fun NotificationBell(modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(32.dp), contentAlignment = Alignment.Center) {
        Icon(
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = stringResource(R.string.notifications_description),
            tint = White50,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = stringResource(R.string.notifications_count),
            style = MaterialTheme.typography.labelSmall,
            color = White50,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 4.dp, y = (-2).dp)
                .clip(CircleShape)
                .background(Error1)
                .padding(horizontal = 5.dp)
        )
    }
}

/** Month / Week / Day selector; the active tab is both highlighted and the one shown below it. */
@Composable
fun ViewSelector(
    selected: CalendarView,
    onSelect: (CalendarView) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary600)
            .padding(horizontal = 20.dp)
            .padding(bottom = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CalendarView.entries.forEach { view ->
            ViewTab(
                text = stringResource(view.labelRes),
                selected = view == selected,
                onClick = { onSelect(view) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun ViewTab(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (selected) White50 else Primary600
    val foreground = if (selected) Primary600 else White50
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = foreground,
        textAlign = TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 6.dp)
    )
}

/** Pet filter as a pill with a drop-down of "All pets" plus every pet name; drives what the views show. */
@Composable
fun PetFilterBar(
    pets: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp),
        contentAlignment = Alignment.CenterEnd
    ) {
        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(if (selected == null) MaterialTheme.colorScheme.surfaceContainer else Primary600)
                .clickable { expanded = true }
                .padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val tint = if (selected == null) MaterialTheme.colorScheme.onSurfaceVariant else White50
            Icon(
                painter = painterResource(R.drawable.ic_filter),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = selected ?: stringResource(R.string.filter_by_pet),
                style = MaterialTheme.typography.labelLarge,
                color = tint
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_down),
                contentDescription = null,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.filter_all_pets)) },
                onClick = {
                    onSelect(null)
                    expanded = false
                }
            )
            pets.forEach { pet ->
                DropdownMenuItem(
                    text = { Text(pet) },
                    onClick = {
                        onSelect(pet)
                        expanded = false
                    }
                )
            }
        }
    }
}

/** Day navigator; the arrows move the selected day by one and the label comes from the real date. */
@Composable
fun DayNavigator(
    label: String,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(top = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = stringResource(R.string.previous_day),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onPrevious)
                .padding(4.dp)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = stringResource(R.string.next_day),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onNext)
                .padding(4.dp)
        )
    }
}

/** "New event" button; opens the create-activity modal. */
@Composable
fun NewEventButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Primary600)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.new_event),
            style = MaterialTheme.typography.labelLarge,
            color = White50,
            modifier = Modifier.wrapContentWidth()
        )
    }
}
