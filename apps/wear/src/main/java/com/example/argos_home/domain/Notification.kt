package com.example.argos_home.domain

/** Kind of notification shown on the watch; drives the representative icon of each card. */
enum class NotificationType { REMINDER, MEMBER_JOINED, INVITE, VACCINE_DUE, MEMBERSHIP, ADMIN_TRANSFER }

/** One glanceable notification on the watch; time and date are ready-made labels, never a live clock reading. */
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val time: String,
    val date: String = ""     // Relative day shown in the detail: "Today", "Yesterday" or "Sep 12".
)
