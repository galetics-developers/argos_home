package com.example.argos_home.domain

import com.example.argos_home.data.MedicalRecordRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/** Verifies the pure logic that drives the state-dependent buttons, the date/time labels and the seed. */
class CalendarLogicTest {

    // Which buttons appear is exactly allowedTargets, so these cases pin the whole button behavior on both devices.
    @Test
    fun allowedTargets_dependOnStateAndType() {
        val cancel = ActivityState.CANCELED
        val done = ActivityState.DONE
        val confirm = ActivityState.CONFIRMED

        assertEquals(listOf(cancel, done, confirm), TransitionRules.allowedTargets(ActivityState.PENDING, ItemType.EVENT))
        assertEquals(listOf(cancel, done, confirm), TransitionRules.allowedTargets(ActivityState.PENDING, ItemType.TASK))
        assertEquals(listOf(cancel, done), TransitionRules.allowedTargets(ActivityState.CONFIRMED, ItemType.EVENT))
        assertEquals(listOf(cancel, done), TransitionRules.allowedTargets(ActivityState.CONFIRMED, ItemType.TASK))
        assertTrue(TransitionRules.allowedTargets(ActivityState.DONE, ItemType.EVENT).isEmpty())
        assertTrue(TransitionRules.allowedTargets(ActivityState.CANCELED, ItemType.TASK).isEmpty())
    }

    @Test
    fun time_formatsInUsEnglishTwelveHour() {
        assertEquals("6:00 AM", ActivityFormats.time(LocalTime.of(6, 0)))
        assertEquals("1:05 PM", ActivityFormats.time(LocalTime.of(13, 5)))
        assertEquals("12:00 PM", ActivityFormats.time(LocalTime.of(12, 0)))
    }

    @Test
    fun timeRange_compressesSharedPeriodAndKeepsDistinctOnes() {
        assertEquals("9:00–11:00 AM", ActivityFormats.timeRange(LocalTime.of(9, 0), LocalTime.of(11, 0)))
        assertEquals("11:00 AM–1:00 PM", ActivityFormats.timeRange(LocalTime.of(11, 0), LocalTime.of(13, 0)))
        assertEquals("9:00 AM", ActivityFormats.timeRange(LocalTime.of(9, 0), null))
    }

    @Test
    fun dateChip_saysTodayOnlyForTheCurrentDay() {
        val today = LocalDate.of(2026, 11, 17)
        assertEquals("Today", ActivityFormats.dateChip(today, today))
        assertEquals("Nov 18", ActivityFormats.dateChip(today.plusDays(1), today))
    }

    @Test
    fun reminderValue_isTheStartMinusTheLeadForMinuteReminders() {
        val activity = sample(start = LocalTime.of(7, 0), reminder = 15, unit = ReminderUnit.MINUTES)
        assertEquals("6:45 AM", ActivityFormats.reminderValue(activity))
    }

    @Test
    fun seed_isConsistentAndRelatedPerPet() {
        val today = LocalDate.now()
        assertEquals(14, SeedData.pets.size)
        assertEquals(5, SeedData.members.size)
        assertEquals(30, Household.MAX_PETS)
        assertEquals(21, Household.MAX_MEMBERS)

        // Each pet owns exactly ten activities: five tasks and five events, related by pet and not by home.
        assertEquals(SeedData.pets.size * 10, SeedData.activities.size)
        SeedData.pets.forEach { pet ->
            val own = SeedData.activities.filter { it.petName == pet.name }
            assertEquals(10, own.size)
            assertEquals(5, own.count { it.type == ItemType.TASK })
            assertEquals(5, own.count { it.type == ItemType.EVENT })
        }

        // Ids are unique, nothing is scheduled in the past, and some activities fall today.
        assertEquals(SeedData.activities.size, SeedData.activities.map { it.id }.toSet().size)
        assertTrue(SeedData.activities.none { it.date.isBefore(today) })
        assertTrue(SeedData.activities.any { it.date == today })

        assertEquals(PetType.CAT, SeedData.petTypeOf("Luna"))
        assertEquals(PetType.OTHER, SeedData.petTypeOf("Nobody"))
    }

    @Test
    fun subtitle_isDerivedFromCategoryAndPet() {
        val activity = sample(start = LocalTime.of(8, 0), reminder = 10, unit = ReminderUnit.MINUTES)
        assertEquals("Walking · Boyka Sanchez", activity.subtitle)
        assertFalse(activity.subtitle.isBlank())
    }

    // The medical record status is never chosen by hand: it is derived from the date, and Canceled is preserved.
    @Test
    fun effectiveStatus_derivesFromDateAndKeepsCanceled() {
        val today = LocalDate.of(2026, 11, 20)
        assertEquals(MedicalRecordStatus.PENDING, effectiveStatus(medRecord(today.plusDays(3), MedicalRecordStatus.PENDING), today))
        assertEquals(MedicalRecordStatus.REGISTERED, effectiveStatus(medRecord(today.minusDays(3), MedicalRecordStatus.PENDING), today))
        assertEquals(MedicalRecordStatus.PENDING, effectiveStatus(medRecord(today, MedicalRecordStatus.PENDING), today))
        assertEquals(MedicalRecordStatus.CANCELED, effectiveStatus(medRecord(today.minusDays(3), MedicalRecordStatus.CANCELED), today))
    }

    // Every pet owns exactly ten seeded medical records, local to the phone and never shared with the watch.
    @Test
    fun medicalRecords_seedTenPerPet() {
        SeedData.pets.forEach { pet ->
            assertEquals(10, MedicalRecordRepository.recordsFor(pet.id).size)
        }
    }

