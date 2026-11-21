package com.example.argos_home.domain

// Minimum local rule; the exact requirements are never surfaced to the user, only a generic error.
/** Secure-password check: at least eight characters with a letter and a digit. */
fun isSecurePassword(text: String): Boolean =
    text.length >= 8 && text.any { it.isLetter() } && text.any { it.isDigit() }

/** The new password and its confirmation must match and not be empty. */
fun passwordsMatch(newPassword: String, confirm: String): Boolean =
    newPassword.isNotEmpty() && newPassword == confirm
