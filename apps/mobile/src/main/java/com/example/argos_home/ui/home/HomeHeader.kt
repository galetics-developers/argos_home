package com.example.argos_home.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.Greeting
import com.example.argos_home.domain.SeedData
import com.example.argos_home.domain.greetingFor
import com.example.argos_home.domain.userInitials
import com.example.argos_home.ui.components.NotificationBellBadge
import com.example.argos_home.ui.theme.Primary500
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Quaternary900
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.Tertiary600
import com.example.argos_home.ui.theme.White50
import java.time.LocalTime

/** Home top bar: initials avatar, time-based greeting, the active home as a tappable pill, and the shared bell. */
@Composable
fun HomeHeader(
    activeHousehold: String,
    onOpenSwitch: () -> Unit,
    onBell: () -> Unit,
    modifier: Modifier = Modifier,
    userName: String = SeedData.localMember.name,
    now: LocalTime = LocalTime.now()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary600)
            // The branded bar owns the status-bar inset, so the purple fills behind the system clock.
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(userAvatarColor(userName)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userInitials(userName),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = White50
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        // Takes the free space so a long name shrinks to an ellipsis instead of pushing the household pill off-screen.
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(greetingRes(greetingFor(now))),
                style = MaterialTheme.typography.bodyMedium,
                color = White50.copy(alpha = 0.85f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = White50,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        HouseholdPill(name = activeHousehold, onClick = onOpenSwitch)
        Spacer(modifier = Modifier.size(8.dp))
        NotificationBellBadge(onBell = onBell)
    }
}

/** The active home as a pill; a tap opens the "Switch household" modal. */
@Composable
private fun HouseholdPill(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(White50.copy(alpha = 0.16f))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_nav_household),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = name,
            style = MaterialTheme.typography.labelLarge,
            color = White50,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 120.dp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = stringResource(R.string.switch_household),
            tint = White50,
            modifier = Modifier.size(16.dp)
        )
    }
}

/** Greeting string for each part of the day. */
private fun greetingRes(greeting: Greeting): Int = when (greeting) {
    Greeting.MORNING -> R.string.greeting_morning
    Greeting.AFTERNOON -> R.string.greeting_afternoon
    Greeting.EVENING -> R.string.greeting_evening
}

// The four brand families in strong tones, so white initials stay legible on any of them.
private val AVATAR_COLORS: List<Color> = listOf(Primary500, Secondary500, Tertiary600, Quaternary900)

/** Avatar color chosen from the user's initials, so the same first-name/surname pair always maps to one color. */
fun userAvatarColor(fullName: String): Color {
    val code = userInitials(fullName).fold(0) { acc, c -> acc + c.code }
    val index = ((code % AVATAR_COLORS.size) + AVATAR_COLORS.size) % AVATAR_COLORS.size
    return AVATAR_COLORS[index]
}
