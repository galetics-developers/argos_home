package com.example.argos_home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
/** The watch home and its states: an activity to decide, the day resolved, a request on its way, or a failure. */
@Composable
fun HomeScreen(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = HomeStore.currentActivity
    val result = HomeStore.lastResult
    val failure = HomeStore.lastError

    // Only an accepted result advances after 1.5 s: a refusal never does, so ALREADY_DONE stays visible.
    LaunchedEffect(result) {
        if (result != null && result.accepted) {
            delay(1_500L)
            HomeStore.advanceToNextPending()
            HomeStore.clearLastResult()
        }
    }

    // A phone answer is shown on the card; a send failure takes over with the full error screen instead.
    val resultMessage = if (result != null) resultMessageOf(result) else null
    val onOutcomeTap: (() -> Unit)? =
        if (result?.accepted == false) { { HomeStore.dismissOutcome() } } else null

    WatchFrame(modifier = modifier) { scale ->
        when {
            // An in-flight request takes over the screen; it clears the moment the phone answers or sending fails.
            HomeStore.awaitingResult -> SyncingState(scale = scale)

            failure != null -> ConnectionErrorState(
                scale = scale,
                message = stringResource(R.string.error_connection_message),
                onBack = { HomeStore.dismissOutcome() }
            )

            activity == null -> GreenCheckState(
                scale = scale,
                title = stringResource(R.string.up_to_date_title),
                message = stringResource(R.string.up_to_date_message)
            )

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

/** The "next activity" state: the card, the outcome in words and the state-dependent actions. */
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
    Spacer(modifier = Modifier.height((BOTTOM_GAP * scale).dp))
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
            .padding(horizontal = (OUTCOME_PADDING * scale).dp)
    )
}

// Design units of the watch home, kept as written in the approved layout.
private const val DESIGN_FRAME_WIDTH = 224f
private const val ACTION_ROW_GAP = 12f
private const val OUTCOME_GAP = 6f
private const val OUTCOME_PADDING = 16f
private const val BOTTOM_GAP = 16f

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
        WatchFrame { scale ->
            GreenCheckState(
                scale = scale,
                title = stringResource(R.string.up_to_date_title),
                message = stringResource(R.string.up_to_date_message)
            )
        }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showBackground = true)
@Composable
private fun SyncingPreview() {
    Argos_homeTheme {
        WatchFrame { scale -> SyncingState(scale = scale) }
    }
}

@Preview(device = WearDevices.SMALL_ROUND, showBackground = true)
@Composable
private fun ConnectionErrorPreview() {
    Argos_homeTheme {
        WatchFrame { scale ->
            ConnectionErrorState(
                scale = scale,
                message = stringResource(R.string.error_connection_message),
                onBack = {}
            )
        }
    }
}
