package com.example.argos_home.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.presentation.actions.ActionsScreen
import com.example.argos_home.presentation.home.HomeScreen
import com.example.argos_home.presentation.notifications.NotificationsScreen

// Three swipeable modules: Home, Actions and Notifications, matching the three dots of the indicator.
/** Root of the watch app: a horizontal pager over the Home, Actions and Notifications modules. */
@Composable
fun WearApp(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    HorizontalPager(state = pagerState, modifier = modifier.fillMaxSize()) { page ->
        when (page) {
            0 -> HomeScreen(onRequestTransition = onRequestTransition)
            1 -> ActionsScreen(onRequestTransition = onRequestTransition)
            else -> NotificationsScreen()
        }
    }
}
