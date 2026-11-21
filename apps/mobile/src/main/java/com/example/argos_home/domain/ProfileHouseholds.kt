package com.example.argos_home.domain

/** A home the local user belongs to, for the Profile list: its color, the user's role there and whether it is active. */
data class ProfileHousehold(
    val name: String,
    val memberCount: Int,
    val role: MemberRole,
    val color: HouseholdColor,
    val active: Boolean
)

// Mobile-only, mirrors HomeHouseholds but adds role and color per home; the shared seed is never touched.
/** Homes shown in Profile: the real home (admin) plus a label-only seed (member); [activeName] marks the active one. */
object ProfileHouseholds {

    /** Maximum number of homes a user may belong to; drives the "N/MAX" counter. */
    const val MAX_HOUSEHOLDS: Int = 8

    /** The user's homes with their role and color; the active flag follows the home selected in the header. */
    fun userHouseholds(activeName: String): List<ProfileHousehold> = listOf(
        ProfileHousehold(
            name = SeedData.HOUSEHOLD_NAME,
            memberCount = SeedData.members.size,
            role = MemberRole.ADMIN,
            color = HouseholdColor.PRIMARY,
            active = SeedData.HOUSEHOLD_NAME == activeName
        ),
        ProfileHousehold(
            name = "Grandma Lucía's House",
            memberCount = 2,
            role = MemberRole.MEMBER,
            color = HouseholdColor.TERTIARY,
            active = "Grandma Lucía's House" == activeName
        )
    )

    /** The user's role in the active home: admin in the real home, member in the label-only one. */
    fun roleIn(activeName: String): MemberRole =
        userHouseholds(activeName).firstOrNull { it.active }?.role ?: MemberRole.MEMBER
}
