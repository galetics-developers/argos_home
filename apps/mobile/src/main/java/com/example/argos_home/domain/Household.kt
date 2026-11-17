package com.example.argos_home.domain

/** A person of the household; seed data only, this release has no login and no user selection. */
data class HouseholdMember(
    val id: String,
    val name: String
)

/** A pet of the household; its type drives the avatar icon shown on both devices. */
data class Pet(
    val id: String,
    val name: String,
    val type: PetType,
    val breed: String = ""
)

/** The single home that owns every member, pet and activity; one household per install in this release. */
data class Household(
    val name: String,
    val members: List<HouseholdMember>,
    val pets: List<Pet>
) {
    companion object {
        const val MAX_PETS = 30     // Product golden rule: a home registers at most this many pets.
        const val MAX_MEMBERS = 21  // Product golden rule: a home registers at most this many members.
    }
}
