package com.example.argos_home.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.UserProfile
import com.example.argos_home.domain.displayName
import com.example.argos_home.domain.userInitials
import com.example.argos_home.ui.components.NotificationBellBadge
import com.example.argos_home.ui.home.userAvatarColor
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.White50

/** Profile top bar: large initials avatar with an edit pencil, the name, the email, the active home pill and the bell. */
@Composable
fun ProfileHeader(
    profile: UserProfile,
    activeHousehold: String,
    onEdit: () -> Unit,
    onOpenSwitch: () -> Unit,
    onBell: () -> Unit,
    modifier: Modifier = Modifier
) {
    val name = displayName(profile)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Primary600)
            .clipToBounds()
            // Soft decorative circles behind the content, clipped to the header bounds.
            .drawBehind {
                drawCircle(White50.copy(alpha = 0.08f), radius = size.width * 0.28f, center = Offset(size.width * 0.9f, size.height * 0.12f))
                drawCircle(White50.copy(alpha = 0.06f), radius = size.width * 0.16f, center = Offset(size.width * 0.12f, size.height * 0.62f))
                drawCircle(White50.copy(alpha = 0.05f), radius = size.width * 0.1f, center = Offset(size.width * 0.78f, size.height * 0.72f))
            }
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top row keeps the bell to the right while the avatar stays centered below.
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            NotificationBellBadge(onBell = onBell)
        }

        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(userAvatarColor(name)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = userInitials(name),
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = White50
                )
            }
            // Small crimson circle with a white pencil; a double tap opens Edit profile.
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(White50)
                    .padding(2.dp)
                    .clip(CircleShape)
                    .background(Secondary500)
                    .clickable(onClick = onEdit),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_pencil),
                    contentDescription = stringResource(R.string.edit_profile),
                    tint = White50,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(10.dp))
        Text(
            text = name,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = White50,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(2.dp))
        Text(
            text = profile.email,
            style = MaterialTheme.typography.bodyMedium,
            color = White50.copy(alpha = 0.85f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Spacer(Modifier.height(12.dp))
        HouseholdPill(name = activeHousehold, onClick = onOpenSwitch)
    }
}

/** The active home as a pill; a tap opens the "Switch household" modal (same look as Home). */
@Composable
private fun HouseholdPill(name: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(White50.copy(alpha = 0.16f))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp),
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
            fontWeight = FontWeight.SemiBold,
            color = White50,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = 200.dp)
        )
        Icon(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = stringResource(R.string.switch_household),
            tint = White50,
            modifier = Modifier.size(16.dp)
        )
    }
}
