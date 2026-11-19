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

    // Anchored to the real current day so the data is live: some activities today, the rest across the weeks ahead.
    private val today: LocalDate = LocalDate.now()

    // Five tasks per pet; the day offsets keep two of them today for the "today" progress and the watch "nearest".
    private val taskTitles = listOf("Morning walk", "Breakfast", "Playtime", "Training session", "Evening brushing")
    private val taskCategories = listOf("Walking", "Feeding", "Play", "Training", "Grooming")
    private val taskNotes = listOf(
        "Keep the leash short near the gate.",
        "Use the usual bowl and fresh water.",
        "About twenty minutes of play.",
        "Reinforce the basic commands.",
        "Brush the coat gently."
    )
    private val taskDayOffsets = listOf(0L, 0L, 2L, 5L, 9L)

    // Five events per pet, spread over the weeks ahead so the calendar and the watch have a realistic agenda.
    private val eventTitles = listOf("Vaccination", "Vet checkup", "Deworming", "Grooming appointment", "Dental cleaning")
    private val eventCategories = listOf("Vaccination", "Checkup", "Deworming", "Grooming", "Dental")
    private val eventNotes = listOf(
        "Bring the vaccination card.",
        "Mention the recent appetite.",
        "Follow the vet's dosage.",
        "Ask for a short summer trim.",
        "Fasting required the night before."
    )
    private val eventDayOffsets = listOf(1L, 4L, 7L, 12L, 18L)

    /** Ten activities per pet (5 tasks + 5 events), generated so each pet owns its own schedule, related by pet. */
    val activities: List<ScheduledActivity> = pets.flatMapIndexed { petIndex, pet ->
        buildActivitiesFor(pet, petIndex)
    }

    /** The ten activities of one pet; a few of today's tasks start resolved so the progress bars vary across pets. */
    private fun buildActivitiesFor(pet: Pet, petIndex: Int): List<ScheduledActivity> {
        val result = mutableListOf<ScheduledActivity>()
        val doneToday = petIndex % 3
        for (i in 0 until 5) {
            val member = if (i == 0) localMember.name else members[(petIndex + i) % members.size].name
            val isDone = taskDayOffsets[i] == 0L && i < doneToday
            result += ScheduledActivity(
                id = "act-${pet.id}-t${i + 1}",
                type = ItemType.TASK,
                petName = pet.name,
                title = taskTitles[i],
                category = taskCategories[i],
                date = today.plusDays(taskDayOffsets[i]),
                startTime = LocalTime.of(7 + i * 2, 0),
                recurrence = Recurrence.DAILY,
                reminderAmount = 15,
                reminderUnit = ReminderUnit.MINUTES,
                assignedTo = member,
                note = taskNotes[i],
                state = if (isDone) ActivityState.DONE else ActivityState.PENDING,
                resolvedBy = if (isDone) member else null,
                resolvedAtMillis = if (isDone) System.currentTimeMillis() else null
            )
        }
        for (i in 0 until 5) {
            val member = if (i == 0) localMember.name else members[(petIndex + i) % members.size].name
            val start = LocalTime.of(9 + i, 0)
            result += ScheduledActivity(
                id = "act-${pet.id}-e${i + 1}",
                type = ItemType.EVENT,
                petName = pet.name,
                title = eventTitles[i],
                category = eventCategories[i],
                date = today.plusDays(eventDayOffsets[i]),
                startTime = start,
                endTime = start.plusHours(2),
                recurrence = Recurrence.ONCE,
                reminderAmount = 1,
                reminderUnit = ReminderUnit.HOURS,
                assignedTo = member,
                note = eventNotes[i]
            )
        }
        return result
    }
}
