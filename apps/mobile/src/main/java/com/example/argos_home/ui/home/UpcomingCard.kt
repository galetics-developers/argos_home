package com.example.argos_home.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.ui.calendar.StatusPill
import com.example.argos_home.ui.calendar.statusLabelOf
import com.example.argos_home.ui.pets.categoryColor
import com.example.argos_home.ui.theme.White50

/** One upcoming activity: a category-colored avatar, its title, pet and schedule, and a state pill. */
@Composable
fun UpcomingCard(
    activity: ScheduledActivity,
    onOpen: (ScheduledActivity) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        // A double tap opens this activity in the calendar's Day view on its own date.
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(activity.id) { detectTapGestures(onDoubleTap = { onOpen(activity) }) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(categoryColor(activity.category)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(categoryIconOf(activity.category)),
                    contentDescription = null,
                    tint = White50,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = activity.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = activity.petName,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ActivityFormats.dayMonth(activity.date)} · ${ActivityFormats.scheduleLabel(activity)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(modifier = Modifier.size(8.dp))
            StatusPill(statusLabelOf(activity.state, activity.type))
        }
    }
}
