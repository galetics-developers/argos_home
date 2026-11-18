package com.example.argos_home.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import androidx.wear.tooling.preview.devices.WearDevices
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.presentation.theme.Argos_homeTheme
import kotlinx.coroutines.delay

// The only composable that knows [HomeStore]: everything below takes plain data and lambdas, and stays previewable.
/** The watch home and its three states: an activity to decide, the day resolved, and a request on its way. */
@Composable
fun HomeScreen(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = HomeStore.currentActivity
    val result = HomeStore.lastResult
    val failure = HomeStore.lastError

    // Only an accepted result advances after 1.5 s: a refusal or a failure never does, so ALREADY_DONE stays visible.
    // Keyed on the answer, so it runs once per answer and is cancelled if the screen leaves the composition.
    LaunchedEffect(result) {
        if (result != null && result.accepted) {
            delay(1_500L)
            HomeStore.advanceToNextPending()
            HomeStore.clearLastResult()
        }
    }

    val resultMessage = when {
        failure != null -> failureMessageOf(failure)
        result != null -> resultMessageOf(result)
        else -> null
    }

    val lastRequest = HomeStore.lastRequest

    // A failure or refusal stays on the card instead of a full-screen trap, so the user keeps navigating and can retry.
    val onOutcomeTap: (() -> Unit)? = when {
        failure != null && lastRequest != null ->
            { { onRequestTransition(lastRequest.activityId, lastRequest.targetState) } }

        result?.accepted == false -> { { HomeStore.dismissOutcome() } }
        else -> null
    }

    WatchFrame(modifier = modifier) { scale ->
        when {
            // Only an in-flight request takes over the screen; it clears the moment the phone answers or sending fails.
            HomeStore.awaitingResult -> SyncingContent(scale = scale, message = null, onRetry = null)

            activity == null -> UpToDateContent(scale = scale)

            else -> NextActivityContent(
                activity = activity,
                resultMessage = resultMessage,
                sendBlocked = HomeStore.awaitingResult,
                scale = scale,
                onAction = { target -> onRequestTransition(activity.id, target) },
                onOutcomeTap = onOutcomeTap
            )
        }
    }
}

// The design frame is taller than a round screen, so scale comes from the real width and the column scrolls.
// Scaling by height would shrink the buttons below a usable size, and not scaling would push them off screen.
/** Lays out one watch screen over the proportions of the approved design. */
@Composable
private fun WatchFrame(
    modifier: Modifier = Modifier,
    content: @Composable (scale: Float) -> Unit
) {
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

/** The "next activity" state: the card, the outcome in words and the three actions. */
@Composable
private fun NextActivityContent(
    activity: ScheduledActivity,
    resultMessage: ResultMessage?,
    sendBlocked: Boolean,
    scale: Float,
    onAction: (ActivityState) -> Unit,
    onOutcomeTap: (() -> Unit)? = null
) {
    NextActivityCard(
        dateLabel = ActivityFormats.watchDateLabel(),
        activity = activity,
        scale = scale
    )

    OutcomeText(message = resultMessage, scale = scale, onDismiss = onOutcomeTap)

    Spacer(modifier = Modifier.height((ACTION_ROW_GAP * scale).dp))
    ActionButtons(
        type = activity.type,
        state = activity.state,
        // Visual half of the guard: while a request is in flight no action is offered.
        sendBlocked = sendBlocked,
        onAction = onAction,
        scale = scale
    )

    BottomIndicator(scale = scale)
}

/** The "up to date" state, shown once no activity of the day is waiting for a decision. */
@Composable
private fun UpToDateContent(scale: Float) {
    Spacer(modifier = Modifier.height((EMPTY_STATE_TOP * scale).dp))
    Icon(
        painter = painterResource(R.drawable.ic_check),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size((STATE_ICON_SIZE * scale).dp)
    )
    Spacer(modifier = Modifier.height((STATE_TEXT_GAP * scale).dp))
    Text(
        text = stringResource(R.string.up_to_date_title),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.up_to_date_message),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (STATE_TEXT_PADDING * scale).dp)
    )
    BottomIndicator(scale = scale)
}

