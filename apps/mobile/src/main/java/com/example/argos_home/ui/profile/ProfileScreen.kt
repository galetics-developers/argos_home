package com.example.argos_home.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.argos_home.data.ProfileRepository
import com.example.argos_home.domain.HomeHouseholds
import com.example.argos_home.domain.ProfileHouseholds
import com.example.argos_home.domain.SeedData
import com.example.argos_home.ui.calendar.BottomNavBar
import com.example.argos_home.ui.calendar.Tab
import com.example.argos_home.ui.home.SwitchHouseholdSheet

/** Phone Profile host: a fixed account header over the scrolling counters, account settings and the home list. */
@Composable
fun ProfileScreen(
    activeHousehold: String,
    onSwitchHousehold: (String) -> Unit,
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onChangeEmail: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSignOut: () -> Unit,
    onSelectTab: (Tab) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSwitch by remember { mutableStateOf(false) }
    var showSignOut by remember { mutableStateOf(false) }

    val profile = ProfileRepository.profile
    val households = ProfileHouseholds.userHouseholds(activeHousehold)
    val role = ProfileHouseholds.roleIn(activeHousehold)
    val petCount = SeedData.pets.size

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(selected = Tab.PROFILE, onSelect = onSelectTab) },
        // The branded header owns the status-bar inset itself, so the scaffold adds none of its own.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            ProfileHeader(
                profile = profile,
                activeHousehold = activeHousehold,
                onEdit = onEditProfile,
                onOpenSwitch = { showSwitch = true },
                onBell = onOpenNotifications
            )
            // The header stays fixed; everything below scrolls as one column.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ProfileStats(
                    householdCount = households.size,
                    petCount = petCount,
                    role = role
                )
                AccountSettings(
                    onEditProfile = onEditProfile,
                    onChangePassword = onChangePassword,
                    onChangeEmail = onChangeEmail
                )
                MyHouseholds(
                    households = households,
                    onCreateOrJoin = { showSwitch = true },
                    onSignOut = { showSignOut = true }
                )
            }
        }
    }

    if (showSwitch) {
        SwitchHouseholdSheet(
            households = HomeHouseholds.all,
            activeName = activeHousehold,
            onSelect = {
                onSwitchHousehold(it)
                showSwitch = false
            },
            onDismiss = { showSwitch = false }
        )
    }

    if (showSignOut) {
        SignOutDialog(
            onConfirm = {
                showSignOut = false
                onSignOut()
            },
            onDismiss = { showSignOut = false }
        )
    }
}
