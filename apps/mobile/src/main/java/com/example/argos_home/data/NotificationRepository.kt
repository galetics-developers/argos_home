package com.example.argos_home.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.argos_home.domain.Notification
import com.example.argos_home.domain.NotificationType
import java.util.concurrent.TimeUnit

// One user can belong to several households, so each notification carries its own household name.
/** In-memory notifications of the user across their households; seeded and marked read in place. */
object NotificationRepository {

    private val items: SnapshotStateList<Notification> =
        mutableStateListOf<Notification>().apply { addAll(seed()) }

    /** Observable view of the notifications, newest first. */
    val notifications: List<Notification> get() = items.sortedByDescending { it.timestampMillis }

    /** Distinct household names present, for the "Filter by household" menu. */
    fun households(): List<String> = items.map { it.householdName }.distinct()

    fun markAllRead() {
        for (i in items.indices) if (!items[i].read) items[i] = items[i].copy(read = true)
    }

    // Timestamps are anchored to now so the relative time ("10m ago") stays live across days.
    private fun seed(): List<Notification> {
        val now = System.currentTimeMillis()
        fun ago(amount: Long, unit: TimeUnit): Long = now - unit.toMillis(amount)
        return listOf(
            Notification(
                id = "notif-01", type = NotificationType.REMINDER,
                title = "Reminder: Walk Boyka 4:00 PM", message = "Evening walk is due soon.",
                householdName = "Pérez Household", timestampMillis = ago(10, TimeUnit.MINUTES)
            ),
            Notification(
                id = "notif-02", type = NotificationType.MEMBER_JOINED,
                title = "Ana joined the household", message = "New household member.",
                householdName = "Pérez Household", timestampMillis = ago(2, TimeUnit.HOURS)
            ),
            Notification(
                id = "notif-03", type = NotificationType.INVITE,
                title = "Beach House invited you", message = "Tap to review the invitation.",
                householdName = "Beach House", timestampMillis = ago(4, TimeUnit.HOURS)
            ),
            Notification(
                id = "notif-04", type = NotificationType.VACCINE_DUE,
                title = "Boyka's rabies vaccine is due Oct 15", message = "Schedule the appointment.",
                householdName = "Pérez Household", timestampMillis = ago(1, TimeUnit.DAYS), read = true
            ),
            Notification(
                id = "notif-05", type = NotificationType.ADMIN_TRANSFER,
                title = "Ana is now the household administrator", message = "Administration was transferred.",
                householdName = "Pérez Household", timestampMillis = ago(1, TimeUnit.DAYS), read = true
            ),
            Notification(
                id = "notif-06", type = NotificationType.MEMBERSHIP,
                title = "You are now a member of this household", message = "Welcome to the household.",
                householdName = "Grandma Lucía's House", timestampMillis = ago(6, TimeUnit.DAYS), read = true
            )
        )
    }
}
