package com.example.argos_home.ui.household

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.HouseholdRepository
import com.example.argos_home.domain.HouseholdMemberUi
import com.example.argos_home.domain.otherMembers
import com.example.argos_home.ui.theme.Info1
import com.example.argos_home.ui.theme.Info3
import com.example.argos_home.ui.theme.Info4
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Transfer administration submodule: pick a member, confirm with the password, then roles swap. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TransferAdminScreen(onBack: () -> Unit, modifier: Modifier = Modifier) {
    val candidates = otherMembers(HouseholdRepository.members, HouseholdRepository.localMemberId)
    var selectedId by remember { mutableStateOf<String?>(null) }
    var confirmFor by remember { mutableStateOf<HouseholdMemberUi?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SubmoduleHeader(title = stringResource(R.string.transfer_admin_title), onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Info card: what the transfer does and that the password is required.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Info4.copy(alpha = 0.18f))
                    .border(1.dp, Info3, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_info),
                    contentDescription = null,
                    tint = Info1,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = stringResource(R.string.transfer_info),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Info1
                )
            }
            Spacer(Modifier.height(16.dp))
            candidates.forEach { member ->
                MemberPickRow(
                    member = member,
                    selected = selectedId == member.id,
                    onSelect = { selectedId = member.id },
                    onOpen = {
                        selectedId = member.id
                        confirmFor = member
                    }
                )
                Spacer(Modifier.height(12.dp))
            }
        }
        // Bottom action: opens the confirm modal for the selected member.
        Button(
            onClick = { confirmFor = candidates.firstOrNull { it.id == selectedId } },
            enabled = selectedId != null,
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Primary600,
                disabledContainerColor = Primary600.copy(alpha = 0.4f)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_arrow_left_right),
                contentDescription = null,
                tint = White50,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.transfer_administration),
                fontWeight = FontWeight.SemiBold,
                color = White50
            )
        }
    }

    confirmFor?.let { member ->
        ConfirmTransferDialog(
            memberName = member.name,
            onConfirm = { password ->
                if (HouseholdRepository.isPasswordCorrect(password)) {
                    HouseholdRepository.transferAdminTo(member.id)
                    true
                } else {
                    false
                }
            },
            onSuccess = {
                confirmFor = null
                onBack()
            },
            onDismiss = { confirmFor = null }
        )
    }
}

/** A selectable member: avatar, name and a radio; a double tap opens the confirm modal directly. */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MemberPickRow(
    member: HouseholdMemberUi,
    selected: Boolean,
    onSelect: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, Primary100, RoundedCornerShape(16.dp))
            .combinedClickable(onClick = onSelect, onDoubleClick = onOpen)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MemberAvatar(name = member.name, size = 40)
        Spacer(Modifier.width(12.dp))
        Text(
            text = member.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        RadioButton(selected = selected, onClick = onSelect)
    }
}