/** The syncing state; a [message] means it reports a failure, and [onRetry] turns the whole screen into a retry. */
@Composable
private fun SyncingContent(
    scale: Float,
    message: ResultMessage?,
    onRetry: (() -> Unit)?
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onRetry != null) {
                    Modifier.clickable(role = Role.Button, onClick = onRetry)
                } else {
                    Modifier
                }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height((EMPTY_STATE_TOP * scale).dp))
        // The sync glyph spins so a wait reads as progress, not a frozen dark screen.
        val spin = rememberInfiniteTransition(label = "sync")
        val angle by spin.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(tween(durationMillis = 1200, easing = LinearEasing)),
            label = "sync-angle"
        )
        Icon(
            painter = painterResource(R.drawable.ic_sync),
            contentDescription = stringResource(R.string.syncing_description),
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size((STATE_ICON_SIZE * scale).dp)
                .graphicsLayer { rotationZ = angle }
        )
        Spacer(modifier = Modifier.height((STATE_TEXT_GAP * scale).dp))
        Text(
            text = stringResource(R.string.syncing_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Text(
            text = message?.text ?: stringResource(R.string.syncing_message),
            style = MaterialTheme.typography.bodySmall,
            color = message?.color ?: MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (STATE_TEXT_PADDING * scale).dp)
        )
        BottomIndicator(scale = scale)
    }
}

/** Outcome of the last request in words and color; [onDismiss] makes it tappable, the way out of a refused card. */
@Composable
private fun OutcomeText(message: ResultMessage?, scale: Float, onDismiss: (() -> Unit)? = null) {
    if (message == null) {
        return
    }
    Spacer(modifier = Modifier.height((OUTCOME_GAP * scale).dp))
    Text(
        text = message.text,
        style = MaterialTheme.typography.bodyExtraSmall,
        color = message.color,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onDismiss != null) {
                    Modifier.clickable(role = Role.Button, onClick = onDismiss)
                } else {
                    Modifier
                }
            )
            .padding(horizontal = (STATE_TEXT_PADDING * scale).dp)
    )
}

/** The module dots, drawn at the foot of every state of the screen. */
@Composable
private fun BottomIndicator(scale: Float) {
    Spacer(modifier = Modifier.height((INDICATOR_GAP * scale).dp))
    ModuleIndicator(activeIndex = 0, scale = scale)
    Spacer(modifier = Modifier.height((INDICATOR_BOTTOM * scale).dp))
}

// Design units of the watch home, kept as written in the approved layout.
private const val DESIGN_FRAME_WIDTH = 224f
private const val ACTION_ROW_GAP = 12f
private const val INDICATOR_GAP = 12f
private const val INDICATOR_BOTTOM = 16f
private const val OUTCOME_GAP = 6f
private const val EMPTY_STATE_TOP = 40f
private const val STATE_ICON_SIZE = 32f
private const val STATE_TEXT_GAP = 10f
private const val STATE_TEXT_PADDING = 16f

@Preview(device = WearDevices.SMALL_ROUND, showBackground = true)
@Composable
private fun NextActivityTaskPreview() {
    Argos_homeTheme {
        WatchFrame { scale ->
            NextActivityContent(
                activity = SeedData.activities.first(),
                resultMessage = null,
                sendBlocked = false,
                scale = scale,
                onAction = {}
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showBackground = true)
@Composable
private fun NextActivityEventPreview() {
    Argos_homeTheme {
        WatchFrame { scale ->
            NextActivityContent(
                activity = SeedData.activities.first { it.endTime != null },
                resultMessage = null,
                sendBlocked = false,
                scale = scale,
                onAction = {}
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showBackground = true)
@Composable
private fun UpToDatePreview() {
    Argos_homeTheme {
        WatchFrame { scale -> UpToDateContent(scale = scale) }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showBackground = true)
@Composable
private fun SyncingPreview() {
    Argos_homeTheme {
        WatchFrame { scale ->
            SyncingContent(scale = scale, message = null, onRetry = null)
        }
    }
}
