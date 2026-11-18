package com.example.argos_home.ui.notifications

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.argos_home.R
import com.example.argos_home.domain.NotificationType
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Info3
import com.example.argos_home.ui.theme.Primary500
import com.example.argos_home.ui.theme.Quaternary900
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.Tertiary600

/** Representative icon of each notification type. */
@DrawableRes
fun notificationIcon(type: NotificationType): Int = when (type) {
    NotificationType.REMINDER -> R.drawable.ic_bell
    NotificationType.MEMBER_JOINED -> R.drawable.ic_user_plus
    NotificationType.INVITE -> R.drawable.ic_mail
    NotificationType.VACCINE_DUE -> R.drawable.ic_syringe
    NotificationType.MEMBERSHIP -> R.drawable.ic_shield
    NotificationType.ADMIN_TRANSFER -> R.drawable.ic_shield
}

/** Accent color of each notification type, used for the icon badge; the text label always accompanies it. */
fun notificationColor(type: NotificationType): Color = when (type) {
    NotificationType.REMINDER -> Primary500
    NotificationType.MEMBER_JOINED -> Secondary500
    NotificationType.INVITE -> Tertiary600
    NotificationType.VACCINE_DUE -> Error1
    NotificationType.MEMBERSHIP -> Info3
    NotificationType.ADMIN_TRANSFER -> Quaternary900
}
