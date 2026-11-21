package com.example.argos_home.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Pure-logic tests for the Household module: roles, transfer, invite code and email validation. */
class HouseholdLogicTest {

    private val localId = "member-01"

    @Test
    fun seed_makesLocalAdminFirst_restMembers() {
        val seeded = seedMembers(SeedData.members, localId)
        assertEquals(SeedData.members.size, seeded.size)
        assertEquals(localId, seeded.first().id)
        assertEquals(MemberRole.ADMIN, seeded.first().role)
        assertTrue(seeded.drop(1).all { it.role == MemberRole.MEMBER })
    }

    @Test
    fun transfer_swapsAdminAndMember() {
        val seeded = seedMembers(SeedData.members, localId)
        val target = seeded.first { it.id != localId }.id
        val after = transferAdmin(seeded, fromId = localId, toId = target)
        assertEquals(MemberRole.MEMBER, after.first { it.id == localId }.role)
        assertEquals(MemberRole.ADMIN, after.first { it.id == target }.role)
        // Exactly one admin remains after a transfer.
        assertEquals(1, after.count { it.role == MemberRole.ADMIN })
    }

    @Test
    fun canManageMembers_onlyAdmin() {
        assertTrue(canManageMembers(MemberRole.ADMIN))
        assertFalse(canManageMembers(MemberRole.MEMBER))
    }

    @Test
    fun otherMembers_excludesLocal() {
        val seeded = seedMembers(SeedData.members, localId)
        val others = otherMembers(seeded, localId)
        assertEquals(seeded.size - 1, others.size)
        assertTrue(others.none { it.id == localId })
    }

    @Test
    fun sixDigitCode_isAlwaysSixDigits() {
        repeat(50) {
            val code = newSixDigitCode()
            assertEquals(6, code.length)
            assertTrue(code.all { it.isDigit() })
        }
    }

    @Test
    fun email_acceptsValid_rejectsInvalid() {
        assertTrue(isValidEmail("ana.torres@example.com"))
        assertTrue(isValidEmail("name@example.org"))
        assertFalse(isValidEmail("no-at-sign"))
        assertFalse(isValidEmail("missing@domain"))
        assertFalse(isValidEmail("spaces @example.com"))
        assertFalse(isValidEmail(""))
    }
}
