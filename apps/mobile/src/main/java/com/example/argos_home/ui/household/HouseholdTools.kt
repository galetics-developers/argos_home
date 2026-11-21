package com.example.argos_home.ui.household

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.MemberRole
import com.example.argos_home.domain.canManageMembers
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Secondary600

/** Household tools: admin sees Transfer + Invite + Leave; a member sees only Leave. */
@Composable
fun HouseholdTools(
    role: MemberRole,
    onTransfer: () -> Unit,
    onInvite: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAdmin = canManageMembers(role)
    Column(modifier = modifier.padding(horizontal = 20.dp)) {
        SectionTitle(
            iconRes = R.drawable.ic_settings,
            title = stringResource(if (isAdmin) R.string.admin_tools else R.string.members_tools)
        )
        Spacer(Modifier.height(12.dp))
        if (isAdmin) {
            ToolCard(
                icon = R.drawable.ic_arrow_left_right,
                title = R.string.tool_transfer_title,
                subtitle = R.string.tool_transfer_subtitle,
                onClick = onTransfer
            )
            Spacer(Modifier.height(12.dp))
            ToolCard(
                icon = R.drawable.ic_user_plus,
                title = R.string.tool_invite_title,
                subtitle = R.string.tool_invite_subtitle,
                onClick = onInvite
            )
            Spacer(Modifier.height(12.dp))
        }
        // Red-outlined at rest; fills Secondary with white content on press/hover (RF-20).
        PressFillButton(
            onClick = onLeave,
            fillColor = Secondary600,
            restContentColor = Error1,
            borderColor = Error1,
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) { contentColor ->
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Error1.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_log_out),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Text(
                text = stringResource(R.string.leave_household),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = contentColor,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

/** A tool row: circular icon, title + subtitle and a trailing chevron. */
@Composable
private fun ToolCard(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    @StringRes subtitle: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Primary100, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Primary100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                tint = Primary600,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Icon(
            painter = painterResource(R.drawable.ic_chevron_right),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
    }
}
