package com.example.argos_home.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.argos_home.domain.Household
import com.example.argos_home.domain.Pet
import com.example.argos_home.domain.SeedData

// Snapshot list so the pets screens observe it directly and any add/edit/delete recomposes with no reload.
/** Authoritative in-memory store of the household pets; seeded once from [SeedData]. */
object PetRepository {

    private val items: SnapshotStateList<Pet> =
        mutableStateListOf<Pet>().apply { addAll(SeedData.pets) }

    /** Observable view of the pets. Read only: changes go through add/update/delete. */
    val pets: List<Pet> get() = items

    fun findById(id: String): Pet? = items.firstOrNull { it.id == id }

    /** True while the home is below the product limit, so the add form can refuse the 31st pet. */
    fun canAddPet(): Boolean = items.size < Household.MAX_PETS

    fun add(pet: Pet) {
        if (canAddPet()) items.add(pet)
    }

    fun update(pet: Pet) {
        val index = items.indexOfFirst { it.id == pet.id }
        if (index >= 0) items[index] = pet
    }

    /** Removes the pet and its medical records, so no orphan history is left behind. */
    fun delete(id: String) {
        items.removeAll { it.id == id }
        MedicalRecordRepository.deleteForPet(id)
    }
}
