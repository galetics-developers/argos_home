package com.example.argos_home.ui.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import java.time.LocalDate

/** Day view: a date navigator over the day's schedule, filtered by the selected pet. */
@Composable
fun CalendarDayView(
    date: LocalDate,
    petFilter: String?,
    onPrevious: () -> Unit,
    onNext: () -> Unit,
    onAction: (ScheduledActivity, ActivityState) -> Unit,
    onNewEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activities = ActivityRepository.activitiesOn(date, petFilter)
    // Navigator, section head and "New event" stay put; only the cards between them scroll.
    Column(modifier = modifier.fillMaxSize()) {
        DayNavigator(
            label = com.example.argos_home.domain.ActivityFormats.dayNavigatorLabel(date),
            onPrevious = onPrevious,
            onNext = onNext
        )
        ScheduleSectionHeader(
            count = activities.size,
            isToday = date == LocalDate.now(),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            scheduleCards(activities, onAction)
        }
        NewEventButton(
            onClick = onNewEvent,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

/** Shared list of activity cards, or an empty line when the day has none; used by the Day and Week views. */
fun androidx.compose.foundation.lazy.LazyListScope.scheduleCards(
    activities: List<ScheduledActivity>,
    onAction: (ScheduledActivity, ActivityState) -> Unit
) {
    if (activities.isEmpty()) {
        item {
            Text(
                text = stringResource(R.string.schedule_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp)
            )
        }
        return
    }
    // A stable key stops Compose from handing the remembered state of one card over to the wrong activity.
    items(items = activities, key = { it.id }) { activity ->
        ActivityCard(
            activity = activity,
            onAction = onAction,
            modifier = Modifier.padding(horizontal = 20.dp)
        )
    }
}

/** Section head; the counter is derived from the list, so it cannot drift from the cards below. */
@Composable
fun ScheduleSectionHeader(count: Int, isToday: Boolean, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(if (isToday) R.string.schedule_header else R.string.schedule_header_other),
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = stringResource(R.string.schedule_count, count),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
