package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.Pet
import com.example.argos_home.ui.calendar.petIconOf
import com.example.argos_home.ui.calendar.petTypeColor
import com.example.argos_home.ui.calendar.petTypeColorSoft
import com.example.argos_home.ui.theme.White50

/** One pet of the list: type-colored avatar, name, "type · breed", a chevron, and the day's progress bar. */
@Composable
fun PetCard(pet: Pet, onOpen: () -> Unit, modifier: Modifier = Modifier) {
    val color = petTypeColor(pet.type)
    val progress = petDayProgress(pet.name)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(petTypeColorSoft(pet.type))
            .clickable(role = Role.Button, onClick = onOpen)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(petIconOf(pet.type)),
                    contentDescription = null,
                    tint = White50,
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pet.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1
                )
                Text(
                    text = petSubtitle(pet),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.open_pet),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            LinearProgressIndicator(
                progress = { progress.fraction },
                color = color,
                trackColor = color.copy(alpha = 0.25f),
                modifier = Modifier
                    .weight(1f)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
            )
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = stringResource(R.string.pet_progress, progress.done, progress.total),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/** "type · breed" when a breed is known, otherwise just the type. */
@Composable
private fun petSubtitle(pet: Pet): String {
    val type = stringResource(petTypeLabel(pet.type))
    return if (pet.breed.isBlank()) type else "$type · ${pet.breed}"
}
