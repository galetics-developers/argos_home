package com.example.argos_home.domain

import java.time.LocalDate
import java.time.LocalTime

/** Static seed of the home, its pets and their activities; the watch keeps its own identical copy. */
object SeedData {

    const val HOUSEHOLD_NAME = "Pérez Household"

    /** Every real action is attributed to this member, on the phone and on the watch alike. */
    val localMember = HouseholdMember(id = "member-01", name = "Cesar Sánchez")

    /** A small realistic home: the limits [Household.MAX_MEMBERS] / [Household.MAX_PETS] are enforced, not filled. */
    val members: List<HouseholdMember> = listOf(
        localMember,
        HouseholdMember(id = "member-02", name = "Ana Pérez"),
        HouseholdMember(id = "member-03", name = "Aarón Santos"),
        HouseholdMember(id = "member-04", name = "María López"),
        HouseholdMember(id = "member-05", name = "Diego Pérez")
    )

    // Measures are illustrative but consistent per species, so the profile screen reads realistically.
    val pets: List<Pet> = listOf(
        Pet(id = "pet-01", name = "Boyka Sanchez", type = PetType.DOG, breed = "Labrador Retriever", ageYears = 4, weight = 28.0, height = 56.0),
        Pet(id = "pet-02", name = "Luna", type = PetType.CAT, breed = "Siamese", ageYears = 3, weight = 4.0, height = 25.0),
        Pet(id = "pet-03", name = "Kiwi", type = PetType.RABBIT, breed = "Holland Lop", ageYears = 2, weight = 2.0, height = 18.0),
        Pet(id = "pet-04", name = "Milo", type = PetType.DOG, breed = "Beagle", ageYears = 5, weight = 12.0, height = 38.0),
        Pet(id = "pet-05", name = "Nina", type = PetType.BIRD, breed = "Cockatiel", ageYears = 1, weight = 0.1, height = 30.0),
        Pet(id = "pet-06", name = "Coco", type = PetType.FISH, breed = "Goldfish", ageYears = 1, weight = 0.05, height = 6.0),
        Pet(id = "pet-07", name = "Monty", type = PetType.SNAKE, breed = "Ball Python", ageYears = 3, weight = 1.5, height = 120.0),
        Pet(id = "pet-08", name = "Pixel", type = PetType.RODENT, breed = "Hamster", ageYears = 1, weight = 0.12, height = 10.0),
        Pet(id = "pet-09", name = "Tango", type = PetType.BIRD, breed = "Parakeet", ageYears = 4, weight = 0.4, height = 33.0),
        Pet(id = "pet-10", name = "Sheldon", type = PetType.REPTILE, breed = "Box Turtle", ageYears = 6, weight = 1.2, height = 20.0),
        Pet(id = "pet-11", name = "Hopper", type = PetType.AMPHIBIAN, breed = "Tree Frog", ageYears = 2, weight = 0.03, height = 6.0),
        Pet(id = "pet-12", name = "Webster", type = PetType.ARACHNID, breed = "Tarantula", ageYears = 2, weight = 0.05, height = 7.0),
        Pet(id = "pet-13", name = "Clover", type = PetType.POULTRY, breed = "Hen", ageYears = 1, weight = 2.2, height = 40.0),
        Pet(id = "pet-14", name = "Bruno", type = PetType.OTHER, breed = "Mixed", ageYears = 3, weight = 9.0, height = 35.0)
    )

    val household: Household = Household(name = HOUSEHOLD_NAME, members = members, pets = pets)

    /** Avatar lookup by pet name; an unknown name falls back to a neutral icon via [PetType.OTHER]. */
    fun petTypeOf(petName: String): PetType =
        pets.firstOrNull { it.name == petName }?.type ?: PetType.OTHER

    // Anchored to the real current day so the data is live: a full day today, the rest across the weeks ahead.
    private val today: LocalDate = LocalDate.now()

    // Each pet gets one task and one event per category, so the kinds stay balanced across the home.
    private val taskCategories = listOf("Walking", "Feeding", "Play", "Training", "Grooming")
    private val eventCategories = listOf("Vaccination", "Checkup", "Deworming", "Grooming", "Dental")

    // Per-category title pools; the pet index picks one, so two pets rarely read the same title.
    private val taskTitles: Map<String, List<String>> = mapOf(
        "Walking" to listOf("Morning walk", "Afternoon stroll", "Evening walk", "Park visit", "Long hike"),
        "Feeding" to listOf("Breakfast", "Lunch", "Dinner", "Snack time", "Water refill"),
        "Play" to listOf("Playtime", "Fetch session", "Tug of war", "Puzzle toy", "Garden romp"),
        "Training" to listOf("Training session", "Obedience drill", "Recall practice", "Trick practice", "Leash manners"),
        "Grooming" to listOf("Evening brushing", "Coat brushing", "Ear cleaning", "Nail check", "Bath time")
    )
    private val eventTitles: Map<String, List<String>> = mapOf(
        "Vaccination" to listOf("Rabies vaccine", "Annual vaccination", "Booster shot", "Distemper shot", "Parvo vaccine"),
        "Checkup" to listOf("Vet checkup", "Wellness exam", "Weight check", "Senior review", "General checkup"),
        "Deworming" to listOf("Deworming", "Parasite control", "Flea treatment", "Tick treatment", "Antiparasite dose"),
        "Grooming" to listOf("Grooming appointment", "Professional bath", "Summer trim", "Full grooming", "Nail trimming"),
        "Dental" to listOf("Dental cleaning", "Teeth scaling", "Oral exam", "Plaque removal", "Dental review")
    )
    private val taskNotes: Map<String, String> = mapOf(
        "Walking" to "Keep the leash short near the gate.",
        "Feeding" to "Use the usual bowl and fresh water.",
        "Play" to "About twenty minutes of play.",
        "Training" to "Reinforce the basic commands.",
        "Grooming" to "Brush the coat gently."
    )
    private val eventNotes: Map<String, String> = mapOf(
        "Vaccination" to "Bring the vaccination card.",
        "Checkup" to "Mention the recent appetite.",
        "Deworming" to "Follow the vet's dosage.",
        "Grooming" to "Ask for a short summer trim.",
        "Dental" to "Fasting required the night before."
    )

