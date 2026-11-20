package com.example.argos_home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.argos_home.domain.SeedData
import com.example.argos_home.ui.calendar.CalendarScreen
import com.example.argos_home.ui.calendar.Tab
import com.example.argos_home.ui.home.HomeScreen
import com.example.argos_home.ui.notifications.NotificationsScreen
import com.example.argos_home.ui.pets.PetDetailScreen
import com.example.argos_home.ui.pets.PetForm
import com.example.argos_home.ui.pets.PetsScreen
import java.time.LocalDate

/** Sub-navigation inside the Pets tab; kept in state so no navigation dependency is needed. */
sealed interface PetsRoute {
    data object List : PetsRoute
    data class Detail(val petId: String) : PetsRoute
    data class Form(val petId: String?) : PetsRoute
}

/** Root host: switches the bottom-nav tab, the pets sub-screens and the notifications overlay by state. */
@Composable
fun AppRoot() {
    var tab by remember { mutableStateOf(Tab.HOME) }
    var petsRoute by remember { mutableStateOf<PetsRoute>(PetsRoute.List) }
    var showNotifications by remember { mutableStateOf(false) }
    // Switching home only relabels: multi-home is a label in this release, so the data never changes with it.
    var activeHousehold by remember { mutableStateOf(SeedData.HOUSEHOLD_NAME) }
    // Date the calendar opens on when reached from a Home activity card; a plain tab switch clears it to today.
    var calendarDate by remember { mutableStateOf<LocalDate?>(null) }

    // Shared bottom-nav handler: reaching the calendar by nav resets it to today, not a date opened from Home.
    val selectTab: (Tab) -> Unit = { target ->
        if (target == Tab.CALENDAR) calendarDate = null
        tab = target
    }

    // System back closes the overlay first, then walks the pets sub-screens back to the list.
    BackHandler(enabled = showNotifications) { showNotifications = false }
    BackHandler(enabled = !showNotifications && tab == Tab.PETS && petsRoute !is PetsRoute.List) {
        petsRoute = backFrom(petsRoute)
    }

    when {
        showNotifications -> NotificationsScreen(onBack = { showNotifications = false })

        tab == Tab.HOME -> HomeScreen(
            onOpenNotifications = { showNotifications = true },
            onSelectTab = selectTab,
            // A double tap on a pet card lands on that pet's profile inside the Pets tab.
            onOpenPet = {
                tab = Tab.PETS
                petsRoute = PetsRoute.Detail(it)
            },
            // A double tap on an activity card opens the calendar's Day view on that activity's date.
            onOpenActivity = {
                calendarDate = it
                tab = Tab.CALENDAR
            },
            activeHousehold = activeHousehold,
            onSwitchHousehold = { activeHousehold = it }
        )

        tab == Tab.CALENDAR -> CalendarScreen(
            onOpenNotifications = { showNotifications = true },
            onSelectTab = selectTab,
            initialDate = calendarDate
        )

        else -> when (val route = petsRoute) {
            PetsRoute.List -> PetsScreen(
                onOpenPet = { petsRoute = PetsRoute.Detail(it) },
                onAddPet = { petsRoute = PetsRoute.Form(null) },
                onOpenNotifications = { showNotifications = true },
                onSelectTab = selectTab
            )

            is PetsRoute.Detail -> PetDetailScreen(
                petId = route.petId,
                onBack = { petsRoute = PetsRoute.List },
                onEdit = { petsRoute = PetsRoute.Form(it) }
            )

            is PetsRoute.Form -> PetForm(
                petId = route.petId,
                onCancel = { petsRoute = backFrom(route) },
                onSaved = { petsRoute = PetsRoute.Detail(it) }
            )
        }
    }
}

/** Where "back" lands from a pets sub-screen: a form returns to its pet or the list, a detail to the list. */
private fun backFrom(route: PetsRoute): PetsRoute = when (route) {
    is PetsRoute.Form -> route.petId?.let { PetsRoute.Detail(it) } ?: PetsRoute.List
    else -> PetsRoute.List
}
