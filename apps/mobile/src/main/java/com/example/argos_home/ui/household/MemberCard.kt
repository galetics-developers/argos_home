package com.example.argos_home.ui.household

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.HouseholdMemberUi
import com.example.argos_home.domain.userInitials
import com.example.argos_home.ui.home.userAvatarColor
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Tertiary50
import com.example.argos_home.ui.theme.Tertiary200
import com.example.argos_home.ui.theme.White50

/** Initials avatar reused from Home: deterministic color by initials, white glyph. */
@Composable
fun MemberAvatar(name: String, modifier: Modifier = Modifier, size: Int = 44) {
    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(userAvatarColor(name)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = userInitials(name),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = White50
        )
    }
}

/** One member row: avatar, full name (with "(you)" for the local user), role badge and an admin-only remove icon. */
@Composable
fun MemberCard(
    member: HouseholdMemberUi,
    isLocal: Boolean,
    canRemove: Boolean,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    val background = if (isLocal) Tertiary50 else White50
    val border = if (isLocal) Tertiary200 else Primary100
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(background)
            .border(1.dp, border, RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberAvatar(name = member.name)
        Spacer(Modifier.width(12.dp))
        androidx.compose.foundation.layout.Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = member.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (isLocal) {
                    Text(
                        text = stringResource(R.string.you_label),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.size(6.dp))
            RoleBadge(member.role)
        }
        if (canRemove) {
            IconButton(onClick = onRemove) {
                Icon(
                    painter = painterResource(R.drawable.ic_user_minus),
                    contentDescription = stringResource(R.string.remove_member),
                    tint = Error1,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}
