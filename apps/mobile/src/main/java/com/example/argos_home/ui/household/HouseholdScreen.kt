package com.example.argos_home.ui.household

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.HouseholdRepository
import com.example.argos_home.domain.HouseholdMemberUi
import com.example.argos_home.domain.canManageMembers
import com.example.argos_home.ui.calendar.BottomNavBar
import com.example.argos_home.ui.calendar.Tab

/** Household main view: fixed header + tools, with only the member cards scrolling, and the Remove/Leave dialogs. */
@Composable
fun HouseholdScreen(
    onOpenInvite: () -> Unit,
    onOpenTransfer: () -> Unit,
    onViewSchedule: () -> Unit,
    onSelectTab: (Tab) -> Unit,
    modifier: Modifier = Modifier
) {
    val members = HouseholdRepository.members
    val localId = HouseholdRepository.localMemberId
    val localRole = HouseholdRepository.localRole
    var memberToRemove by remember { mutableStateOf<HouseholdMemberUi?>(null) }
    var showLeave by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(selected = Tab.HOUSEHOLD, onSelect = onSelectTab) },
        // The branded header owns the status-bar inset itself, so the scaffold adds none of its own.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HouseholdHeader(
                householdName = HouseholdRepository.householdName,
                color = HouseholdRepository.color,
                memberCount = members.size,
                maxMembers = HouseholdRepository.maxMembers,
                localRole = localRole,
                onInvite = onOpenInvite,
                onViewSchedule = onViewSchedule
            )
            Spacer(Modifier.height(16.dp))
            // Members title stays fixed; only the cards below it scroll (a growing home list never pushes the tools away).
            SectionTitle(
                iconRes = R.drawable.ic_users,
                title = stringResource(R.string.members_section),
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(12.dp))
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(members, key = { it.id }) { member ->
                    MemberCard(
                        member = member,
                        isLocal = member.id == localId,
                        canRemove = canManageMembers(localRole) && member.id != localId,
                        onRemove = { memberToRemove = member }
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            // Tools stay fixed at the bottom, above the navigation bar.
            HouseholdTools(
                role = localRole,
                onTransfer = onOpenTransfer,
                onInvite = onOpenInvite,
                onLeave = { showLeave = true }
            )
            Spacer(Modifier.height(16.dp))
        }
    }

    memberToRemove?.let { member ->
        RemoveMemberDialog(
            memberName = member.name,
            householdName = HouseholdRepository.householdName,
            onConfirm = {
                HouseholdRepository.removeMember(member.id)
                memberToRemove = null
            },
            onDismiss = { memberToRemove = null }
        )
    }

    if (showLeave) {
        LeaveHouseholdDialog(
            householdName = HouseholdRepository.householdName,
            onConfirm = {
                HouseholdRepository.leave()
                showLeave = false
                onSelectTab(Tab.HOME)
            },
            onDismiss = { showLeave = false }
        )
    }
}
