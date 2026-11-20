package com.example.argos_home.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.NotificationRepository
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.White50

// Shared bell + unread badge; the badge sits outside the icon's circular clip so the count is never cut off.
@Composable
fun NotificationBellBadge(onBell: () -> Unit, modifier: Modifier = Modifier) {
    val unread = NotificationRepository.notifications.count { !it.read }
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        // Only the icon is clipped to a circle for the ripple; keeping the badge out of that clip stops the cut-off.
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .clickable(onClick = onBell),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_bell),
                contentDescription = stringResource(R.string.notifications_description),
                tint = White50,
                modifier = Modifier.size(24.dp)
            )
        }
        if (unread > 0) {
            // Drawn on top of the bell, centered in its own circle with symmetric padding so the digit stays whole.
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
                    .defaultMinSize(minWidth = 16.dp, minHeight = 16.dp)
                    .clip(CircleShape)
                    .background(Error1)
                    .padding(horizontal = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (unread > 9) "9+" else unread.toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = White50,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
