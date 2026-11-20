package com.example.argos_home.ui.calendar

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.argos_home.R
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import java.time.LocalDate

/** The three calendar views; the enum order is also the order of the selector tabs. */
enum class CalendarView(@StringRes val labelRes: Int) {
    MONTH(R.string.view_month),
    WEEK(R.string.view_week),
    DAY(R.string.view_day)
}

// Reads the repository snapshot list, so a transition applied here or arriving from the watch repaints by itself.
/** Phone calendar host: owns the view, selected day, pet filter and the create-activity modal. */
@Composable
fun CalendarScreen(
    onOpenNotifications: () -> Unit,
    onSelectTab: (Tab) -> Unit,
    modifier: Modifier = Modifier
) {
    var view by rememberSaveable { mutableStateOf(CalendarView.DAY) }
    var selectedDate by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    var petFilter by rememberSaveable { mutableStateOf<String?>(null) }
    var showNewActivity by remember { mutableStateOf(false) }

    val date = LocalDate.ofEpochDay(selectedDate)
    val petNames = remember { SeedData.pets.map { it.name } }

    // The card offers only legal transitions, so the chosen target travels straight to the store, which validates again.
    val onAction: (ScheduledActivity, ActivityState) -> Unit = { activity, target ->
        ActivityRepository.applyTransition(
            id = activity.id,
            target = target,
            resolvedAtMillis = System.currentTimeMillis()
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        bottomBar = { BottomNavBar(selected = Tab.CALENDAR, onSelect = onSelectTab) },
        // The branded top bar owns the status-bar inset itself, so the scaffold adds none of its own.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            CalendarTopBar(onBell = onOpenNotifications)
            ViewSelector(selected = view, onSelect = { view = it })
            PetFilterBar(pets = petNames, selected = petFilter, onSelect = { petFilter = it })

            when (view) {
                CalendarView.MONTH -> CalendarMonthView(
                    month = date,
                    petFilter = petFilter,
                    onDaySelected = { day ->
                        selectedDate = day.toEpochDay()
                        view = CalendarView.DAY
                    },
                    onPreviousMonth = { selectedDate = date.minusMonths(1).toEpochDay() },
                    onNextMonth = { selectedDate = date.plusMonths(1).toEpochDay() },
                    onNewEvent = { showNewActivity = true }
                )

                CalendarView.WEEK -> CalendarWeekView(
                    date = date,
                    petFilter = petFilter,
                    // Picking a day in the week stays in Week and just reloads that day's cards below.
                    onDaySelected = { day -> selectedDate = day.toEpochDay() },
                    onAction = onAction,
                    onNewEvent = { showNewActivity = true }
                )

                CalendarView.DAY -> CalendarDayView(
                    date = date,
                    petFilter = petFilter,
                    onPrevious = { selectedDate = date.minusDays(1).toEpochDay() },
                    onNext = { selectedDate = date.plusDays(1).toEpochDay() },
                    onAction = onAction,
                    onNewEvent = { showNewActivity = true }
                )
            }
        }
    }

    if (showNewActivity) {
        NewActivitySheet(
            defaultDate = date,
            onDismiss = { showNewActivity = false },
            onSave = { activity ->
                ActivityRepository.addActivity(activity)
                showNewActivity = false
            }
        )
    }
}
