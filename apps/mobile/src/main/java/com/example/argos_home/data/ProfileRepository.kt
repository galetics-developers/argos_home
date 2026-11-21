package com.example.argos_home.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.argos_home.domain.UserProfile
import java.time.LocalDate

// Mobile-only account state: the editable profile lives here, derived from the seed; it never touches SeedData or the watch.
/** In-memory store for the Profile module; edits stay on the phone and do not persist across restarts. */
object ProfileRepository {

    /** The local user's editable account, seeded from the prototype values. */
    var profile: UserProfile by mutableStateOf(
        UserProfile(
            firstNames = "Cesar David",
            lastNames = "Sanchez Trejo",
            email = "cesar@example.com",
            birthDate = LocalDate.of(2005, 8, 19),
            phoneDigits = "9842765180"
        )
    )
        private set

    /** Replaces the editable personal fields; keeps the current email untouched. */
    fun updateProfile(firstNames: String, lastNames: String, birthDate: LocalDate, phoneDigits: String) {
        profile = profile.copy(
            firstNames = firstNames.trim(),
            lastNames = lastNames.trim(),
            birthDate = birthDate,
            phoneDigits = phoneDigits
        )
    }

    /** Updates just the email, reflected in the header. */
    fun updateEmail(newEmail: String) {
        profile = profile.copy(email = newEmail.trim())
    }

    // Until the user changes it this session, the current password is the project's shared demo value (single source).
    private var changedPassword: String? = null

    /** True when [text] is the current password: the new one if changed this session, else the project demo value. */
    fun isCurrentPassword(text: String): Boolean =
        changedPassword?.let { it == text } ?: HouseholdRepository.isPasswordCorrect(text)

    /** Stores a new password in memory for the rest of the session (demo only, never a real credential). */
    fun changePassword(newPassword: String) {
        changedPassword = newPassword
    }
}
