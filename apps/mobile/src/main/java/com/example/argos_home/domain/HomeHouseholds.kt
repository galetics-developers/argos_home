package com.example.argos_home.domain

/** A home the user belongs to, for the Home switcher; multi-home is a label here, so only name and size are kept. */
data class HouseholdSummary(
    val name: String,
    val memberCount: Int
)

// Mobile-only, so the shared SeedData/Household stay byte-identical with the watch; switching a home only relabels.
/** Homes shown in the Home switcher: the real home with its true member count, plus label-only seeds. */
object HomeHouseholds {
    val all: List<HouseholdSummary> = listOf(
        HouseholdSummary(name = SeedData.HOUSEHOLD_NAME, memberCount = SeedData.members.size),
        HouseholdSummary(name = "Grandma Lucía's House", memberCount = 2)
    )
}
