package com.example.argos_home.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.argos_home.domain.HouseholdColor
import com.example.argos_home.domain.HouseholdMemberUi
import com.example.argos_home.domain.Household
import com.example.argos_home.domain.InviteCode
import com.example.argos_home.domain.Invitation
import com.example.argos_home.domain.MemberRole
import com.example.argos_home.domain.SeedData
import com.example.argos_home.domain.newInviteCode
import com.example.argos_home.domain.seedMembers
import com.example.argos_home.domain.transferAdmin

// Mobile-only Household state: roles, invitations and the invite code live here, derived from the read-only seed.
/** In-memory store for the Household module; mutations stay on the phone and never touch SeedData or the watch. */
object HouseholdRepository {

    /** The user this install acts as, admin at start; the same local member used across the app. */
    val localMemberId: String = SeedData.localMember.id

    val householdName: String = SeedData.HOUSEHOLD_NAME
    val color: HouseholdColor = HouseholdColor.PRIMARY
    val maxMembers: Int = Household.MAX_MEMBERS

    private val membersState: SnapshotStateList<HouseholdMemberUi> =
        mutableStateListOf<HouseholdMemberUi>().apply { addAll(seedMembers(SeedData.members, localMemberId)) }

    /** Observable members with their roles; the local member is first. */
    val members: List<HouseholdMemberUi> get() = membersState

    /** The local user's current role; drives the admin-only tools and remove icons. */
    val localRole: MemberRole
        get() = membersState.firstOrNull { it.id == localMemberId }?.role ?: MemberRole.MEMBER

    fun nameOf(id: String): String = membersState.firstOrNull { it.id == id }?.name ?: ""

    /** Removes a member from this session's roster (never the local user). */
    fun removeMember(id: String) {
        if (id != localMemberId) membersState.removeAll { it.id == id }
    }

    /** Hands admin to [id]: the local user becomes a member and [id] becomes the admin. */
    fun transferAdminTo(id: String) {
        val next = transferAdmin(membersState.toList(), fromId = localMemberId, toId = id)
        membersState.clear()
        membersState.addAll(next)
    }

    /** Demo leave: without accounts there is no home to land in, so the seed is kept and the screen just returns. */
    fun leave() { /* no-op in this release: without accounts there is no home to land in, so the seed is kept */ }

    // --- Invitations --------------------------------------------------------

    private val invitationsState: SnapshotStateList<Invitation> =
        mutableStateListOf(Invitation(id = "inv-seed-01", email = "ana.torres@example.com"))

    val invitations: List<Invitation> get() = invitationsState

    private var invitationSeq = 1

    fun addInvitation(email: String) {
        invitationSeq += 1
        invitationsState.add(Invitation(id = "inv-${invitationSeq}", email = email.trim()))
    }

    fun removeInvitation(id: String) {
        invitationsState.removeAll { it.id == id }
    }

    // --- Invite code (QR + 6 digits) ---------------------------------------

    var inviteCode: InviteCode by mutableStateOf(newInviteCode())
        private set

    /** New code + link when the countdown expires or on demand; the old one stops being valid. */
    fun regenerateCode() {
        inviteCode = newInviteCode()
    }

    // --- Transfer confirmation ---------------------------------------------

    // Not a real credential: a demo value to exercise the confirm flow and its generic error.
    private const val DEMO_PASSWORD = "argos2026"

    /** True only for the demo password; a wrong or empty entry yields the generic error, never a hint. */
    fun isPasswordCorrect(text: String): Boolean = text == DEMO_PASSWORD
}
