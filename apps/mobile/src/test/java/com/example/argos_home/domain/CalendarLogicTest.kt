package com.example.argos_home.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
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
        assertEquals(listOf(cancel, done), TransitionRules.allowedTargets(ActivityState.PENDING, ItemType.TASK))
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
