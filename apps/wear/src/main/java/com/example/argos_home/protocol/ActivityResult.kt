package com.example.argos_home.protocol

import com.example.argos_home.domain.ActivityState

/** Why the phone refused a requested transition. */
enum class RejectionReason {
    ALREADY_DONE,
    ALREADY_CANCELED,
    INVALID_TRANSITION,

    // Emitted by the phone repository when no activity matches the id, not by the transition rules.
    UNKNOWN_ACTIVITY;

    companion object {
        /** Returns null on an unknown name instead of throwing like valueOf: payloads are untrusted input. */
        fun fromName(value: String): RejectionReason? = entries.firstOrNull { it.name == value }
    }
}

/** Phone answer; [resultingState] is the state really stored, on rejection too, so the watch can correct itself. */
data class ActivityResult(
    val activityId: String,
    val accepted: Boolean,
    val resultingState: ActivityState,
    val reason: RejectionReason? = null,

    // Both are filled whenever the activity is already resolved, so the watch can say who completed it and when.
    val actorName: String? = null,
    val resolvedAtMillis: Long? = null
)
