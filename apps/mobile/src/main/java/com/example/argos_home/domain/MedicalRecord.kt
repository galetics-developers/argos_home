package com.example.argos_home.domain

import java.time.LocalDate
import java.time.LocalTime

/** Kind of medical record; drives the representative icon and reads as a label in the history. */
enum class MedicalRecordType { VACCINE, MEDICATION, PROCEDURE }

/** Status of a medical record; PENDING/REGISTERED are derived from the date, CANCELED stays for existing data. */
enum class MedicalRecordStatus { PENDING, REGISTERED, CANCELED }

/** One medical history entry of a pet; a read-only record, independent of the calendar activity. */
data class MedicalRecord(
    val id: String,
    val petId: String,
    val type: MedicalRecordType,
    val title: String,
    val date: LocalDate,
    val startTime: LocalTime? = null,   // Start of the appointment window, shown as a time range on the card.
    val endTime: LocalTime? = null,     // End of the appointment window, paired with startTime.
    val reminderAmount: Int = 0,        // Reminder lead paired with reminderUnit (e.g. 15 MINUTES before).
    val reminderUnit: ReminderUnit = ReminderUnit.MINUTES,
    val assignedTo: String = "",        // Member name or clinic responsible for the record.
    val status: MedicalRecordStatus,
    val note: String = ""
)

/** Derives the status from the date: CANCELED stays as stored, a past date reads REGISTERED, otherwise PENDING. */
fun effectiveStatus(record: MedicalRecord, today: LocalDate = LocalDate.now()): MedicalRecordStatus = when {
    record.status == MedicalRecordStatus.CANCELED -> MedicalRecordStatus.CANCELED
    record.date.isBefore(today) -> MedicalRecordStatus.REGISTERED
    else -> MedicalRecordStatus.PENDING
}
