package com.example.argos_home.ui.household

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.HouseholdRepository
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Quaternary200
import com.example.argos_home.ui.theme.White50

private enum class InviteTab { QR, EMAIL }

/** Invite member submodule: a segmented control over the QR and By-email tabs, both sharing the pending list. */
@Composable
fun InviteMemberScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    var tab by remember { mutableStateOf(InviteTab.QR) }
    val invitations = HouseholdRepository.invitations
    val inviteCode = HouseholdRepository.inviteCode

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubmoduleHeader(title = stringResource(R.string.invite_member_title), onBack = onBack)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            InviteTabSelector(selected = tab, onSelect = { tab = it })
            Spacer(Modifier.height(16.dp))
            when (tab) {
                InviteTab.QR -> QrInviteTab(
                    householdName = HouseholdRepository.householdName,
                    inviteCode = inviteCode,
                    invitations = invitations,
                    onRemoveInvitation = { HouseholdRepository.removeInvitation(it.id) }
                )

                InviteTab.EMAIL -> EmailInviteTab(
                    invitations = invitations,
                    onSend = { HouseholdRepository.addInvitation(it) },
                    onRemoveInvitation = { HouseholdRepository.removeInvitation(it.id) }
                )
            }
        }
    }
}

/** Two-pill segmented control; the active tab is filled primary with white text. */
@Composable
private fun InviteTabSelector(selected: InviteTab, onSelect: (InviteTab) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Quaternary200)
            .padding(4.dp)
    ) {
        TabPill(
            label = stringResource(R.string.tab_qr_code),
            active = selected == InviteTab.QR,
            onClick = { onSelect(InviteTab.QR) },
            modifier = Modifier.weight(1f)
        )
        TabPill(
            label = stringResource(R.string.tab_by_email),
            active = selected == InviteTab.EMAIL,
            onClick = { onSelect(InviteTab.EMAIL) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun TabPill(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (active) Primary600 else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            color = if (active) White50 else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
