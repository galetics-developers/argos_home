package com.example.argos_home.ui.pets

import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.domain.ActivityState
import java.time.LocalDate

/** Today's care progress of one pet: done over total, with canceled activities excluded. */
data class PetDayProgress(val done: Int, val total: Int) {
    val fraction: Float get() = if (total == 0) 0f else done.toFloat() / total
}

/** Done = confirmed or completed today; total = today's activities that are not canceled. */
fun petDayProgress(petName: String): PetDayProgress {
    val today = ActivityRepository.activitiesOn(LocalDate.now(), petName)
        .filter { it.state != ActivityState.CANCELED }
    val done = today.count { it.state == ActivityState.CONFIRMED || it.state == ActivityState.DONE }
    return PetDayProgress(done = done, total = today.size)
}
