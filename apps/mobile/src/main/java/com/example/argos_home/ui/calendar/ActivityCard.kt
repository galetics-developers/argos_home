package com.example.argos_home.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ItemType
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.domain.TransitionRules
import com.example.argos_home.ui.theme.Argos_homeTheme
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Info1
import com.example.argos_home.ui.theme.Primary500
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.Success1
import com.example.argos_home.ui.theme.White50

/** One activity of the day; its action buttons are the transitions the shared rules allow for its current state. */
@Composable
fun ActivityCard(
    activity: ScheduledActivity,
    onAction: (ScheduledActivity, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    val status = statusLabelOf(activity.state, activity.type)
    val targets = TransitionRules.allowedTargets(activity.state, activity.type)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PetAvatar(activity.petName)
                Spacer(modifier = Modifier.size(8.dp))
                TypeBadge(activity.type)
                Spacer(modifier = Modifier.weight(1f))
                StatusPill(status)
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = activity.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = activity.subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(10.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                DetailChip(ActivityFormats.dateChip(activity.date))
                DetailChip(ActivityFormats.scheduleLabel(activity))
                DetailChip(ActivityFormats.recurrenceLabel(activity.recurrence))
                DetailChip(stringResource(R.string.chip_reminder, ActivityFormats.reminderValue(activity)))
                DetailChip(stringResource(R.string.chip_assigned_to, activity.assignedTo))
            }

            Spacer(modifier = Modifier.height(10.dp))
            // The note is capped at two lines on the card; a longer note is trimmed with an ellipsis.
            Text(
                text = activity.note,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            // The state shows once, in the pill above; the buttons are the only thing below the note.
            if (targets.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    targets.forEach { target ->
                        ActionButton(
                            target = target,
                            onClick = { onAction(activity, target) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

/** One action as a filled button; its color tells the actions apart (cancel, done, confirm). */
@Composable
private fun ActionButton(target: ActivityState, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val (labelRes, color) = specOf(target)
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 6.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelLarge,
            color = White50,
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

/** Label and color of each action; a terminal activity offers none, so this is only called for a legal target. */
private fun specOf(target: ActivityState): Pair<Int, Color> = when (target) {
    ActivityState.CANCELED -> R.string.action_cancel to Error1
    ActivityState.DONE -> R.string.action_done to Success1
    ActivityState.CONFIRMED -> R.string.action_confirm to Info1
    ActivityState.PENDING -> R.string.action_done to Success1
}

/** A circular pet avatar in the pet's own palette color, so pets read apart across the list. */
@Composable
private fun PetAvatar(petName: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(28.dp)
            .clip(CircleShape)
            .background(petAvatarColor(petName)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(petIconOf(SeedData.petTypeOf(petName))),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(18.dp)
        )
    }
}

// Task and Event read as two distinct brand tones, matching the watch so the kind is told apart at a glance.
@Composable
private fun TypeBadge(type: ItemType, modifier: Modifier = Modifier) {
    val (label, color) = when (type) {
        ItemType.TASK -> R.string.type_task to Primary500
        ItemType.EVENT -> R.string.type_event to Secondary500
    }
    Text(
        text = stringResource(label),
        style = MaterialTheme.typography.labelSmall,
        color = White50,
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

@Composable
private fun DetailChip(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    )
}

@Preview(showBackground = true)
@Composable
private fun PendingTaskCardPreview() {
    Argos_homeTheme {
        ActivityCard(
            activity = SeedData.activities.first { it.type == ItemType.TASK },
            onAction = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PendingEventCardPreview() {
    Argos_homeTheme {
        ActivityCard(
            activity = SeedData.activities.first {
                it.type == ItemType.EVENT && it.state == ActivityState.PENDING
            },
            onAction = { _, _ -> },
            modifier = Modifier.padding(16.dp)
        )
    }
}