    // Up to six start slots per day; the slot index sets the time, so no two activities share an hour that day.
    private val daySlots: List<LocalTime> = listOf(
        LocalTime.of(7, 0), LocalTime.of(9, 30), LocalTime.of(11, 0),
        LocalTime.of(14, 30), LocalTime.of(16, 0), LocalTime.of(18, 30)
    )

    // Number of days the agenda spreads over; 14 pets * 10 activities / 28 days = five activities per day.
    private const val SPREAD_DAYS = 28

    // Rotated across days and slots so every day shows each state and the cards exercise every button set.
    private val stateCycle = listOf(
        ActivityState.PENDING, ActivityState.CONFIRMED, ActivityState.DONE, ActivityState.CANCELED
    )

    /** A pet's activity before it is placed on the calendar; date and time are assigned during distribution. */
    private class Template(
        val id: String,
        val type: ItemType,
        val petName: String,
        val title: String,
        val category: String,
        val note: String,
        val assignedTo: String,
        val recurrence: Recurrence,
        val reminderAmount: Int,
        val reminderUnit: ReminderUnit
    )

    /** Ten activities per pet (5 tasks + 5 events), spread so every active day holds four to six of them. */
    val activities: List<ScheduledActivity> = buildActivities()

    private fun buildActivities(): List<ScheduledActivity> {
        val templates = buildTemplates()
        // Round-robin into days: each day draws from distant templates, so it mixes pets and both kinds.
        val byDay = templates.withIndex().groupBy({ it.index % SPREAD_DAYS }, { it.value })
        val result = mutableListOf<ScheduledActivity>()
        byDay.forEach { (dayIndex, dayTemplates) ->
            val date = today.plusDays(dayIndex.toLong())
            dayTemplates.forEachIndexed { slot, t ->
                val start = daySlots[slot % daySlots.size]
                val end = if (t.type == ItemType.EVENT) start.plusHours(if (slot % 2 == 0) 1 else 2) else null
                // The four states rotate, so each day carries a mix and the terminal ones carry a resolver.
                val state = stateCycle[(slot + dayIndex) % stateCycle.size]
                val resolved = state == ActivityState.DONE || state == ActivityState.CANCELED
                result += ScheduledActivity(
                    id = t.id,
                    type = t.type,
                    petName = t.petName,
                    title = t.title,
                    category = t.category,
                    date = date,
                    startTime = start,
                    endTime = end,
                    recurrence = t.recurrence,
                    reminderAmount = t.reminderAmount,
                    reminderUnit = t.reminderUnit,
                    assignedTo = t.assignedTo,
                    note = t.note,
                    state = state,
                    resolvedBy = if (resolved) t.assignedTo else null,
                    resolvedAtMillis = if (resolved) System.currentTimeMillis() else null
                )
            }
        }
        return result
    }

    /** Five tasks and five events per pet, with titles, categories and owners varied by pet. */
    private fun buildTemplates(): List<Template> {
        val templates = mutableListOf<Template>()
        pets.forEachIndexed { petIndex, pet ->
            for (i in 0 until 5) {
                val category = taskCategories[i]
                val titles = taskTitles.getValue(category)
                templates += Template(
                    id = "act-${pet.id}-t${i + 1}",
                    type = ItemType.TASK,
                    petName = pet.name,
                    title = titles[petIndex % titles.size],
                    category = category,
                    note = taskNotes.getValue(category),
                    assignedTo = ownerFor(petIndex, i),
                    recurrence = Recurrence.DAILY,
                    reminderAmount = 15,
                    reminderUnit = ReminderUnit.MINUTES
                )
            }
            for (i in 0 until 5) {
                val category = eventCategories[i]
                val titles = eventTitles.getValue(category)
                templates += Template(
                    id = "act-${pet.id}-e${i + 1}",
                    type = ItemType.EVENT,
                    petName = pet.name,
                    title = titles[petIndex % titles.size],
                    category = category,
                    note = eventNotes.getValue(category),
                    assignedTo = ownerFor(petIndex, i),
                    recurrence = Recurrence.ONCE,
                    reminderAmount = 1,
                    reminderUnit = ReminderUnit.HOURS
                )
            }
        }
        return templates
    }

    // The first of each kind stays with the local member, so the watch "assigned to me" view keeps content.
    private fun ownerFor(petIndex: Int, i: Int): String =
        if (i == 0) localMember.name else members[(petIndex + i) % members.size].name
}
