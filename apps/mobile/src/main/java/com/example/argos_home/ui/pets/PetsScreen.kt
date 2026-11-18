package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.PetRepository
import com.example.argos_home.domain.Household
import com.example.argos_home.ui.calendar.BottomNavBar
import com.example.argos_home.ui.calendar.Tab
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Pets list host: the household pets with their day progress, plus the add-pet entry point. */
@Composable
fun PetsScreen(
    onOpenPet: (String) -> Unit,
    onAddPet: () -> Unit,
    onOpenNotifications: () -> Unit,
    onSelectTab: (Tab) -> Unit,
    modifier: Modifier = Modifier
) {
    val pets = PetRepository.pets
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = { PetsTopBar(count = pets.size, onBell = onOpenNotifications) },
        bottomBar = { BottomNavBar(selected = Tab.PETS, onSelect = onSelectTab) },
        // The branded bars own the system-bar insets, so the content adds none of its own.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = stringResource(R.string.pets_subtitle),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
            // Only the pet cards scroll; the add action stays fixed for quick access near the 30-pet limit.
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = pets, key = { it.id }) { pet ->
                    PetCard(pet = pet, onOpen = { onOpenPet(pet.id) })
                }
            }
            AddPetButton(
                onClick = onAddPet,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

/** Branded top bar; the purple fills behind the status bar so it merges with the phone chrome. */
@Composable
private fun PetsTopBar(count: Int, onBell: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary600)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.pets_title),
            style = MaterialTheme.typography.titleLarge,
            color = White50
        )
        Spacer(modifier = Modifier.size(10.dp))
        Text(
            text = stringResource(R.string.pets_count, count, Household.MAX_PETS),
            style = MaterialTheme.typography.labelMedium,
            color = White50,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(White50.copy(alpha = 0.2f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Icon(
            painter = painterResource(R.drawable.ic_bell),
            contentDescription = stringResource(R.string.notifications_title),
            tint = White50,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .clickable(onClick = onBell)
                .padding(4.dp)
        )
    }
}

/** Full-width "Add pet" action, pinned under the list. */
@Composable
private fun AddPetButton(onClick: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Primary600)
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_plus),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.add_pet),
            style = MaterialTheme.typography.labelLarge,
            color = White50
        )
    }
}
