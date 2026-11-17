package com.example.argos_home.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ItemType
import com.example.argos_home.domain.Recurrence
import com.example.argos_home.domain.ReminderUnit
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

/** Create-activity modal; the Task/Event toggle swaps the fields while keeping the shared ones in place. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewActivitySheet(
    defaultDate: LocalDate,
    onDismiss: () -> Unit,
    onSave: (ScheduledActivity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NewActivityForm(defaultDate = defaultDate, onCancel = onDismiss, onSave = onSave)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewActivityForm(
    defaultDate: LocalDate,
    onCancel: () -> Unit,
    onSave: (ScheduledActivity) -> Unit
) {
    var type by remember { mutableStateOf(ItemType.TASK) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var petName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(defaultDate) }
    var startText by remember { mutableStateOf("") }
    var startPeriod by remember { mutableStateOf("AM") }
    var endText by remember { mutableStateOf("") }
    var endPeriod by remember { mutableStateOf("AM") }
    var frequency by remember { mutableStateOf(Recurrence.DAILY) }
    var reminderAmount by remember { mutableStateOf("15") }
    var reminderUnit by remember { mutableStateOf(ReminderUnit.MINUTES) }
    var assignedTo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val petOptions = remember { SeedData.pets.map { it.name } }
    val memberOptions = remember { listOf("You") + SeedData.members.map { it.name } }

    val startTime = parseTime(startText, startPeriod)
    val endTime = parseTime(endText, endPeriod)
    val canSave = title.isNotBlank() && petName.isNotBlank() && startTime != null &&
        (type == ItemType.TASK || endTime != null)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Text(
            text = stringResource(R.string.new_activity_title),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        FieldLabel(stringResource(R.string.field_activity_type))
        TypeToggle(selected = type, onSelect = { type = it })

        FieldLabel(stringResource(R.string.field_title))
        PlainField(
            value = title,
            onValue = { title = it },
            placeholder = stringResource(
                if (type == ItemType.TASK) R.string.hint_task_title else R.string.hint_event_title
            )
        )

        if (type == ItemType.TASK) {
            FieldLabel(stringResource(R.string.field_task_type))
            SuggestField(category, { category = it }, stringResource(R.string.hint_task_type), TASK_TYPES)
        } else {
            FieldLabel(stringResource(R.string.field_event_type))
            SuggestField(category, { category = it }, stringResource(R.string.hint_event_type), EVENT_TYPES)
            Text(
                text = stringResource(R.string.event_type_options),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        FieldLabel(stringResource(R.string.field_pet))
        SuggestField(petName, { petName = it }, stringResource(R.string.hint_pet), petOptions)

        FieldLabel(stringResource(R.string.field_date))
        PlainField(
            value = ActivityFormats.dayMonth(date),
            onValue = {},
            placeholder = stringResource(R.string.hint_date),
            readOnly = true,
            onClick = { showDatePicker = true }
        )

        FieldLabel(stringResource(R.string.field_time))
        TimeRow(
            text = startText,
            onText = { startText = it },
            placeholder = stringResource(R.string.hint_time_start),
            period = startPeriod,
            onPeriod = { startPeriod = it }
        )
        if (type == ItemType.EVENT) {
            TimeRow(
                text = endText,
                onText = { endText = it },
                placeholder = stringResource(R.string.hint_time_end),
                period = endPeriod,
                onPeriod = { endPeriod = it }
            )
        }

        if (type == ItemType.TASK) {
            FieldLabel(stringResource(R.string.field_frequency))
            OptionDropdown(
                value = ActivityFormats.recurrenceLabel(frequency),
                placeholder = stringResource(R.string.hint_frequency),
                options = Recurrence.entries.map { ActivityFormats.recurrenceLabel(it) },
                onIndex = { frequency = Recurrence.entries[it] }
            )
        }

        FieldLabel(stringResource(R.string.field_reminder))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = reminderAmount,
                onValueChange = { input -> reminderAmount = input.filter { it.isDigit() }.take(3) },
                placeholder = { Text(stringResource(R.string.hint_reminder_amount)) },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            )
            OptionDropdown(
                value = ActivityFormats.unitLabel(reminderUnit),
                placeholder = ActivityFormats.unitLabel(ReminderUnit.MINUTES),
                options = ReminderUnit.entries.map { ActivityFormats.unitLabel(it) },
                onIndex = { reminderUnit = ReminderUnit.entries[it] },
                modifier = Modifier.weight(1f)
            )
        }
        Text(
            text = stringResource(R.string.reminder_hint),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        FieldLabel(stringResource(R.string.field_assigned_to))
        SuggestField(assignedTo, { assignedTo = it }, stringResource(R.string.hint_assigned_to), memberOptions)

        FieldLabel(stringResource(R.string.field_notes))
        OutlinedTextField(
            value = notes,
            onValueChange = { notes = it },
            placeholder = { Text(stringResource(R.string.hint_notes)) },
            shape = RoundedCornerShape(12.dp),
            minLines = 2,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.weight(1f)
            ) {
                Text(stringResource(R.string.action_cancel))
            }
            Button(
                onClick = {
                    onSave(
                        buildActivity(
                            type = type,
                            title = title.trim(),
                            category = category.trim().ifEmpty { defaultCategory(type) },
                            petName = petName.trim(),
                            date = date,
                            startTime = startTime ?: LocalTime.of(8, 0),
                            endTime = if (type == ItemType.EVENT) endTime else null,
                            recurrence = if (type == ItemType.EVENT) Recurrence.ONCE else frequency,
                            reminderAmount = reminderAmount.toIntOrNull() ?: 15,
                            reminderUnit = reminderUnit,
                            assignedTo = resolveAssignee(assignedTo),
                            note = notes.trim()
                        )
                    )
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

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        date = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    showDatePicker = false
                }) { Text(stringResource(R.string.date_picker_confirm)) }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(stringResource(R.string.date_picker_dismiss))
                }
            }
        ) {
            DatePicker(state = pickerState)
        }
    }
}

/** Task/Event segmented toggle. */
@Composable
private fun TypeToggle(selected: ItemType, onSelect: (ItemType) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TogglePill(stringResource(R.string.toggle_task), selected == ItemType.TASK, { onSelect(ItemType.TASK) }, Modifier.weight(1f))
        TogglePill(stringResource(R.string.toggle_event), selected == ItemType.EVENT, { onSelect(ItemType.EVENT) }, Modifier.weight(1f))
    }
}

