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
            date = "Today"
        ),
        Notification(
            id = "wear-notif-02",
            type = NotificationType.REMINDER,
            title = "Walk reminder",
            message = "Milo walk at 5:00 PM",
            time = "4:45 PM",
            date = "Today"
        ),
        Notification(
            id = "wear-notif-03",
            type = NotificationType.MEMBER_JOINED,
            title = "Ana joined",
            message = "New household member",
            time = "8:15 AM",
            date = "Yesterday"
        ),
        Notification(
            id = "wear-notif-04",
            type = NotificationType.ADMIN_TRANSFER,
            title = "Admin transfer",
            message = "Ana is now administrator",
            time = "6:40 PM",
            date = "Yesterday"
        ),
        Notification(
            id = "wear-notif-05",
            type = NotificationType.INVITE,
            title = "Beach House invite",
            message = "Tap to review the invitation",
            time = "11:20 AM",
            date = "Sep 10"
        ),
        Notification(
            id = "wear-notif-06",
            type = NotificationType.MEMBERSHIP,
            title = "Household linked",
            message = "Pérez's household",
            time = "2:00 PM",
            date = "Sep 8"
        )
    )
}
