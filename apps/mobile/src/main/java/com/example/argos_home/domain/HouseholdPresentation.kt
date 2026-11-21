package com.example.argos_home.domain

/** A household member with its mobile-only role, for the Household screens. */
data class HouseholdMemberUi(
    val id: String,
    val name: String,
    val role: MemberRole
)

/** Seed roles and ordering: the local member is the admin and comes first, the rest keep seed order as members. */
fun seedMembers(members: List<HouseholdMember>, localId: String): List<HouseholdMemberUi> {
    val local = members.firstOrNull { it.id == localId }
    val others = members.filter { it.id != localId }
    val ordered = listOfNotNull(local) + others
    return ordered.map { HouseholdMemberUi(it.id, it.name, if (it.id == localId) MemberRole.ADMIN else MemberRole.MEMBER) }
}

/** New roster after handing admin to [toId]: the former admin [fromId] becomes a member, [toId] becomes admin. */
fun transferAdmin(members: List<HouseholdMemberUi>, fromId: String, toId: String): List<HouseholdMemberUi> =
    members.map {
        when (it.id) {
            toId -> it.copy(role = MemberRole.ADMIN)
            fromId -> it.copy(role = MemberRole.MEMBER)
            else -> it
        }
    }

/** Only an admin manages members (remove, invite, transfer); a member sees just "Leave household". */
fun canManageMembers(role: MemberRole): Boolean = role == MemberRole.ADMIN

/** Members other than the local user, for the remove icons and the transfer picker. */
fun otherMembers(members: List<HouseholdMemberUi>, localId: String): List<HouseholdMemberUi> =
    members.filter { it.id != localId }
