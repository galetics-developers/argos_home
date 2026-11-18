package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.PetRepository
import com.example.argos_home.domain.HeightUnit
import com.example.argos_home.domain.Pet
import com.example.argos_home.domain.PetType
import com.example.argos_home.domain.WeightUnit
import com.example.argos_home.ui.calendar.petIconOf
import com.example.argos_home.ui.calendar.petTypeColor
import com.example.argos_home.ui.calendar.petTypeColorSoft
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Create or edit a pet; edit preloads the stored values, add starts empty with Dog selected. */
@Composable
fun PetForm(
    petId: String?,
    onCancel: () -> Unit,
    onSaved: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val existing = petId?.let { PetRepository.findById(it) }
    var name by remember { mutableStateOf(existing?.name ?: "") }
    var type by remember { mutableStateOf(existing?.type ?: PetType.DOG) }
    var breed by remember { mutableStateOf(existing?.breed ?: "") }
    var age by remember { mutableStateOf(existing?.ageYears?.toString() ?: "") }
    var weight by remember { mutableStateOf(existing?.weight?.let { trimValue(it) } ?: "") }
    var weightUnit by remember { mutableStateOf(existing?.weightUnit ?: WeightUnit.KG) }
    var height by remember { mutableStateOf(existing?.height?.let { trimValue(it) } ?: "") }
    var heightUnit by remember { mutableStateOf(existing?.heightUnit ?: HeightUnit.CM) }
    val canSave = name.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        FormTopBar(isEdit = existing != null, onBack = onCancel)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(modifier = Modifier.size(4.dp))
            AvatarPreview(type)

            FieldLabel(stringResource(R.string.field_full_name))
            PlainField(name, { name = it }, stringResource(R.string.hint_full_name))

            FieldLabel(stringResource(R.string.field_type))
            TypeGrid(selected = type, onSelect = { type = it })

            FieldLabel(stringResource(R.string.field_breed))
            PlainField(breed, { breed = it }, stringResource(R.string.hint_breed))

            FieldLabel(stringResource(R.string.field_age))
            PlainField(age, { age = it.filter { c -> c.isDigit() }.take(3) }, stringResource(R.string.hint_age), keyboardType = KeyboardType.Number)

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel(stringResource(R.string.field_weight))
                    Spacer(modifier = Modifier.size(6.dp))
                    PlainField(weight, { weight = filterDecimal(it) }, stringResource(R.string.hint_weight), keyboardType = KeyboardType.Decimal)
                }
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel(stringResource(R.string.field_unit))
                    Spacer(modifier = Modifier.size(6.dp))
                    OptionDropdown(
                        value = weightUnit.label,
                        placeholder = weightUnit.label,
                        options = WeightUnit.entries.map { it.label },
                        onIndex = { weightUnit = WeightUnit.entries[it] }
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel(stringResource(R.string.field_height))
                    Spacer(modifier = Modifier.size(6.dp))
                    PlainField(height, { height = filterDecimal(it) }, stringResource(R.string.hint_height), keyboardType = KeyboardType.Decimal)
                }
                Column(modifier = Modifier.weight(1f)) {
                    FieldLabel(stringResource(R.string.field_unit))
                    Spacer(modifier = Modifier.size(6.dp))
                    OptionDropdown(
                        value = heightUnit.label,
                        placeholder = heightUnit.label,
                        options = HeightUnit.entries.map { it.label },
                        onIndex = { heightUnit = HeightUnit.entries[it] }
                    )
                }
            }

            Spacer(modifier = Modifier.size(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(onClick = onCancel, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                    Text(stringResource(R.string.action_cancel))
                }
                Button(
                    onClick = {
                        val saved = Pet(
                            id = existing?.id ?: ("pet-" + System.currentTimeMillis()),
                            name = name.trim(),
                            type = type,
                            breed = breed.trim(),
                            ageYears = age.toIntOrNull() ?: 0,
                            weight = weight.toDoubleOrNull() ?: 0.0,
                            weightUnit = weightUnit,
                            height = height.toDoubleOrNull() ?: 0.0,
                            heightUnit = heightUnit
                        )
                        if (existing != null) PetRepository.update(saved) else PetRepository.add(saved)
                        onSaved(saved.id)
                    },
                    enabled = canSave,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.action_save))
                }
            }
        }
    }
}

@Composable
private fun FormTopBar(isEdit: Boolean, onBack: () -> Unit) {
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
            text = stringResource(if (isEdit) R.string.edit_pet_title else R.string.add_pet_title),
            style = MaterialTheme.typography.titleMedium,
            color = White50
        )
    }
}

/** Avatar that mirrors the selected type: a soft-toned circle bordered in the type color, with a static photo button. */
@Composable
private fun AvatarPreview(type: PetType) {
    val color = petTypeColor(type)
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(contentAlignment = Alignment.BottomEnd) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(petTypeColorSoft(type))
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(petIconOf(type)),
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(40.dp)
                )
            }
            // Static photo affordance: shown for fidelity but it does not pick an image in this release.
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(Primary600),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_camera),
                    contentDescription = stringResource(R.string.pet_photo),
                    tint = White50,
                    modifier = Modifier.size(14.dp)
                )
            }
        }
    }
}

/** The twelve species as a four-column grid of selectable cards. */
@Composable
private fun TypeGrid(selected: PetType, onSelect: (PetType) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        PetType.entries.chunked(4).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEach { type ->
                    TypeCard(type = type, selected = type == selected, onClick = { onSelect(type) }, modifier = Modifier.weight(1f))
                }
                repeat(4 - row.size) { Spacer(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun TypeCard(type: PetType, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    // Each type carries its group color on the border and icon; selecting it fills the card with that color.
    val color = petTypeColor(type)
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) color.copy(alpha = 0.16f) else MaterialTheme.colorScheme.surface)
            .border(if (selected) 2.dp else 1.dp, color, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(petIconOf(type)),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )
        Text(
            text = stringResource(petTypeLabel(type)),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) color else MaterialTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

/** Keeps digits and a single decimal point, so the weight/height fields parse cleanly. */
private fun filterDecimal(input: String): String {
    val cleaned = input.filter { it.isDigit() || it == '.' }
    val dot = cleaned.indexOf('.')
    return if (dot < 0) cleaned else cleaned.substring(0, dot + 1) + cleaned.substring(dot + 1).replace(".", "")
}

/** Drops a trailing ".0" so a whole number shows without it. */
private fun trimValue(value: Double): String =
    if (value % 1.0 == 0.0) value.toInt().toString() else value.toString()
