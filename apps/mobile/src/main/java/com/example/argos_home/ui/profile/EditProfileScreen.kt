package com.example.argos_home.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material3.SelectableDates
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.ProfileRepository
import com.example.argos_home.domain.formatBirthDate
import com.example.argos_home.domain.formatPhone
import com.example.argos_home.domain.onlyDigits
import com.example.argos_home.ui.household.PressFillButton
import com.example.argos_home.ui.household.SubmoduleHeader
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.White50
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

/** Edit profile submodule: name, last name, birth date (past only) and phone (grouped), saved back to the profile. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(onBack: () -> Unit, onSaved: () -> Unit, modifier: Modifier = Modifier) {
    val seed = ProfileRepository.profile
    var firstNames by remember { mutableStateOf(seed.firstNames) }
    var lastNames by remember { mutableStateOf(seed.lastNames) }
    var birthDate by remember { mutableStateOf(seed.birthDate) }
    var phoneDigits by remember { mutableStateOf(seed.phoneDigits) }
    var showDatePicker by remember { mutableStateOf(false) }

    val canSave = firstNames.isNotBlank() && lastNames.isNotBlank()

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SubmoduleHeader(title = stringResource(R.string.edit_profile), onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EditableAvatar()

                LabeledField(
                    label = stringResource(R.string.field_names),
                    value = firstNames,
                    onValueChange = { firstNames = it },
                    placeholder = stringResource(R.string.hint_names),
                    leadingIcon = R.drawable.ic_nav_profile,
                    modifier = Modifier.fillMaxWidth()
                )
                LabeledField(
                    label = stringResource(R.string.field_last_names),
                    value = lastNames,
                    onValueChange = { lastNames = it },
                    placeholder = stringResource(R.string.hint_last_names),
                    leadingIcon = R.drawable.ic_nav_profile,
                    modifier = Modifier.fillMaxWidth()
                )
                LabeledField(
                    label = stringResource(R.string.field_birth_date),
                    value = formatBirthDate(birthDate),
                    onValueChange = {},
                    placeholder = stringResource(R.string.hint_birth_date),
                    leadingIcon = R.drawable.ic_calendar,
                    readOnly = true,
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                )
                LabeledField(
                    label = stringResource(R.string.field_phone),
                    value = formatPhone(phoneDigits),
                    onValueChange = { phoneDigits = onlyDigits(it) },
                    placeholder = stringResource(R.string.hint_phone),
                    leadingIcon = R.drawable.ic_phone,
                    keyboardType = KeyboardType.Phone,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Cancel + Save pinned at the bottom, outside the scroll area.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PressFillButton(
                    onClick = onBack,
                    fillColor = Primary600,
                    restContentColor = Primary600,
                    restContainerColor = White50,
                    borderColor = Primary600,
                    modifier = Modifier.weight(1f)
                ) { contentColor ->
                    Text(
                        text = stringResource(R.string.action_cancel),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                }
                Button(
                    onClick = {
                        ProfileRepository.updateProfile(firstNames, lastNames, birthDate, phoneDigits)
                        onSaved()
                    },
                    enabled = canSave,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = White50,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_save), color = White50)
                }
            }
        }
    }

    if (showDatePicker) {
        val todayUtc = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = birthDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
            // A birth date can never be in the future, so only past days are selectable.
            selectableDates = object : SelectableDates {
                override fun isSelectableDate(utcTimeMillis: Long): Boolean = utcTimeMillis <= todayUtc
                override fun isSelectableYear(year: Int): Boolean = year <= LocalDate.now().year
            }
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { millis ->
                        birthDate = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
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

/** Large user avatar with a small camera badge; the photo picker is a future feature, the badge is shown for fidelity. */
@Composable
private fun EditableAvatar(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.BottomEnd) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(Primary100),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_nav_profile),
                contentDescription = null,
                tint = Primary600,
                modifier = Modifier.size(44.dp)
            )
        }
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(White50)
                .padding(2.dp)
                .clip(CircleShape)
                .background(Secondary500),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_camera),
                contentDescription = stringResource(R.string.change_photo),
                tint = White50,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
