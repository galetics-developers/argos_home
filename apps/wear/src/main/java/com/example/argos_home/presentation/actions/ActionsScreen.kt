package com.example.argos_home.presentation.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.domain.TransitionRules
import com.example.argos_home.presentation.home.ActionButtons
import com.example.argos_home.presentation.home.ConnectionErrorState
import com.example.argos_home.presentation.home.GreenCheckState
import com.example.argos_home.presentation.home.HomeStore
import com.example.argos_home.presentation.home.NextActivityCard
import com.example.argos_home.presentation.home.SyncingState
import com.example.argos_home.presentation.home.resultMessageOf
import com.example.argos_home.presentation.activityTimeText
import com.example.argos_home.presentation.activityTypeColor
import com.example.argos_home.presentation.categoryColor
import com.example.argos_home.presentation.kindLabel
import com.example.argos_home.presentation.petIconOf
import com.example.argos_home.presentation.petTypeColor
import com.example.argos_home.presentation.petTypeColorSoft
import com.example.argos_home.presentation.theme.Error2
import com.example.argos_home.presentation.theme.Warning2
import com.example.argos_home.presentation.theme.White50
import java.time.LocalDate

// Module 2 of the watch: every non-terminal activity assigned to the local member, and a focused detail.
/** The Actions module: the pending activities of the member, with a detail that confirms or cancels each one. */
@Composable
fun ActionsScreen(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = selectedId?.let { id -> HomeStore.activities.firstOrNull { it.id == id } }
    // Pending means non-terminal: PENDING or CONFIRMED; the watch never lists DONE or CANCELED.
    val pending = HomeStore.activities.filter { !TransitionRules.isTerminal(it.state) }

    when {
        selected != null -> CenteredFrame(modifier) { scale ->
            ActionDetail(
                activity = selected,
                scale = scale,
                onAction = { target -> onRequestTransition(selected.id, target) },
                onBack = { selectedId = null }
            )
        }

        pending.isEmpty() -> CenteredFrame(modifier) { scale ->
            GreenCheckState(
                scale = scale,
                title = stringResource(R.string.nothing_pending_title),
                message = stringResource(R.string.actions_empty_message)
            )
        }

        else -> ActionsList(pending = pending, onOpen = { selectedId = it }, modifier = modifier)
    }
}

/** The pending activities, in a round-aware list that centers and scales its items to the watch screen. */
@Composable
private fun ActionsList(
    pending: List<ScheduledActivity>,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        state = rememberScalingLazyListState(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.actions_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                // Activities label and the real pending count (PENDING + CONFIRMED) on one level, both gray.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.actions_activities),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.actions_pending_count, pending.size),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        items(items = pending, key = { it.id }) { activity ->
            ActivityRow(activity = activity, onOpen = onOpen)
        }
    }
}

/** An activity card: filled type avatar, pet name, colored kind and category chips, and the time and date. */
@Composable
private fun ActivityRow(activity: ScheduledActivity, onOpen: (String) -> Unit) {
    val today = LocalDate.now()
    // Only the time and date carry urgency: yellow today, red once overdue; name, avatar and chips keep their color.
    val timeColor = when {
        activity.date == today -> Warning2
        activity.date.isBefore(today) -> Error2
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }
    val dateLabel = if (activity.date == today) TODAY_LABEL else ActivityFormats.dayMonth(activity.date)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(role = Role.Button, onClick = { onOpen(activity.id) })
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TypeAvatar(activity.petName)
        Spacer(modifier = Modifier.size(10.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                text = activity.petName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                LabelChip(text = kindLabel(activity.type), background = activityTypeColor(activity.type))
                LabelChip(text = activity.category, background = categoryColor(activity.category))
            }
            // Stacked so a full event range (both ends with AM/PM) and the date each fit their own line.
            Text(
                text = activityTimeText(activity),
                style = MaterialTheme.typography.labelMedium,
                color = timeColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = dateLabel,
                style = MaterialTheme.typography.labelSmall,
                color = timeColor,
                maxLines = 1
            )
        }
    }
}

/** A small filled pill with white text, for the activity kind and its category. */
@Composable
private fun LabelChip(text: String, background: Color) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = White50,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

/** The focused detail: the Home card and the state-dependent actions, plus a control back to the list. */
@Composable
private fun ActionDetail(
    activity: ScheduledActivity,
    scale: Float,
    onAction: (ActivityState) -> Unit,
    onBack: () -> Unit
) {
    val awaiting = HomeStore.awaitingResult && HomeStore.pendingActivityId == activity.id
    val failed = HomeStore.lastError != null && HomeStore.lastRequest?.activityId == activity.id
    val result = HomeStore.lastResult
    val outcome = if (result != null && result.activityId == activity.id) resultMessageOf(result) else null

    when {
        awaiting -> SyncingState(scale = scale)

        failed -> ConnectionErrorState(
            scale = scale,
            message = stringResource(R.string.error_connection_message),
            onBack = {
                HomeStore.dismissOutcome()
                onBack()
            }
        )

        else -> {
            NextActivityCard(dateLabel = ActivityFormats.watchDateLabel(), activity = activity, scale = scale)

            if (outcome != null) {
                Spacer(modifier = Modifier.height((6f * scale).dp))
                Text(
                    text = outcome.text,
                    style = MaterialTheme.typography.bodyExtraSmall,
                    color = outcome.color,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(horizontal = (12f * scale).dp)
                )
            }

            Spacer(modifier = Modifier.height((12f * scale).dp))
            ActionButtons(
                type = activity.type,
                state = activity.state,
                sendBlocked = HomeStore.awaitingResult,
                onAction = onAction,
                scale = scale
            )

            Spacer(modifier = Modifier.height((10f * scale).dp))
            Text(
                text = stringResource(R.string.actions_back_description),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(role = Role.Button, onClick = onBack)
                    .padding(vertical = (4f * scale).dp)
            )
            Spacer(modifier = Modifier.height((16f * scale).dp))
        }
    }
}

/** A circular pet avatar with a soft type background and the strong type glyph, matching the Home avatar. */
@Composable
private fun TypeAvatar(petName: String) {
    val type = SeedData.petTypeOf(petName)
    Box(
        modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(petTypeColor(type)),
        contentAlignment = Alignment.Center
    ) {
        // Strong circle, light same-color glyph: a monochrome avatar where the icon stays readable.
        Icon(
            painter = painterResource(petIconOf(type)),
            contentDescription = null,
            tint = petTypeColorSoft(type),
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Scales a single-screen state to the real screen width and lets it scroll, as the Home module does. */
@Composable
private fun CenteredFrame(modifier: Modifier = Modifier, content: @Composable (scale: Float) -> Unit) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val scale = maxWidth / DESIGN_FRAME_WIDTH.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content(scale)
        }
    }
}

private const val DESIGN_FRAME_WIDTH = 224f
private const val TODAY_LABEL = "Today"
