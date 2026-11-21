package com.example.argos_home.domain

import kotlin.random.Random

/** State of an invitation; this release only ever creates pending ones (no backend to accept them). */
enum class InviteStatus { PENDING }

/** A pending invitation shown in the "Pending invitations" list. */
data class Invitation(
    val id: String,
    val email: String,
    val status: InviteStatus = InviteStatus.PENDING
)

/** The single-use invite: a 6-digit code and the shareable link that carries it. Valid for 10 minutes. */
data class InviteCode(
    val code: String,
    val link: String
)

/** Six digits with leading zeros kept, so the code is always exactly six characters. */
fun newSixDigitCode(random: Random = Random.Default): String =
    (0 until 6).joinToString("") { random.nextInt(10).toString() }

/** Fresh invite: a new code plus its join link (demo link; without a backend there is no destination). */
fun newInviteCode(random: Random = Random.Default): InviteCode {
    val code = newSixDigitCode(random)
    return InviteCode(code = code, link = "https://argoshome.app/join/$code")
}

// Deliberately lenient: one @, a dot in the domain, no spaces; enough to reject obvious typos without a backend.
/** Basic e-mail format check, pure so it is unit-tested on the JVM. */
fun isValidEmail(text: String): Boolean =
    Regex("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$").matches(text.trim())

/** How long an invite code stays valid before it regenerates. */
const val INVITE_VALIDITY_SECONDS: Int = 10 * 60
