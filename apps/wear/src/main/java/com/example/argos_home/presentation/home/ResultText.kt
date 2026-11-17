package com.example.argos_home.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.presentation.theme.Error1
import com.example.argos_home.presentation.theme.Info1
import com.example.argos_home.presentation.theme.Success1
import com.example.argos_home.presentation.theme.Warning1
import com.example.argos_home.presentation.wearable.SendFailure
import com.example.argos_home.protocol.ActivityResult
import com.example.argos_home.protocol.RejectionReason
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Wording and color of an outcome travel together, so reporting one without its text is impossible. */
@Immutable
data class ResultMessage(
    val text: String,
    val color: Color
)

/** Wording and color of an answer coming from the phone, accepted or refused. */
@Composable
fun resultMessageOf(result: ActivityResult): ResultMessage =
    if (result.accepted) {
        acceptedMessage(result.resultingState)
    } else {
        rejectedMessage(result)
    }

/** Wording and color of a request that never reached the phone. */
@Composable
fun failureMessageOf(failure: SendFailure): ResultMessage = when (failure) {
    // Both reachability failures read alike: from the wrist, no node answering and a node dropping are one problem.
    SendFailure.NO_PHONE_NODE,
    SendFailure.PHONE_NOT_CONNECTED ->
        ResultMessage(stringResource(R.string.failure_phone_unreachable), Warning1)

    SendFailure.SEND_FAILED -> ResultMessage(stringResource(R.string.failure_send), Error1)
}

@Composable
private fun acceptedMessage(state: ActivityState): ResultMessage = when (state) {
    ActivityState.DONE -> ResultMessage(stringResource(R.string.result_done), Success1)
    ActivityState.CONFIRMED -> ResultMessage(stringResource(R.string.result_confirmed), Info1)
    ActivityState.CANCELED -> ResultMessage(stringResource(R.string.result_canceled), Error1)
    ActivityState.PENDING -> ResultMessage(stringResource(R.string.result_pending), Warning1)
}

@Composable
private fun rejectedMessage(result: ActivityResult): ResultMessage {
    val text = when (result.reason) {
        // The refusal the user most needs explained, so it names who completed it and when the phone sends both.
        RejectionReason.ALREADY_DONE -> alreadyDoneText(result)
        RejectionReason.ALREADY_CANCELED -> stringResource(R.string.result_already_canceled)
        RejectionReason.INVALID_TRANSITION ->
            stringResource(R.string.result_invalid_transition)

        RejectionReason.UNKNOWN_ACTIVITY -> stringResource(R.string.result_unknown_activity)
        // A refusal with no reason is not expected, but it still has to read as words.
        null -> stringResource(R.string.result_rejected)
    }
    return ResultMessage(text, stateColor(result.resultingState))
}

@Composable
private fun alreadyDoneText(result: ActivityResult): String {
    val actor = result.actorName
    val resolvedAt = result.resolvedAtMillis
    return if (actor != null && resolvedAt != null) {
        stringResource(R.string.result_already_done, actor, formatTimeOfDay(resolvedAt))
    } else {
        stringResource(R.string.result_already_done_short)
    }
}

private fun stateColor(state: ActivityState): Color = when (state) {
    ActivityState.DONE -> Success1
    ActivityState.CONFIRMED -> Info1
    ActivityState.CANCELED -> Error1
    ActivityState.PENDING -> Warning1
}

// The locale is pinned to US so the time reads in English even on a device set to another language.
/** Time of day of a resolution reported by the phone: a real instant that travelled inside the answer. */
private fun formatTimeOfDay(millis: Long): String =
    SimpleDateFormat("h:mm a", Locale.US).format(Date(millis))
