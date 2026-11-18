package com.example.argos_home.presentation.notifications

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.ScalingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberScalingLazyListState
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.argos_home.R
import com.example.argos_home.domain.Notification
import com.example.argos_home.domain.NotificationSeed
import com.example.argos_home.domain.NotificationType
import com.example.argos_home.presentation.home.ModuleIndicator
import com.example.argos_home.presentation.theme.Primary500
import com.example.argos_home.presentation.theme.Quaternary900
import com.example.argos_home.presentation.theme.Secondary500
import com.example.argos_home.presentation.theme.Success2
import com.example.argos_home.presentation.theme.Tertiary600
import com.example.argos_home.presentation.theme.White50

// Module 3 of the watch: a glanceable list of notifications, a focused detail, and a green empty state.
/** The Notifications module: the watch's local notifications, with a focused detail for the tapped one. */
@Composable
fun NotificationsScreen(modifier: Modifier = Modifier) {
    var selectedId by rememberSaveable { mutableStateOf<String?>(null) }
    val items = NotificationSeed.notifications
    val selected = selectedId?.let { id -> items.firstOrNull { it.id == id } }

    when {
        selected != null -> CenteredFrame(modifier) { scale ->
            NotificationDetail(selected, scale, onBack = { selectedId = null })
        }

        items.isEmpty() -> CenteredFrame(modifier) { scale -> NothingPending(scale) }

        else -> NotificationList(items = items, onOpen = { selectedId = it }, modifier = modifier)
    }
}

/** The notifications in a round-aware list that centers and scales its items to the watch screen. */
@Composable
private fun NotificationList(
    items: List<Notification>,
    onOpen: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    ScalingLazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        state = rememberScalingLazyListState(),
        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = stringResource(R.string.notifications_title),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
        }
        items(items = items, key = { it.id }) { notification ->
            NotificationRow(notification = notification, onOpen = onOpen)
        }
        item { ModuleIndicator(activeIndex = 2, scale = 1f) }
    }
}

/** A compact notification card: colored type icon, title, message and time; tapping it opens the detail. */
@Composable
private fun NotificationRow(notification: Notification, onOpen: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(role = Role.Button, onClick = { onOpen(notification.id) })
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TypeBadge(notification.type, size = 34.dp, iconSize = 18.dp)
        Spacer(modifier = Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1
            )
            Text(
                text = notification.message,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = notification.time,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

/** The focused detail: the icon, title, message and extra line, all centered on the round screen. */
@Composable
private fun NotificationDetail(notification: Notification, scale: Float, onBack: () -> Unit) {
    Spacer(modifier = Modifier.height((20f * scale).dp))
    TypeBadge(notification.type, size = (56f * scale).dp, iconSize = (30f * scale).dp)
    Spacer(modifier = Modifier.height((10f * scale).dp))
    Text(
        text = notification.title,
        style = MaterialTheme.typography.titleSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = (12f * scale).dp)
    )
    Text(
        text = notification.message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = (12f * scale).dp)
    )
    val footer = if (notification.detail.isNotBlank()) notification.detail else notification.time
    Spacer(modifier = Modifier.height((6f * scale).dp))
    Text(
        text = footer,
        style = MaterialTheme.typography.numeralSmall,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height((10f * scale).dp))
    Text(
        text = stringResource(R.string.notifications_back_description),
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.primary,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(role = Role.Button, onClick = onBack)
            .padding(vertical = (4f * scale).dp)
    )
    ModuleIndicator(activeIndex = 2, scale = scale)
    Spacer(modifier = Modifier.height((16f * scale).dp))
}

/** The empty state: a green check tells the household that nothing is waiting. */
@Composable
private fun NothingPending(scale: Float) {
    Spacer(modifier = Modifier.height((40f * scale).dp))
    Box(
        modifier = Modifier
            .size((56f * scale).dp)
            .clip(CircleShape)
            .background(Success2),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size((28f * scale).dp)
        )
    }
    Spacer(modifier = Modifier.height((10f * scale).dp))
    Text(
        text = stringResource(R.string.nothing_pending_title),
        style = MaterialTheme.typography.titleMedium,
        color = Success2,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = stringResource(R.string.nothing_pending_message),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(horizontal = (16f * scale).dp)
    )
    Spacer(modifier = Modifier.height((12f * scale).dp))
    ModuleIndicator(activeIndex = 2, scale = scale)
    Spacer(modifier = Modifier.height((16f * scale).dp))
}

/** Circular badge with the notification type icon in its accent color. */
@Composable
private fun TypeBadge(type: NotificationType, size: androidx.compose.ui.unit.Dp, iconSize: androidx.compose.ui.unit.Dp) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(colorOf(type)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconOf(type)),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(iconSize)
        )
    }
}

/** Scales a single-screen state to the real screen width and lets it scroll, as the Home module does. */
@Composable
private fun CenteredFrame(modifier: Modifier = Modifier, content: @Composable (scale: Float) -> Unit) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        val scale = maxWidth / DESIGN_FRAME_WIDTH.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            content(scale)
        }
    }
}

@DrawableRes
private fun iconOf(type: NotificationType): Int = when (type) {
    NotificationType.REMINDER -> R.drawable.ic_bell
    NotificationType.MEMBER_JOINED -> R.drawable.ic_user_plus
    NotificationType.INVITE -> R.drawable.ic_mail
    NotificationType.VACCINE_DUE -> R.drawable.ic_syringe
    NotificationType.MEMBERSHIP -> R.drawable.ic_shield
    NotificationType.ADMIN_TRANSFER -> R.drawable.ic_shield
}

private fun colorOf(type: NotificationType): Color = when (type) {
    NotificationType.REMINDER -> Primary500
    NotificationType.MEMBER_JOINED -> Primary500
    NotificationType.INVITE -> Tertiary600
    NotificationType.VACCINE_DUE -> Secondary500
    NotificationType.MEMBERSHIP -> Tertiary600
    NotificationType.ADMIN_TRANSFER -> Quaternary900
}

private const val DESIGN_FRAME_WIDTH = 224f