@Composable
private fun TogglePill(text: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleSmall,
        color = if (selected) White50 else MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) Primary600 else androidx.compose.ui.graphics.Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp)
    )
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface
    )
}

/** Plain text field; when [readOnly] it is used as a click target (e.g. the date field opens the picker). */
@Composable
private fun PlainField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    readOnly: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValue,
        placeholder = { Text(placeholder) },
        singleLine = true,
        readOnly = readOnly,
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    )
}

/** Editable field with suggestions: shows up to six matching options, or the first few when empty. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SuggestField(
    value: String,
    onValue: (String) -> Unit,
    placeholder: String,
    options: List<String>
) {
    var expanded by remember { mutableStateOf(false) }
    val filtered = if (value.isBlank()) options.take(6)
    else options.filter { it.contains(value.trim(), ignoreCase = true) }.take(6)
    ExposedDropdownMenuBox(expanded = expanded && filtered.isNotEmpty(), onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value,
            onValueChange = { onValue(it); expanded = true },
            placeholder = { Text(placeholder) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded && filtered.isNotEmpty(),
            onDismissRequest = { expanded = false }
        ) {
            filtered.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onValue(option); expanded = false })
            }
        }
    }
}

/** Read-only drop-down that picks one of a fixed set of options by index. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OptionDropdown(
    value: String,
    placeholder: String,
    options: List<String>,
    onIndex: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }, modifier = modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            placeholder = { Text(placeholder) },
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEachIndexed { index, option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onIndex(index); expanded = false })
            }
        }
    }
}

/** A time text field ("08:30") beside an AM/PM drop-down. */
@Composable
private fun TimeRow(
    text: String,
    onText: (String) -> Unit,
    placeholder: String,
    period: String,
    onPeriod: (String) -> Unit
) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = text,
            onValueChange = { onText(it.filter { c -> c.isDigit() || c == ':' }.take(5)) },
            placeholder = { Text(placeholder) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.weight(1f)
        )
        OptionDropdown(
            value = period,
            placeholder = stringResource(R.string.hint_period),
            options = listOf("AM", "PM"),
            onIndex = { onPeriod(if (it == 0) "AM" else "PM") },
            modifier = Modifier.width(110.dp)
        )
    }
}

/** Parses "8:30" + "AM"/"PM" into a 24-hour [LocalTime]; returns null when the text is not a valid clock time. */
private fun parseTime(text: String, period: String): LocalTime? {
    val trimmed = text.trim()
    if (trimmed.isEmpty()) return null
    val parts = trimmed.split(":")
    val hour12 = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val minute = if (parts.size > 1) parts[1].toIntOrNull() ?: return null else 0
    if (hour12 !in 1..12 || minute !in 0..59) return null
    val hour24 = (hour12 % 12) + if (period == "PM") 12 else 0
    return LocalTime.of(hour24, minute)
}

private fun defaultCategory(type: ItemType): String =
    if (type == ItemType.TASK) "Task" else "Appointment"

/** "You" is stored as the local member so the created card reads the same as the seed ones. */
private fun resolveAssignee(input: String): String {
    val trimmed = input.trim()
    return if (trimmed.isEmpty() || trimmed.equals("You", ignoreCase = true)) SeedData.localMember.name else trimmed
}

private fun buildActivity(
    type: ItemType,
    title: String,
    category: String,
    petName: String,
    date: LocalDate,
    startTime: LocalTime,
    endTime: LocalTime?,
    recurrence: Recurrence,
    reminderAmount: Int,
    reminderUnit: ReminderUnit,
    assignedTo: String,
    note: String
): ScheduledActivity = ScheduledActivity(
    id = "user-" + System.currentTimeMillis(),
    type = type,
    petName = petName,
    title = title,
    category = category,
    date = date,
    startTime = startTime,
    endTime = endTime,
    recurrence = recurrence,
    reminderAmount = reminderAmount,
    reminderUnit = reminderUnit,
    assignedTo = assignedTo,
    note = note
)

private val TASK_TYPES = listOf("Walking", "Feeding", "Play", "Training", "Care", "Grooming")
private val EVENT_TYPES = listOf(
    "Veterinary Visit", "Vaccination", "Deworming", "Professional Dental Cleaning",
    "Medical Examination", "Laboratory Test", "Professional Nail Trimming", "Professional Bath",
    "Grooming", "Treatment Start", "Medical Procedure", "Veterinary Emergency",
    "Medical Checkup", "Documentation Update"
)
