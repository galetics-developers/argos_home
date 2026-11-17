package com.example.argos_home.protocol

import com.example.argos_home.domain.ActivityState

/** Transition the watch asks for; the phone decides and answers with an [ActivityResult]. */
data class ActivityUpdate(
    val activityId: String,
    val targetState: ActivityState,
    val requestedAtMillis: Long,
    val memberId: String
)
