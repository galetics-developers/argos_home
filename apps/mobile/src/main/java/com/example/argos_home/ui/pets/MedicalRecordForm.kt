package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBars
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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.MedicalRecord
import com.example.argos_home.domain.MedicalRecordStatus
import com.example.argos_home.domain.MedicalRecordType
import com.example.argos_home.domain.SeedData
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Full-screen add-record form shown over the detail; its own entity and fields, never a calendar activity. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalRecordForm(
    petId: String,
    onDismiss: () -> Unit,
    onSave: (MedicalRecord) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, decorFitsSystemWindows = false)
    ) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            RecordForm(petId = petId, onCancel = onDismiss, onSave = onSave)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecordForm(petId: String, onCancel: () -> Unit, onSave: (MedicalRecord) -> Unit) {
    var type by remember { mutableStateOf(MedicalRecordType.VACCINE) }
    var title by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now()) }
    var period by remember { mutableStateOf("") }
    var assignedTo by remember { mutableStateOf("") }
    var status by remember { mutableStateOf(MedicalRecordStatus.PENDING) }
    var notes by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }

    val typeLabels = MedicalRecordType.entries.map { stringResource(medicalTypeLabel(it)) }
    val statusLabels = MedicalRecordStatus.entries.map { stringResource(medicalStatusLabel(it)) }
    val assigneeOptions = remember { SeedData.members.map { it.name } + listOf("Argos Vet Clinic", "Northside Vet Clinic") }
    val canSave = title.isNotBlank()

    Column(modifier = Modifier.fillMaxSize()) {
        // Branded header merged with the status bar, back control included.
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
                    .clickable(onClick = onCancel)
                    .padding(2.dp)
            )
            Spacer(modifier = Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.new_record_title),
                style = MaterialTheme.typography.titleMedium,
                color = White50
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .imePadding()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            FieldLabel(stringResource(R.string.field_record_type), required = true)
            OptionDropdown(
                value = stringResource(medicalTypeLabel(type)),
                placeholder = stringResource(R.string.field_record_type),
                options = typeLabels,
                onIndex = { type = MedicalRecordType.entries[it] }
            )

            FieldLabel(stringResource(R.string.field_record_title), required = true)
            PlainField(title, { title = it }, stringResource(R.string.hint_record_title))

            FieldLabel(stringResource(R.string.field_date), required = true)
            ClickableField(
                value = ActivityFormats.dayMonth(date),
                placeholder = stringResource(R.string.hint_date),
                onClick = { showDatePicker = true }
            )

            FieldLabel(stringResource(R.string.field_period))
            PlainField(period, { period = it }, stringResource(R.string.hint_record_period))

            FieldLabel(stringResource(R.string.field_clinic))
            SuggestField(assignedTo, { assignedTo = it }, stringResource(R.string.hint_clinic), assigneeOptions)

            FieldLabel(stringResource(R.string.field_status), required = true)
            OptionDropdown(
                value = stringResource(medicalStatusLabel(status)),
                placeholder = stringResource(R.string.field_status),
                options = statusLabels,
                onIndex = { status = MedicalRecordStatus.entries[it] }
            )

            FieldLabel(stringResource(R.string.field_notes))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                placeholder = { Text(stringResource(R.string.hint_notes)) },
                shape = RoundedCornerShape(12.dp),
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Actions pinned at the bottom, above the navigation bar, so Save is always reachable.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(onClick = onCancel, shape = RoundedCornerShape(12.dp), modifier = Modifier.weight(1f)) {
                Text(stringResource(R.string.action_cancel))
            }
            Button(
                onClick = {
                    onSave(
                        MedicalRecord(
                            id = "med-" + System.currentTimeMillis(),
                            petId = petId,
                            type = type,
                            title = title.trim(),
                            date = date,
                            period = period.trim(),
                            assignedTo = assignedTo.trim(),
                            status = status,
                            note = notes.trim()
                        )
                    )
                },
                enabled = canSave,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check_circle),
                    contentDescription = null,
                    tint = White50,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.size(8.dp))
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
