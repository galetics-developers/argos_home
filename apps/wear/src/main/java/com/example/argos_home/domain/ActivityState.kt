package com.example.argos_home.domain

/** Lifecycle of an activity; DONE and CANCELED are terminal and only the phone moves between states. */
enum class ActivityState {
    PENDING,
    CONFIRMED,
    DONE,
    CANCELED;

    companion object {
        /** Returns null on an unknown name instead of throwing like valueOf: payloads are untrusted input. */
        fun fromName(value: String): ActivityState? = entries.firstOrNull { it.name == value }
    }
}
