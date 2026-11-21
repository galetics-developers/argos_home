package com.example.argos_home.domain

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/** Editable account data for the local user; mobile-only, never crosses the Data Layer nor touches SeedData. */
data class UserProfile(
    val firstNames: String,
    val lastNames: String,
    val email: String,
    val birthDate: LocalDate,
    val phoneDigits: String
)

/** Short display name: first given name + first surname (e.g. "Cesar Sanchez"), base for the header and initials. */
fun displayName(profile: UserProfile): String {
    val first = profile.firstNames.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    val last = profile.lastNames.trim().split(Regex("\\s+")).firstOrNull().orEmpty()
    return listOf(first, last).filter { it.isNotEmpty() }.joinToString(" ")
}

/** Keeps only digits, capped at ten, so the phone field stores a clean national number. */
fun onlyDigits(text: String): String = text.filter { it.isDigit() }.take(10)

/** Groups up to ten digits as "984 276 5180" (3-3-4); shorter input is grouped as far as it reaches. */
fun formatPhone(digits: String): String {
    val d = onlyDigits(digits)
    return buildString {
        d.forEachIndexed { i, c ->
            if (i == 3 || i == 6) append(' ')
            append(c)
        }
    }
}

private val BIRTH_DATE_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

/** Birth date as dd/MM/yyyy for the field and the stored profile. */
fun formatBirthDate(date: LocalDate): String = date.format(BIRTH_DATE_FORMAT)