    // A time range is valid only when the end is strictly after the start; an open end is allowed.
    @Test
    fun timeRange_endMustBeAfterStart() {
        assertTrue(isValidTimeRange(LocalTime.of(14, 0), LocalTime.of(15, 0)))
        assertFalse(isValidTimeRange(LocalTime.of(14, 0), LocalTime.of(1, 0)))
        assertFalse(isValidTimeRange(LocalTime.of(14, 0), LocalTime.of(14, 0)))
        assertTrue(isValidTimeRange(null, LocalTime.of(1, 0)))
        assertTrue(isValidTimeRange(LocalTime.of(14, 0), null))
    }

    // The Home ring and counters ignore CANCELED, so the day's base is PENDING + CONFIRMED + DONE only.
    @Test
    fun homeSummary_countsExcludeCanceledAndComputePercent() {
        val today = LocalDate.of(2026, 11, 20)
        val midnight = LocalDateTime.of(today, LocalTime.MIDNIGHT)
        val activities = listOf(
            homeAct("h1", today, LocalTime.of(8, 0), ActivityState.DONE, "Done one"),
            homeAct("h2", today, LocalTime.of(9, 0), ActivityState.DONE, "Done two"),
            homeAct("h3", today, LocalTime.of(10, 0), ActivityState.PENDING, "Pending one"),
            homeAct("h4", today, LocalTime.of(11, 0), ActivityState.CONFIRMED, "Confirmed one"),
            homeAct("h5", today, LocalTime.of(12, 0), ActivityState.CANCELED, "Canceled one"),
            homeAct("h6", today.plusDays(1), LocalTime.of(9, 0), ActivityState.PENDING, "Tomorrow one")
        )
        val summary = HomeSummary.of(activities, today, midnight)

        assertEquals(4, summary.total)
        assertEquals(1, summary.pending)
        assertEquals(2, summary.completed)
        assertEquals(50, summary.percent)
        assertEquals(0.5f, summary.fraction, 0.0001f)
        // Next event is the soonest still-pending activity from now on.
        assertEquals("Pending one", summary.nextEventTitle)
        // Upcoming keeps only open activities, soonest first; DONE and CANCELED are left out.
        assertEquals(listOf("Pending one", "Confirmed one", "Tomorrow one"), summary.upcoming.map { it.title })
    }

    @Test
    fun homeSummary_zeroTotalGivesZeroPercent() {
        val today = LocalDate.of(2026, 11, 20)
        val onlyCanceled = listOf(
            homeAct("h1", today, LocalTime.of(8, 0), ActivityState.CANCELED, "Canceled")
        )
        val summary = HomeSummary.of(onlyCanceled, today, LocalDateTime.of(today, LocalTime.MIDNIGHT))
        assertEquals(0, summary.total)
        assertEquals(0, summary.percent)
        assertEquals(0f, summary.fraction, 0.0001f)
        assertEquals(null, summary.nextEventTitle)
    }

    @Test
    fun homeSummary_nextEventSkipsActivitiesAlreadyStarted() {
        val today = LocalDate.of(2026, 11, 20)
        val now = LocalDateTime.of(today, LocalTime.of(10, 0))
        val activities = listOf(
            homeAct("past", today, LocalTime.of(8, 0), ActivityState.PENDING, "Earlier today"),
            homeAct("next", today, LocalTime.of(14, 0), ActivityState.PENDING, "Later today")
        )
        assertEquals("Later today", HomeSummary.of(activities, today, now).nextEventTitle)
    }

    @Test
    fun userInitials_takesFirstNameAndSurname() {
        assertEquals("CS", userInitials("Cesar Sánchez"))
        assertEquals("AP", userInitials("  ana  pérez  lópez "))
        assertEquals("M", userInitials("Madonna"))
        assertEquals("", userInitials("   "))
    }

    @Test
    fun greetingFor_bucketsByHour() {
        assertEquals(Greeting.MORNING, greetingFor(LocalTime.of(0, 0)))
        assertEquals(Greeting.MORNING, greetingFor(LocalTime.of(11, 59)))
        assertEquals(Greeting.AFTERNOON, greetingFor(LocalTime.of(12, 0)))
        assertEquals(Greeting.AFTERNOON, greetingFor(LocalTime.of(17, 59)))
        assertEquals(Greeting.EVENING, greetingFor(LocalTime.of(18, 0)))
        assertEquals(Greeting.EVENING, greetingFor(LocalTime.of(23, 30)))
    }

    private fun homeAct(
        id: String,
        date: LocalDate,
        start: LocalTime,
        state: ActivityState,
        title: String
    ): ScheduledActivity =
        ScheduledActivity(
            id = id,
            type = ItemType.TASK,
            petName = "Boyka Sanchez",
            title = title,
            category = "Walking",
            date = date,
            startTime = start,
            recurrence = Recurrence.DAILY,
            reminderAmount = 15,
            reminderUnit = ReminderUnit.MINUTES,
            assignedTo = "Cesar Sánchez",
            note = "note",
            state = state
        )

    private fun medRecord(date: LocalDate, status: MedicalRecordStatus): MedicalRecord =
        MedicalRecord(
            id = "rec-01",
            petId = "pet-01",
            type = MedicalRecordType.VACCINE,
            title = "Rabies booster",
            date = date,
            status = status
        )

    private fun sample(start: LocalTime, reminder: Int, unit: ReminderUnit): ScheduledActivity =
        ScheduledActivity(
            id = "test-01",
            type = ItemType.TASK,
            petName = "Boyka Sanchez",
            title = "Morning Walk",
            category = "Walking",
            date = LocalDate.now(),
            startTime = start,
            recurrence = Recurrence.DAILY,
            reminderAmount = reminder,
            reminderUnit = unit,
            assignedTo = "Cesar Sánchez",
            note = "note"
        )
}
