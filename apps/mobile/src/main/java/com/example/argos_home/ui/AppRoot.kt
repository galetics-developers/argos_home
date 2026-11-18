package com.example.argos_home.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.argos_home.ui.calendar.CalendarScreen
import com.example.argos_home.ui.calendar.Tab
import com.example.argos_home.ui.notifications.NotificationsScreen
import com.example.argos_home.ui.pets.PetDetailScreen
import com.example.argos_home.ui.pets.PetForm
import com.example.argos_home.ui.pets.PetsScreen

/** Sub-navigation inside the Pets tab; kept in state so no navigation dependency is needed. */
sealed interface PetsRoute {
    data object List : PetsRoute
    data class Detail(val petId: String) : PetsRoute
    data class Form(val petId: String?) : PetsRoute
}

/** Root host: switches the bottom-nav tab, the pets sub-screens and the notifications overlay by state. */
@Composable
fun AppRoot() {
    var tab by remember { mutableStateOf(Tab.PETS) }
    var petsRoute by remember { mutableStateOf<PetsRoute>(PetsRoute.List) }
    var showNotifications by remember { mutableStateOf(false) }

    // System back closes the overlay first, then walks the pets sub-screens back to the list.
    BackHandler(enabled = showNotifications) { showNotifications = false }
    BackHandler(enabled = !showNotifications && tab == Tab.PETS && petsRoute !is PetsRoute.List) {
        petsRoute = backFrom(petsRoute)
    }

    when {
        showNotifications -> NotificationsScreen(onBack = { showNotifications = false })

        tab == Tab.CALENDAR -> CalendarScreen(
            onOpenNotifications = { showNotifications = true },
            onSelectTab = { tab = it }
        )

        else -> when (val route = petsRoute) {
            PetsRoute.List -> PetsScreen(
                onOpenPet = { petsRoute = PetsRoute.Detail(it) },
                onAddPet = { petsRoute = PetsRoute.Form(null) },
                onOpenNotifications = { showNotifications = true },
                onSelectTab = { tab = it }
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
