package com.example.argos_home.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate

/** Pure-logic tests for the Profile module: display name, phone and date formatting, validation and the home list. */
class ProfileLogicTest {

    private val sample = UserProfile(
        firstNames = "Cesar David",
        lastNames = "Sanchez Trejo",
        email = "cesar@example.com",
        birthDate = LocalDate.of(2005, 8, 19),
        phoneDigits = "9842765180"
    )

    @Test
    fun displayName_takesFirstGivenNameAndFirstSurname() {
        assertEquals("Cesar Sanchez", displayName(sample))
        assertEquals("CS", userInitials(displayName(sample)))
    }

    @Test
    fun onlyDigits_stripsAndCapsAtTen() {
        assertEquals("9842765180", onlyDigits("(984) 276-5180"))
        assertEquals("9842765180", onlyDigits("98427651800000"))
    }

    @Test
    fun formatPhone_groupsThreeThreeFour() {
        assertEquals("984 276 5180", formatPhone("9842765180"))
        assertEquals("984 27", formatPhone("98427"))
    }

    @Test
    fun formatBirthDate_isDayMonthYear() {
        assertEquals("19/08/2005", formatBirthDate(sample.birthDate))
    }

    @Test
    fun securePassword_requiresLengthLetterAndDigit() {
        assertTrue(isSecurePassword("argos2026"))
        assertFalse(isSecurePassword("short1"))
        assertFalse(isSecurePassword("onlyletters"))
        assertFalse(isSecurePassword("12345678"))
    }

    @Test
    fun passwordsMatch_rejectsEmptyAndMismatch() {
        assertTrue(passwordsMatch("argos2026", "argos2026"))
        assertFalse(passwordsMatch("argos2026", "argos2027"))
        assertFalse(passwordsMatch("", ""))
    }

    @Test
    fun email_acceptsValidRejectsTypos() {
        assertTrue(isValidEmail("cesar@example.com"))
        assertFalse(isValidEmail("cesar@example"))
        assertFalse(isValidEmail("cesar example.com"))
    }

    @Test
    fun userHouseholds_realHomeIsAdminAndActive() {
        val homes = ProfileHouseholds.userHouseholds(SeedData.HOUSEHOLD_NAME)
        assertEquals(2, homes.size)
        assertTrue(homes.size <= ProfileHouseholds.MAX_HOUSEHOLDS)
        val perez = homes.first { it.name == SeedData.HOUSEHOLD_NAME }
        assertEquals(MemberRole.ADMIN, perez.role)
        assertTrue(perez.active)
        assertEquals(SeedData.members.size, perez.memberCount)
    }

    @Test
    fun roleIn_matchesActiveHome() {
        assertEquals(MemberRole.ADMIN, ProfileHouseholds.roleIn(SeedData.HOUSEHOLD_NAME))
        assertEquals(MemberRole.MEMBER, ProfileHouseholds.roleIn("Grandma Lucía's House"))
    }
}
