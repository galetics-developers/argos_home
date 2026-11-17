package com.example.argos_home.presentation.actions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
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
import com.example.argos_home.presentation.home.HomeStore
import com.example.argos_home.presentation.home.ModuleIndicator
import com.example.argos_home.presentation.home.NextActivityCard
import com.example.argos_home.presentation.home.failureMessageOf
import com.example.argos_home.presentation.home.resultMessageOf
import com.example.argos_home.presentation.petAvatarColor
import com.example.argos_home.presentation.petUsesWhiteGlyph
import com.example.argos_home.presentation.petIconOf
import com.example.argos_home.presentation.theme.Black900
import com.example.argos_home.presentation.theme.White50

// Module 2 of the watch: the list of home activities and a focused detail reusing the Home card and actions.
/** The Actions module: every pending activity of the home, with a detail that confirms or cancels it. */
@Composable
fun ActionsScreen(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val selected = selectedId?.let { id -> HomeStore.activities.firstOrNull { it.id == id } }
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

        pending.isEmpty() -> CenteredFrame(modifier) { scale -> NothingPending(scale) }

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
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(R.string.actions_pending_count, pending.size),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        items(items = pending, key = { it.id }) { activity ->
            ActivityRow(activity = activity, onOpen = onOpen)
        }
        item { ModuleIndicator(activeIndex = 1, scale = 1f) }
    }
}

/** A compact activity card: colored avatar, pet name, kind and time; tapping it opens the detail. */
@Composable
private fun ActivityRow(activity: ScheduledActivity, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(role = Role.Button, onClick = { onOpen(activity.id) })
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        PetAvatar(activity.petName, size = 34.dp, iconSize = 20.dp)
        Spacer(modifier = Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = activity.petName,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = "${activity.category} · ${ActivityFormats.scheduleLabel(activity)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
            Text(
                text = ActivityFormats.dayMonth(activity.date),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

/** The focused detail: the Home card and the state-dependent actions, pointed at one chosen activity. */
@Composable
private fun ActionDetail(
    activity: ScheduledActivity,
    scale: Float,
    onAction: (ActivityState) -> Unit,
    onBack: () -> Unit
) {
    val awaiting = HomeStore.awaitingResult && HomeStore.pendingActivityId == activity.id
    val result = HomeStore.lastResult
    val failure = HomeStore.lastError
    val outcome = when {
        awaiting -> null
        failure != null && HomeStore.lastRequest?.activityId == activity.id -> failureMessageOf(failure)
        result != null && result.activityId == activity.id -> resultMessageOf(result)
        else -> null
    }

    NextActivityCard(dateLabel = ActivityFormats.watchDateLabel(), activity = activity, scale = scale)

    if (awaiting) {
        Spacer(modifier = Modifier.height((6f * scale).dp))
        Text(
            text = stringResource(R.string.syncing_title),
            style = MaterialTheme.typography.bodyExtraSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    } else if (outcome != null) {
        Spacer(modifier = Modifier.height((6f * scale).dp))
        Text(
            text = outcome.text,
            style = MaterialTheme.typography.bodyExtraSmall,
            color = outcome.color,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (12f * scale).dp)
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

    ModuleIndicator(activeIndex = 1, scale = scale)
    Spacer(modifier = Modifier.height((16f * scale).dp))
}

/** The empty state of the module: nothing is left to confirm or cancel. */
@Composable
private fun NothingPending(scale: Float) {
    Spacer(modifier = Modifier.height((40f * scale).dp))
    Icon(
        painter = painterResource(R.drawable.ic_check),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size((32f * scale).dp)
    )
    Spacer(modifier = Modifier.height((10f * scale).dp))
    Text(
        text = stringResource(R.string.nothing_pending_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.nothing_pending_message),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (16f * scale).dp)
    )
    Spacer(modifier = Modifier.height((12f * scale).dp))
    ModuleIndicator(activeIndex = 1, scale = scale)
    Spacer(modifier = Modifier.height((16f * scale).dp))
}

/** A circular pet avatar in the pet's own palette color, shared by the list and the detail. */
@Composable
private fun PetAvatar(petName: String, size: androidx.compose.ui.unit.Dp, iconSize: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(petAvatarColor(petName)),
        contentAlignment = Alignment.Center
    ) {
        // White glyph on the darker circles; a dark glyph on the light sandy/orange/green ones for contrast.
        Icon(
            painter = painterResource(petIconOf(SeedData.petTypeOf(petName))),
            contentDescription = null,
            tint = if (petUsesWhiteGlyph(petName)) White50 else Black900,
            modifier = Modifier.size(iconSize)
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
