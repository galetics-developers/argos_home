package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.ui.calendar.StatusPill
import com.example.argos_home.ui.calendar.statusLabelOf
import com.example.argos_home.ui.theme.White50

/** Care schedule of one pet: its activities with kind, schedule, status and who confirmed each. */
@Composable
fun CareScheduleTab(petName: String, modifier: Modifier = Modifier) {
    val activities = ActivityRepository.activities
        .filter { it.petName == petName }
        .sortedWith(compareBy({ it.date }, { it.startTime }))

    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (activities.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.schedule_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(activities, key = { it.id }) { CareRow(it) }
        }
    }
}

/** A compact activity row: category badge, title, schedule, and status with "Confirmed by" when resolved. */
@Composable
private fun CareRow(activity: ScheduledActivity) {
    val status = statusLabelOf(activity.state, activity.type)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = activity.category,
                style = MaterialTheme.typography.labelSmall,
                color = White50,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(categoryColor(activity.category))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
            Spacer(modifier = Modifier.weight(1f))
            StatusPill(status)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = activity.title,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = "${ActivityFormats.dayMonth(activity.date)} · ${ActivityFormats.scheduleLabel(activity)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (activity.resolvedBy != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.confirmed_by, activity.resolvedBy),
                style = MaterialTheme.typography.labelMedium,
                color = status.color
            )
        }
    }
}
