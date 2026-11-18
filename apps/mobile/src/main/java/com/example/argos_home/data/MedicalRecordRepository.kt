package com.example.argos_home.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.argos_home.domain.MedicalRecord
import com.example.argos_home.domain.MedicalRecordStatus
import com.example.argos_home.domain.MedicalRecordType
import com.example.argos_home.domain.SeedData
import java.time.LocalDate

// Health history lives only on the phone; it never flows through the Data Layer to the watch.
/** In-memory medical history, seeded per pet so every pet owns its own records across the three types. */
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

    // Three records per pet (a past vaccine, a past medication, an upcoming procedure), related by pet, not by home.
    private fun seed(): List<MedicalRecord> {
        val today = LocalDate.now()
        val clinics = listOf("Argos Vet Clinic", "Northside Vet Clinic")
        return SeedData.pets.flatMapIndexed { index, pet ->
            val member = SeedData.members[index % SeedData.members.size].name
            listOf(
                MedicalRecord(
                    id = "med-${pet.id}-1", petId = pet.id, type = MedicalRecordType.VACCINE,
                    title = "Rabies booster", date = today.minusMonths(2), period = "10:30 - 11:00",
                    assignedTo = clinics[index % clinics.size], status = MedicalRecordStatus.COMPLETED,
                    note = "Next dose due in one year."
                ),
                MedicalRecord(
                    id = "med-${pet.id}-2", petId = pet.id, type = MedicalRecordType.MEDICATION,
                    title = "Deworming course", date = today.minusMonths(1), period = "7-day course",
                    assignedTo = member, status = MedicalRecordStatus.COMPLETED,
                    note = "One dose every 12 hours with food."
                ),
                MedicalRecord(
                    id = "med-${pet.id}-3", petId = pet.id, type = MedicalRecordType.PROCEDURE,
                    title = "Annual checkup", date = today.plusDays((index % 4 + 1) * 3L), period = "09:00 - 10:30",
                    assignedTo = clinics[(index + 1) % clinics.size],
                    status = if (index % 3 == 0) MedicalRecordStatus.CANCELED else MedicalRecordStatus.PENDING,
                    note = "Bring previous lab results."
                )
            )
        }
    }
}
