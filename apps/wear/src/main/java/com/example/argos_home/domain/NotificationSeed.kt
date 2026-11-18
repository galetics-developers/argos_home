package com.example.argos_home.domain

// Notifications do not cross the Data Layer, so the watch keeps its own local seed, like [SeedData].
/** Static glanceable notifications of the watch, coherent in names with the phone seed. */
object NotificationSeed {

    val notifications: List<Notification> = listOf(
        Notification(
            id = "wear-notif-01",
            type = NotificationType.VACCINE_DUE,
            title = "Boyka Sanchez's vaccine",
            message = "Rabies due today",
            time = "9:00 AM",
            detail = "9:00 AM - 12:00 PM"
        ),
        Notification(
            id = "wear-notif-02",
            type = NotificationType.MEMBER_JOINED,
            title = "Ana joined",
            message = "New household member",
            time = "Yesterday"
        ),
        Notification(
            id = "wear-notif-03",
            type = NotificationType.ADMIN_TRANSFER,
            title = "Admin transfer",
            message = "Ana is now administrator",
            time = "Yesterday"
        ),
        Notification(
            id = "wear-notif-04",
            type = NotificationType.MEMBERSHIP,
            title = "Household linked",
            message = "Pérez's household",
            time = "2 days ago"
        )
    )
}
