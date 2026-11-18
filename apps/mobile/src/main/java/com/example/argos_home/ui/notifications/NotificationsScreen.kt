package com.example.argos_home.ui.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.NotificationRepository
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Notifications of the user: grouped by date, filterable by household, with a mark-all-read action. */
@Composable
fun NotificationsScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var household by remember { mutableStateOf<String?>(null) }
    val all = NotificationRepository.notifications
    val filtered = if (household == null) all else all.filter { it.householdName == household }
    // groupBy keeps the newest-first order already present in the list, so sections read Today, Yesterday, older.
    val groups = filtered.groupBy { dayOf(it.timestampMillis) }

    Column(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        NotificationsTopBar(onBack = onBack, onMarkAllRead = { NotificationRepository.markAllRead() })
        HouseholdFilter(
            households = NotificationRepository.households(),
            selected = household,
            onSelect = { household = it }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            groups.forEach { (date, dayItems) ->
                item(key = "header-$date") {
                    Text(
                        text = sectionLabel(date),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 6.dp, bottom = 2.dp)
                    )
                }
                items(dayItems, key = { it.id }) { notification ->
                    NotificationCard(notification)
                }
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(onBack: () -> Unit, onMarkAllRead: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary600)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = stringResource(R.string.back),
            tint = White50,
            modifier = Modifier
                .size(28.dp)
                .clickable(onClick = onBack)
                .padding(2.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.notifications_title),
            style = MaterialTheme.typography.titleMedium,
            color = White50,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = stringResource(R.string.mark_all_read),
            style = MaterialTheme.typography.labelMedium,
            color = White50,
            modifier = Modifier
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onMarkAllRead)
                .padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/** "Filter by household" pill with a drop-down of "All households" plus every household present. */
@Composable
private fun HouseholdFilter(households: List<String>, selected: String?, onSelect: (String?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
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
                text = selected ?: stringResource(R.string.filter_by_household),
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
                text = { Text(stringResource(R.string.filter_all_households)) },
                onClick = { onSelect(null); expanded = false }
            )
            households.forEach { name ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = { onSelect(name); expanded = false }
                )
            }
        }
    }
}
