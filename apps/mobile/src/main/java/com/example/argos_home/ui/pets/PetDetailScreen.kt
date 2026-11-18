package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.MedicalRecordRepository
import com.example.argos_home.data.PetRepository
import com.example.argos_home.domain.Pet
import com.example.argos_home.ui.calendar.petIconOf
import com.example.argos_home.ui.calendar.petTypeColor
import com.example.argos_home.ui.calendar.petTypeColorSoft
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Pet detail: a scrollable profile and tab content, with the pet actions pinned at the bottom. */
@Composable
fun PetDetailScreen(
    petId: String,
    onBack: () -> Unit,
    onEdit: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val pet = PetRepository.pets.firstOrNull { it.id == petId } ?: return
    var careSelected by remember { mutableStateOf(true) }
    var showDelete by remember { mutableStateOf(false) }
    var showRecordForm by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            DetailTopBar(onBack = onBack, onEdit = { onEdit(pet.id) })

            // Only the profile and the tab content scroll; the actions stay fixed below.
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                ProfileBlock(pet)
                MeasuresRow(pet)
                Spacer(modifier = Modifier.size(16.dp))
                PetTabs(
                    careSelected = careSelected,
                    onCare = { careSelected = true },
                    onHealth = { careSelected = false },
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
                Spacer(modifier = Modifier.size(16.dp))
                Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                    if (careSelected) CareScheduleTab(pet.name) else HealthRecordsTab(pet.id)
                }
                Spacer(modifier = Modifier.size(16.dp))
            }

            BottomActions(
                showAddRecord = !careSelected,
                onAddRecord = { showRecordForm = true },
                onDelete = { showDelete = true }
            )
        }
    }

    if (showDelete) {
        DeletePetDialog(
            petName = pet.name,
            onConfirm = {
                PetRepository.delete(pet.id)
                showDelete = false
                onBack()
            },
            onDismiss = { showDelete = false }
        )
    }

    if (showRecordForm) {
        MedicalRecordForm(
            petId = pet.id,
            onDismiss = { showRecordForm = false },
            onSave = { record ->
                MedicalRecordRepository.add(record)
                showRecordForm = false
            }
        )
    }
}

/** Branded detail bar with a back control and the edit entry point. */
@Composable
private fun DetailTopBar(onBack: () -> Unit, onEdit: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Primary600)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_chevron_left),
            contentDescription = stringResource(R.string.back),
            tint = White50,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onBack)
                .padding(2.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(
            text = stringResource(R.string.pet_profile_title),
            style = MaterialTheme.typography.titleMedium,
            color = White50,
            modifier = Modifier.weight(1f)
        )
        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = stringResource(R.string.edit_pet),
            tint = White50,
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .clickable(onClick = onEdit)
                .padding(3.dp)
        )
    }
}

/** Avatar in the type color over a soft tone, a static photo button, and the type and breed chips. */
@Composable
private fun ProfileBlock(pet: Pet) {
    val color = petTypeColor(pet.type)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds()
            .background(petTypeColorSoft(pet.type))
    ) {
        // Soft decorative circles behind the avatar, echoing the original header design.
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 28.dp, y = (-30).dp)
                .size(120.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-24).dp, y = 24.dp)
                .size(84.dp)
                .clip(CircleShape)
                .background(color.copy(alpha = 0.10f))
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .clip(CircleShape)
                        .background(color),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(petIconOf(pet.type)),
                        contentDescription = null,
                        tint = White50,
                        modifier = Modifier.size(48.dp)
                    )
                }
                // Static photo affordance: shown for fidelity but it does not pick an image in this release.
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Primary600),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_camera),
                        contentDescription = stringResource(R.string.pet_photo),
                        tint = White50,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text(
                text = pet.name,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.size(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip(stringResource(petTypeLabel(pet.type)), filled = true)
                if (pet.breed.isNotBlank()) Chip(pet.breed, filled = false)
            }
        }
    }
}

@Composable
private fun Chip(text: String, filled: Boolean) {
    val bg = if (filled) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceContainer
    Text(
        text = text,
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 4.dp)
    )
}

/** Age, weight and height, each with its icon, in three equal cells. */
@Composable
private fun MeasuresRow(pet: Pet) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        MeasureCell(R.drawable.ic_calendar, stringResource(R.string.age_years, pet.ageYears), stringResource(R.string.label_age), Modifier.weight(1f))
        MeasureCell(R.drawable.ic_weight, "${trim(pet.weight)} ${pet.weightUnit.label}", stringResource(R.string.label_weight), Modifier.weight(1f))
        MeasureCell(R.drawable.ic_ruler, "${trim(pet.height)} ${pet.heightUnit.label}", stringResource(R.string.label_height), Modifier.weight(1f))
    }
}

@Composable
private fun MeasureCell(iconRes: Int, value: String, label: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = Primary600,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.size(6.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/** Segmented control of the two tabs; the active one differs by fill and by weight, not color alone. */
@Composable
private fun PetTabs(careSelected: Boolean, onCare: () -> Unit, onHealth: () -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TabPill(stringResource(R.string.tab_care_schedule), careSelected, onCare, Modifier.weight(1f))
        TabPill(stringResource(R.string.tab_health_records), !careSelected, onHealth, Modifier.weight(1f))
    }
}

@Composable
private fun TabPill(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = if (selected) White50 else MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Primary600 else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}

/** The pinned actions under the content: Add health record (health tab only) and Delete pet. */
@Composable
private fun BottomActions(showAddRecord: Boolean, onAddRecord: () -> Unit, onDelete: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (showAddRecord) {
            ActionRow(
                iconRes = R.drawable.ic_plus,
                label = stringResource(R.string.add_health_record),
                contentColor = White50,
                background = MaterialTheme.colorScheme.primary,
                onClick = onAddRecord
            )
        }
        ActionRow(
            iconRes = R.drawable.ic_trash,
            label = stringResource(R.string.delete_pet),
            contentColor = Error1,
            background = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.4f),
            onClick = onDelete
        )
    }
}

@Composable
private fun ActionRow(iconRes: Int, label: String, contentColor: androidx.compose.ui.graphics.Color, background: androidx.compose.ui.graphics.Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(background)
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelLarge, color = contentColor)
    }
}

/** Drops a trailing ".0" so whole numbers read cleanly (e.g. 28 instead of 28.0). */
private fun trim(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
