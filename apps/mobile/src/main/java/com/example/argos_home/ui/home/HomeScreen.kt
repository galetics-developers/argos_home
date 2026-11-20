package com.example.argos_home.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.data.PetRepository
import com.example.argos_home.domain.HomeHouseholds
import com.example.argos_home.domain.HomeSummary
import com.example.argos_home.ui.calendar.BottomNavBar
import com.example.argos_home.ui.calendar.Tab
import com.example.argos_home.ui.theme.Gray200
import com.example.argos_home.ui.theme.Primary50
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary600
import java.time.LocalDate

// Reads the repository snapshot lists, so an activity change or a pet edit repaints the summary on its own.
/** Phone Home host: a fixed dashboard over the pet carousel and the single scrolling Upcoming list. */
@Composable
fun HomeScreen(
    onOpenNotifications: () -> Unit,
    onSelectTab: (Tab) -> Unit,
    onOpenPet: (String) -> Unit,
    onOpenActivity: (LocalDate) -> Unit,
    activeHousehold: String,
    onSwitchHousehold: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showSwitch by remember { mutableStateOf(false) }

    val pets = PetRepository.pets
    val summary = HomeSummary.of(ActivityRepository.activities, LocalDate.now())

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(selected = Tab.HOME, onSelect = onSelectTab) },
        // The branded header owns the status-bar inset itself, so the scaffold adds none of its own.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HomeHeader(
                activeHousehold = activeHousehold,
                onOpenSwitch = { showSwitch = true },
                onBell = onOpenNotifications
            )

            Spacer(modifier = Modifier.size(14.dp))
            // Ring and counters read as one bordered card: a tinted top over a plain counter row.
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Gray200, RoundedCornerShape(16.dp))
            ) {
                HomeProgress(summary = summary, modifier = Modifier.background(Primary50))
                HomeStats(summary = summary, modifier = Modifier.background(MaterialTheme.colorScheme.surface))
            }

            Spacer(modifier = Modifier.size(18.dp))
            CareByPetHeader(petCount = pets.size)

            Spacer(modifier = Modifier.size(12.dp))
            PetCarousel(pets = pets, onOpenProfile = onOpenPet)

            Spacer(modifier = Modifier.size(8.dp))
            UpcomingList(
                activities = summary.upcoming,
                onOpenActivity = { onOpenActivity(it.date) },
                modifier = Modifier.weight(1f)
            )
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
}

/** Section header for the pet carousel: a paw glyph, the title and subtitle, and the home's pet count. */
@Composable
private fun CareByPetHeader(petCount: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_nav_pets),
            contentDescription = null,
            tint = Primary600,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(R.string.home_care_by_pet),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = stringResource(R.string.home_care_by_pet_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.size(8.dp))
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(Primary100)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = stringResource(R.string.home_pets_count, petCount),
                style = MaterialTheme.typography.labelMedium,
                color = Primary600
            )
        }
    }
}
