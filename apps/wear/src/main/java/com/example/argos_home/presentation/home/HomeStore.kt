package com.example.argos_home.presentation.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.domain.TransitionRules
import com.example.argos_home.presentation.wearable.SendFailure
import com.example.argos_home.protocol.ActivityResult
import java.time.LocalDateTime

/** A transition the user asked for, kept so a failed request can be retried as it was. */
data class TransitionRequest(
    val activityId: String,
    val targetState: ActivityState
)

// A singleton, not a view model: the state must outlive screen recreation while a request is in flight.
// The list is a local seeded copy of the day until it is synchronized from the phone in a later release.
/** Watch home state; [applyResult] is the only writer, so no transition is ever applied without a phone result. */
object HomeStore {

    // The watch is a personal client: only activities assigned to the local member are shown, sorted by date and time.
    private val mutableActivities = mutableStateListOf<ScheduledActivity>().apply {
        addAll(
            SeedData.activities
                .filter { it.assignedTo == SeedData.localMember.name }
                .sortedWith(compareBy({ it.date }, { it.startTime }))
        )
    }

    /** The activities of the home, observable by the composition. */
    val activities: List<ScheduledActivity> get() = mutableActivities

    /** Where the card walk through currently stands; starts on the activity nearest to the real current time. */
    var currentIndex by mutableIntStateOf(initialNearestIndex())
        private set

    // Together with [pendingActivityId] this guards the applied result from being overwritten by a late send callback.
    /** True from the moment a request leaves the watch until the phone answers or sending fails. */
    var awaitingResult by mutableStateOf(false)
        private set

    /** Which activity that request belongs to, so an answer for another one cannot unblock it. */
    var pendingActivityId by mutableStateOf<String?>(null)
        private set

    /** Last answer from the phone, accepted or rejected, or null once it has been consumed. */
    var lastResult by mutableStateOf<ActivityResult?>(null)
        private set

    /** Last send failure, or null when the last attempt reached the phone. */
    var lastError by mutableStateOf<SendFailure?>(null)
        private set

    /** Last requested transition, kept so the failure screen can offer to retry it. */
    var lastRequest by mutableStateOf<TransitionRequest?>(null)
        private set

    // While a result is on screen its activity stays pinned here, so the message is never read over the wrong one.
    /** Next activity still awaiting a decision, searched forward; null means the day is fully resolved. */
    val currentActivity: ScheduledActivity?
        get() {
            val pinnedId = lastResult?.activityId ?: pendingActivityId
            val pinned = pinnedId?.let { id -> mutableActivities.firstOrNull { it.id == id } }
            return pinned ?: nextPendingIndex(currentIndex)?.let { mutableActivities[it] }
        }

    // First half of the late-callback guard; it also keeps a double tap from asking the phone twice.
    /** Opens a request for [activityId], or answers false when one is already in flight. */
    fun beginSend(activityId: String, targetState: ActivityState): Boolean {
        if (awaitingResult) {
            Log.d(TAG, "Ignored request for $activityId: one is already in flight")
            return false
        }
        awaitingResult = true
        pendingActivityId = activityId
        lastRequest = TransitionRequest(activityId, targetState)
        lastResult = null
        lastError = null
        return true
    }

    // Second half of the guard: a send callback arriving after the answer finds the request closed and writes nothing.
    /** Notes that the Data Layer took the request. */
    fun markSendDelivered(activityId: String) {
        if (!awaitingResult || pendingActivityId != activityId) {
            Log.d(TAG, "Late send callback for $activityId: the answer was already applied")
            return
        }
        lastError = null
    }

    // The reported state is written on rejection too, which is how a drifted watch view corrects itself.
    /** Applies an answer of the phone to the local copy. */
    fun applyResult(result: ActivityResult) {
        val index = mutableActivities.indexOfFirst { it.id == result.activityId }
        if (index < 0) {
            // The phone knows an activity this build was not seeded with: nothing to write.
            Log.w(TAG, "Answer for unknown local activity ${result.activityId}")
        } else {
            val current = mutableActivities[index]
            mutableActivities[index] = current.copy(
                state = result.resultingState,
                resolvedBy = result.actorName ?: current.resolvedBy,
                resolvedAtMillis = result.resolvedAtMillis ?: current.resolvedAtMillis
            )
        }
        lastResult = result
        lastError = null
        awaitingResult = false
        pendingActivityId = null
    }

    /** Records a send failure; the displayed state is untouched, since nothing was decided. */
    fun reportFailure(failure: SendFailure) {
        lastError = failure
        awaitingResult = false
        pendingActivityId = null
    }

    // Searching from the next index avoids stopping forever on a confirmed event, which is not terminal yet.
    /** Moves on to the next pending activity, restarting from 0 when nothing is left further down the day. */
    fun advanceToNextPending() {
        val resolvedId = lastResult?.activityId
        val resolvedIndex = resolvedId
            ?.let { id -> mutableActivities.indexOfFirst { it.id == id } }
            ?.takeIf { it >= 0 }
            ?: currentIndex
        currentIndex = nextPendingIndex(resolvedIndex + 1)
            ?: nextPendingIndex(0)
            ?: mutableActivities.size
    }

    /** Drops the answer already read by the user, so it cannot be shown again. */
    fun clearLastResult() {
        lastResult = null
    }

    /** Drops the outcome just read and advances only if it left the activity resolved: the way out of a refusal. */
    fun dismissOutcome() {
        val pinnedId = lastResult?.activityId
        val resolved = pinnedId
            ?.let { id -> mutableActivities.firstOrNull { it.id == id } }
            ?.let { TransitionRules.isTerminal(it.state) } == true
        if (resolved) {
            advanceToNextPending()
        }
        lastResult = null
        lastError = null
    }

    private fun nextPendingIndex(from: Int): Int? {
        for (index in from.coerceAtLeast(0) until mutableActivities.size) {
            if (!TransitionRules.isTerminal(mutableActivities[index].state)) {
                return index
            }
        }
        return null
    }

    // The list is already in chronological order, so the first upcoming non-terminal activity is the nearest one.
    /** Index of the soonest non-terminal activity still ahead of now; falls back to the first non-terminal one. */
    private fun initialNearestIndex(): Int {
        val now = LocalDateTime.now()
        val upcoming = mutableActivities.indexOfFirst {
            !TransitionRules.isTerminal(it.state) && !it.date.atTime(it.startTime).isBefore(now)
        }
        if (upcoming >= 0) return upcoming
        val anyPending = mutableActivities.indexOfFirst { !TransitionRules.isTerminal(it.state) }
        return anyPending.coerceAtLeast(0)
    }

    private const val TAG = "ArgosHomeStore"
}
