package com.example.argos_home.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.argos_home.domain.MedicalRecord
import com.example.argos_home.domain.MedicalRecordStatus
import com.example.argos_home.domain.MedicalRecordType
import com.example.argos_home.domain.ReminderUnit
import com.example.argos_home.domain.SeedData
import java.time.LocalDate
import java.time.LocalTime

// Health history lives only on the phone; it never flows through the Data Layer to the watch.
/** In-memory medical history, seeded per pet so every pet owns ten records across the three types. */
object MedicalRecordRepository {

    private val items: SnapshotStateList<MedicalRecord> =
        mutableStateListOf<MedicalRecord>().apply { addAll(seed()) }

    /** Records of one pet, newest first, for the Health records tab. */
    fun recordsFor(petId: String): List<MedicalRecord> =
        items.filter { it.petId == petId }.sortedByDescending { it.date }

    fun add(record: MedicalRecord) {
        items.add(record)
    }

    fun deleteForPet(petId: String) {
        items.removeAll { it.petId == petId }
    }

    // Ten deterministic records per pet: the three types rotate, dates fall in the past and future so the
    // date-derived status shows both Registered and Pending, and a few are explicitly Canceled.
    private fun seed(): List<MedicalRecord> {
        val today = LocalDate.now()
        val clinics = listOf("Argos Vet Clinic", "Northside Vet Clinic", "Riviera Pet Hospital")
        val types = MedicalRecordType.entries
        val titlesByType = mapOf(
            MedicalRecordType.VACCINE to listOf("Rabies booster", "Distemper shot", "Parvovirus vaccine", "Bordetella vaccine"),
            MedicalRecordType.MEDICATION to listOf("Deworming course", "Flea treatment", "Antibiotic course", "Joint supplement"),
            MedicalRecordType.PROCEDURE to listOf("Annual checkup", "Dental cleaning", "X-ray scan", "Blood panel")
        )
        val notesByType = mapOf(
            MedicalRecordType.VACCINE to "Next dose due in one year.",
            MedicalRecordType.MEDICATION to "One dose every 12 hours with food.",
            MedicalRecordType.PROCEDURE to "Bring previous lab results."
        )
        // Five past offsets and five future ones; a per-pet rotation keeps both halves for every pet.
        val dayOffsets = listOf(-90, -45, -20, -10, -3, 2, 7, 14, 30, 60)
        val startHours = listOf(9, 10, 11, 13, 8, 14, 15, 16, 10, 12)
        val durations = listOf(30L, 60L, 90L, 45L, 120L)
        val reminders = listOf(
            15 to ReminderUnit.MINUTES, 30 to ReminderUnit.MINUTES, 1 to ReminderUnit.HOURS,
            2 to ReminderUnit.HOURS, 1 to ReminderUnit.DAYS, 1 to ReminderUnit.MONTHS
        )

        return SeedData.pets.flatMapIndexed { index, pet ->
            (1..10).map { i ->
                val type = types[(i - 1) % types.size]
                val titles = titlesByType.getValue(type)
                val offset = dayOffsets[(i - 1 + index) % dayOffsets.size]
                val start = LocalTime.of(startHours[(i - 1) % startHours.size], if (i % 2 == 0) 30 else 0)
                val (amount, unit) = reminders[(index + i) % reminders.size]
                val canceled = (index + i) % 7 == 0
                val assignee = if (type == MedicalRecordType.PROCEDURE) clinics[(index + i) % clinics.size]
                    else SeedData.members[(index + i) % SeedData.members.size].name
                MedicalRecord(
                    id = "med-${pet.id}-$i",
                    petId = pet.id,
                    type = type,
                    title = titles[(index + i) % titles.size],
                    date = today.plusDays(offset.toLong()),
                    startTime = start,
                    endTime = start.plusMinutes(durations[(index + i) % durations.size]),
                    reminderAmount = amount,
                    reminderUnit = unit,
                    assignedTo = assignee,
                    status = if (canceled) MedicalRecordStatus.CANCELED else MedicalRecordStatus.PENDING,
                    note = notesByType.getValue(type)
                )
            }
        }
    }
}
