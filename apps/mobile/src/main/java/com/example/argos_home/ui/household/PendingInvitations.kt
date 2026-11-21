package com.example.argos_home.ui.household

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import com.example.argos_home.domain.Invitation
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.Secondary50

/** "Pending invitations" list; shared by both invite tabs. */
@Composable
fun PendingInvitations(
    invitations: List<Invitation>,
    onRemove: (Invitation) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        SectionTitle(iconRes = R.drawable.ic_clock, title = stringResource(R.string.pending_invitations))
        Spacer(Modifier.height(12.dp))
        invitations.forEach { invitation ->
            InvitationRow(invitation = invitation, onRemove = { onRemove(invitation) })
            Spacer(Modifier.height(10.dp))
        }
    }
}

/** One pending invitation: envelope, email, the "Pending" status (text, not color alone) and a remove X. */
@Composable
private fun InvitationRow(invitation: Invitation, onRemove: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, Primary100, RoundedCornerShape(14.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(Secondary50),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_mail),
                contentDescription = null,
                tint = Secondary500,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = invitation.email,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.height(2.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = Secondary500,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = stringResource(R.string.invite_status_pending),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Secondary500
                )
            }
        }
        IconButton(onClick = onRemove) {
            Icon(
                painter = painterResource(R.drawable.ic_x),
                contentDescription = stringResource(R.string.remove_invitation),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
