package com.example.argos_home.domain

/** Kind of notification; drives the representative icon of each card on both devices. */
enum class NotificationType { REMINDER, MEMBER_JOINED, INVITE, VACCINE_DUE, MEMBERSHIP, ADMIN_TRANSFER }

/** One notification shown to the user; grouped by day on the phone, with the household kept as a chip. */
data class Notification(
    val id: String,
    val type: NotificationType,
    val title: String,
    val message: String,
    val householdName: String,
    val timestampMillis: Long,
    val read: Boolean = false
) {
    companion object {
        const val MAX_HOUSEHOLDS = 8  // Product rule: a user belongs to at most this many households.
    }
}
