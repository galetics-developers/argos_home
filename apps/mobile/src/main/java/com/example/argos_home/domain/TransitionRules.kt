package com.example.argos_home.domain

import com.example.argos_home.protocol.RejectionReason

/** Outcome of checking a requested transition. */
sealed interface TransitionCheck {

    data object Allowed : TransitionCheck

    /** The reason travels back to the requester so it can explain the refusal. */
    data class Rejected(val reason: RejectionReason) : TransitionCheck
}

/** Pure rules: no stored state, no Android types, no clock, so phone decision and watch enablement cannot drift. */
object TransitionRules {

    /** DONE and CANCELED are final: an activity in one of them is already resolved. */
    fun isTerminal(state: ActivityState): Boolean = when (state) {
        ActivityState.DONE, ActivityState.CANCELED -> true
        ActivityState.PENDING, ActivityState.CONFIRMED -> false
    }

    /** Targets each kind offers; tasks and events share one set: CONFIRMED, DONE and CANCELED. */
    fun isAllowedForType(type: ItemType, target: ActivityState): Boolean = when (type) {
        ItemType.TASK, ItemType.EVENT ->
            target == ActivityState.CONFIRMED ||
                target == ActivityState.DONE ||
                target == ActivityState.CANCELED
    }

    /** Checks the terminal state before the per kind matrix, so a DONE on a resolved activity answers ALREADY_DONE. */
    fun validate(current: ActivityState, type: ItemType, target: ActivityState): TransitionCheck =
        when {
            isTerminal(current) -> TransitionCheck.Rejected(
                if (current == ActivityState.DONE) {
                    RejectionReason.ALREADY_DONE
                } else {
                    RejectionReason.ALREADY_CANCELED
                }
            )

            !isAllowedForType(type, target) ->
                TransitionCheck.Rejected(RejectionReason.INVALID_TRANSITION)

            !isReachable(current, target) ->
                TransitionCheck.Rejected(RejectionReason.INVALID_TRANSITION)

            else -> TransitionCheck.Allowed
        }

    // Drives which action buttons appear on both devices: only legal transitions show, so a terminal activity shows none.
    /** Transitions currently offered for an activity, in CANCELED, DONE, CONFIRMED order; empty when it is resolved. */
    fun allowedTargets(current: ActivityState, type: ItemType): List<ActivityState> =
        BUTTON_ORDER.filter { validate(current, type, it) is TransitionCheck.Allowed }

    /** Label of a pending activity; it depends on the kind and not on the clock, because the seeded day is fixed. */
    fun pendingLabel(type: ItemType): String = when (type) {
        ItemType.TASK -> "Due soon"
        ItemType.EVENT -> "Pending"
    }

    /** Button order shared by both devices: cancel, done, confirm. */
    private val BUTTON_ORDER = listOf(ActivityState.CANCELED, ActivityState.DONE, ActivityState.CONFIRMED)

    /** Transitions of the state machine itself, independent of the kind of activity. */
    private fun isReachable(current: ActivityState, target: ActivityState): Boolean =
        when (current) {
            ActivityState.PENDING -> target != ActivityState.PENDING
            ActivityState.CONFIRMED ->
                target == ActivityState.DONE || target == ActivityState.CANCELED

            ActivityState.DONE, ActivityState.CANCELED -> false
        }
}
