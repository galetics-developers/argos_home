package com.example.argos_home.presentation.wearable

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Verifies the node choice rule without Data Layer types: nearby wins, else the first, else null. */
class PhoneNodeChoiceTest {

    @Test
    fun prefersNearbyNode() {
        val candidates = listOf(
            NodeChoice(id = "far", isNearby = false),
            NodeChoice(id = "near", isNearby = true)
        )
        assertEquals("near", pickPreferredNode(candidates))
    }

    @Test
    fun fallsBackToFirstWhenNoneNearby() {
        val candidates = listOf(
            NodeChoice(id = "first", isNearby = false),
            NodeChoice(id = "second", isNearby = false)
        )
        assertEquals("first", pickPreferredNode(candidates))
    }

    @Test
    fun returnsNullWhenEmpty() {
        assertNull(pickPreferredNode(emptyList()))
    }
}
