package com.example.argos_home.domain

/** Kind of activity; the kind, not the clock, decides the offered transitions and the pending label. */
enum class ItemType {
    TASK,
    EVENT;

    companion object {
        /** Returns null on an unknown name instead of throwing like valueOf: payloads are untrusted input. */
        fun fromName(value: String): ItemType? = entries.firstOrNull { it.name == value }
    }
}
