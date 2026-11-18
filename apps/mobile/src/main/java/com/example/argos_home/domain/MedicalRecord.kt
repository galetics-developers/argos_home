package com.example.argos_home.domain

import java.time.LocalDate

/** Kind of medical record; drives the representative icon and reads as a label in the history. */
enum class MedicalRecordType { VACCINE, MEDICATION, PROCEDURE }

/** Status of a medical record as shown in the history; it never flows through the watch confirmation. */
enum class MedicalRecordStatus { PENDING, COMPLETED, CANCELED }

/** One medical history entry of a pet; a read-only record, independent of the calendar activity. */
data class MedicalRecord(
    val id: String,
    val petId: String,
    val type: MedicalRecordType,
    val title: String,
    val date: LocalDate,
    val period: String = "",       // Free text period or time range, e.g. "7-day course" or "10:30 - 14:00".
    val assignedTo: String = "",   // Member name or clinic responsible for the record.
    val status: MedicalRecordStatus,
    val note: String = ""
)
