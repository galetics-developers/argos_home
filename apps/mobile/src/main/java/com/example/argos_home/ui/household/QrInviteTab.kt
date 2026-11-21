package com.example.argos_home.ui.household

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argos_home.R
import com.example.argos_home.data.HouseholdRepository
import com.example.argos_home.domain.INVITE_VALIDITY_SECONDS
import com.example.argos_home.domain.Invitation
import com.example.argos_home.domain.InviteCode
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary50
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Primary700
import com.example.argos_home.ui.theme.White50
import kotlinx.coroutines.delay

/** QR tab: scannable QR, 6-digit code, a 10-minute countdown that regenerates, share link and pending invites. */
@Composable
fun QrInviteTab(
    householdName: String,
    inviteCode: InviteCode,
    invitations: List<Invitation>,
    onRemoveInvitation: (Invitation) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val shareLabel = stringResource(R.string.share_invite_link)

    // Counts down from ten minutes; reaching zero regenerates the code, which restarts this effect.
    var remaining by remember { mutableIntStateOf(INVITE_VALIDITY_SECONDS) }
    LaunchedEffect(inviteCode.code) {
        remaining = INVITE_VALIDITY_SECONDS
        while (remaining > 0) {
            delay(1000)
            remaining -= 1
        }
        HouseholdRepository.regenerateCode()
    }
    val timeText = "%02d:%02d".format(remaining / 60, remaining % 60)

    Column(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(White50)
                .border(1.dp, Primary100, RoundedCornerShape(20.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            QrImage(content = inviteCode.link, sizeDp = 180)
            Spacer(Modifier.height(12.dp))
            Text(
                text = stringResource(R.string.scan_to_join, householdName),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Primary50)
                    .border(1.dp, Primary100, RoundedCornerShape(14.dp))
                    .padding(vertical = 14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(R.string.six_digit_code),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = inviteCode.code.toCharArray().joinToString("  "),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp,
                    color = Primary700
                )
            }
            Spacer(Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Icon(
                    painter = painterResource(R.drawable.ic_clock),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = stringResource(R.string.expires_in, timeText),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(Modifier.height(16.dp))
            PressFillButton(
                onClick = {
                    val send = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, inviteCode.link)
                    }
                    runCatching { context.startActivity(Intent.createChooser(send, shareLabel)) }
                },
                fillColor = Primary600,
                modifier = Modifier.fillMaxWidth()
            ) { contentColor ->
                Icon(
                    painter = painterResource(R.drawable.ic_share),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.share_invite_link),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
        Spacer(Modifier.height(20.dp))
        PendingInvitations(invitations = invitations, onRemove = onRemoveInvitation)
    }
}
