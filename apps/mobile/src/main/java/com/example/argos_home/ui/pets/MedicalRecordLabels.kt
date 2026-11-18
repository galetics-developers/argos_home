package com.example.argos_home.ui.pets

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.example.argos_home.R
import com.example.argos_home.domain.MedicalRecordStatus
import com.example.argos_home.domain.MedicalRecordType
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Success1
import com.example.argos_home.ui.theme.Warning1

/** Icon of a medical record type, drawn next to its title in the history. */
@DrawableRes
fun medicalTypeIcon(type: MedicalRecordType): Int = when (type) {
    MedicalRecordType.VACCINE -> R.drawable.ic_syringe
    MedicalRecordType.MEDICATION -> R.drawable.ic_pill
    MedicalRecordType.PROCEDURE -> R.drawable.ic_clipboard
}

/** Label of a medical record type. */
@StringRes
fun medicalTypeLabel(type: MedicalRecordType): Int = when (type) {
    MedicalRecordType.VACCINE -> R.string.med_type_vaccine
    MedicalRecordType.MEDICATION -> R.string.med_type_medication
    MedicalRecordType.PROCEDURE -> R.string.med_type_procedure
}

/** Label of a medical record status. */
@StringRes
fun medicalStatusLabel(status: MedicalRecordStatus): Int = when (status) {
    MedicalRecordStatus.PENDING -> R.string.med_status_pending
    MedicalRecordStatus.COMPLETED -> R.string.med_status_completed
    MedicalRecordStatus.CANCELED -> R.string.med_status_canceled
}

/** Color of a medical record status; always paired with its label, never shown alone. */
fun medicalStatusColor(status: MedicalRecordStatus): Color = when (status) {
    MedicalRecordStatus.PENDING -> Warning1
    MedicalRecordStatus.COMPLETED -> Success1
    MedicalRecordStatus.CANCELED -> Error1
}
