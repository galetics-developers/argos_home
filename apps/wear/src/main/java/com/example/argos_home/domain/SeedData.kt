package com.example.argos_home.domain

import java.time.LocalDate
import java.time.LocalTime

/** Static seed of the home and its activities; the watch keeps its own copy, so both copies must stay identical. */
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

    val pets: List<Pet> = listOf(
        Pet(id = "pet-01", name = "Boyka Sanchez", type = PetType.DOG, breed = "Labrador Retriever"),
        Pet(id = "pet-02", name = "Luna", type = PetType.CAT, breed = "Siamese"),
        Pet(id = "pet-03", name = "Kiwi", type = PetType.RABBIT, breed = "Holland Lop"),
        Pet(id = "pet-04", name = "Milo", type = PetType.DOG, breed = "Beagle"),
        Pet(id = "pet-05", name = "Nina", type = PetType.BIRD, breed = "Cockatiel"),
        Pet(id = "pet-06", name = "Coco", type = PetType.FISH, breed = "Goldfish")
    )

    val household: Household = Household(name = HOUSEHOLD_NAME, members = members, pets = pets)

    /** Avatar lookup by pet name; an unknown name falls back to a neutral icon via [PetType.OTHER]. */
    fun petTypeOf(petName: String): PetType =
        pets.firstOrNull { it.name == petName }?.type ?: PetType.OTHER

    // Anchored to the real current day so the data is live: some activities today, the rest spread across this month.
    private val today: LocalDate = LocalDate.now()

    /** The seeded activities of the home, recomputed from today's date so the calendar is never stale. */
    val activities: List<ScheduledActivity> = listOf(
        ScheduledActivity(
            id = "act-01",
            type = ItemType.TASK,
            petName = "Boyka Sanchez",
            title = "Morning Walk",
            category = "Walking",
            date = today,
            startTime = LocalTime.of(7, 0),
            recurrence = Recurrence.DAILY,
            reminderAmount = 15,
            reminderUnit = ReminderUnit.MINUTES,
            assignedTo = "Cesar Sánchez",
            note = "Keep the leash short near the park gate."
        ),
        ScheduledActivity(
            id = "act-02",
            type = ItemType.TASK,
            petName = "Luna",
            title = "Morning Meal",
            category = "Feeding",
            date = today,
            startTime = LocalTime.of(8, 0),
            recurrence = Recurrence.DAILY,
            reminderAmount = 15,
            reminderUnit = ReminderUnit.MINUTES,
            assignedTo = "María López",
            note = "Use the blue bowl and add a little water."
        ),
        ScheduledActivity(
            id = "act-03",
            type = ItemType.TASK,
            petName = "Boyka Sanchez",
            title = "Evening Walk",
            category = "Walking",
            date = today,
            startTime = LocalTime.of(18, 0),
            recurrence = Recurrence.DAILY,
            reminderAmount = 15,
            reminderUnit = ReminderUnit.MINUTES,
            assignedTo = "Cesar Sánchez",
            note = "Take the long route if the weather is dry."
        ),
        ScheduledActivity(
            id = "act-04",
            type = ItemType.TASK,
            petName = "Kiwi",
            title = "Evening Meal",
            category = "Feeding",
            date = today,
            startTime = LocalTime.of(18, 30),
            recurrence = Recurrence.DAILY,
            reminderAmount = 15,
            reminderUnit = ReminderUnit.MINUTES,
            assignedTo = "Cesar Sánchez",
            note = "Refill the seed cup and change the water."
        ),
        ScheduledActivity(
            id = "act-05",
            type = ItemType.EVENT,
            petName = "Boyka Sanchez",
            title = "Vaccination",
            category = "Appointment",
            date = today.plusDays(1),
            startTime = LocalTime.of(9, 0),
            endTime = LocalTime.of(11, 0),
            recurrence = Recurrence.ONCE,
            reminderAmount = 30,
            reminderUnit = ReminderUnit.MINUTES,
            assignedTo = "Ana Pérez",
            note = "Bring vaccination card and recent stool sample."
        ),
        ScheduledActivity(
            id = "act-06",
            type = ItemType.TASK,
            petName = "Coco",
            title = "Tank Cleaning",
            category = "Care",
            date = today.plusDays(1),
            startTime = LocalTime.of(19, 0),
            recurrence = Recurrence.WEEKLY,
            reminderAmount = 1,
            reminderUnit = ReminderUnit.HOURS,
            assignedTo = "Diego Pérez",
            note = "Replace a third of the water, do not empty the tank."
        ),
        ScheduledActivity(
            id = "act-07",
            type = ItemType.EVENT,
            petName = "Milo",
            title = "Grooming",
            category = "Grooming",
            date = today.plusDays(3),
            startTime = LocalTime.of(10, 0),
            endTime = LocalTime.of(11, 30),
            recurrence = Recurrence.ONCE,
            reminderAmount = 1,
            reminderUnit = ReminderUnit.HOURS,
            assignedTo = "Aarón Santos",
            note = "Ask for a short summer cut around the paws."
        ),
        ScheduledActivity(
            id = "act-08",
            type = ItemType.EVENT,
            petName = "Luna",
            title = "Vet Checkup",
            category = "Appointment",
            date = today.plusDays(5),
            startTime = LocalTime.of(16, 0),
            endTime = LocalTime.of(16, 45),
            recurrence = Recurrence.ONCE,
            reminderAmount = 1,
            reminderUnit = ReminderUnit.DAYS,
            assignedTo = "Cesar Sánchez",
            note = "Mention she has been eating less this week."
        )
    )
}
