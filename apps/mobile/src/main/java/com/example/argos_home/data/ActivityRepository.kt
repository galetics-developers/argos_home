package com.example.argos_home.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.domain.TransitionCheck
import com.example.argos_home.domain.TransitionRules
import com.example.argos_home.protocol.ActivityResult
import com.example.argos_home.protocol.RejectionReason
import java.time.LocalDate

// Snapshot list so the calendar observes it directly and an accepted transition recomposes with no reload.
/** Authoritative store of the day on the phone; as an object it survives activity recreation. */
object ActivityRepository {

    private val items: SnapshotStateList<ScheduledActivity> =
        mutableStateListOf<ScheduledActivity>().apply { addAll(SeedData.activities) }

    /** Observable view of the day. Read only: mutation goes through [applyTransition] alone. */
    val activities: List<ScheduledActivity> get() = items

    fun findById(id: String): ScheduledActivity? = items.firstOrNull { it.id == id }

    /** Activities of one day, optionally narrowed to a pet, ordered by start time for the schedule list. */
    fun activitiesOn(date: LocalDate, petName: String? = null): List<ScheduledActivity> =
        items.filter { it.date == date && (petName == null || it.petName == petName) }
            .sortedBy { it.startTime }

    /** How many activities fall on each day of a month, for the month-grid dots; respects the pet filter. */
    fun countsByDay(year: Int, month: Int, petName: String? = null): Map<LocalDate, Int> =
        items.filter {
            it.date.year == year && it.date.monthValue == month && (petName == null || it.petName == petName)
        }.groupingBy { it.date }.eachCount()

    // New activities live only in the phone store: with no DataClient yet they do not reach the watch.
    /** Adds a user-created activity to the day; the calendar observes the list, so it appears at once. */
    fun addActivity(activity: ScheduledActivity) {
        items.add(activity)
    }

    // Returns the very object sent back over the Data Layer, carrying the real state so the watch can correct itself.
    /** The only function that mutates the day: card taps and watch messages both land here and cannot disagree. */
    fun applyTransition(
        id: String,
        target: ActivityState,
        resolvedAtMillis: Long
    ): ActivityResult {
        val index = items.indexOfFirst { it.id == id }
        if (index < 0) {
            // Only the store knows which ids exist, so UNKNOWN_ACTIVITY is the one reason the rules cannot produce.
            // PENDING is reported because there is no activity whose real state could be sent instead.
            return ActivityResult(
                activityId = id,
                accepted = false,
                resultingState = ActivityState.PENDING,
                reason = RejectionReason.UNKNOWN_ACTIVITY
            )
        }

        val current = items[index]
        when (val check = TransitionRules.validate(current.state, current.type, target)) {
            is TransitionCheck.Rejected -> return rejection(current, check.reason)
            TransitionCheck.Allowed -> Unit
        }

        // Every action is attributed to the local member: with no login, other members exist only as seed text.
        val resolved = current.copy(
            state = target,
            resolvedBy = SeedData.localMember.name,
            resolvedAtMillis = resolvedAtMillis
        )
        items[index] = resolved
        return ActivityResult(
            activityId = resolved.id,
            accepted = true,
            resultingState = resolved.state,
            actorName = resolved.resolvedBy,
            resolvedAtMillis = resolved.resolvedAtMillis
        )
    }

    /** Answer for a refused transition: writes nothing, so the stored day is left exactly as it was. */
    private fun rejection(
        activity: ScheduledActivity,
        reason: RejectionReason
    ): ActivityResult = ActivityResult(
        activityId = activity.id,
        accepted = false,
        resultingState = activity.state,
        reason = reason,
        actorName = activity.resolvedBy,
        resolvedAtMillis = activity.resolvedAtMillis
    )
}
