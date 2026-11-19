package com.example.argos_home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.presentation.actions.ActionsScreen
import com.example.argos_home.presentation.home.HomeScreen
import com.example.argos_home.presentation.home.MODULE_COUNT
import com.example.argos_home.presentation.home.ModuleIndicator
import com.example.argos_home.presentation.notifications.NotificationsScreen
import kotlinx.coroutines.launch

// Three swipeable modules: Home, Actions and Notifications. The page indicator is fixed over all three.
/** Root of the watch app: a horizontal pager with one shared, interactive page indicator at the foot. */
@Composable
fun WearApp(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { MODULE_COUNT })
    val scope = rememberCoroutineScope()
    Box(modifier = modifier.fillMaxSize()) {
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> HomeScreen(onRequestTransition = onRequestTransition)
                1 -> ActionsScreen(onRequestTransition = onRequestTransition)
                else -> NotificationsScreen()
            }
        }
        // Fixed and interactive: it tracks the current page and jumps to a module when its dot is tapped.
        ModuleIndicator(
            activeIndex = pagerState.currentPage,
            scale = 1f,
            onDotClick = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = INDICATOR_BOTTOM.dp)
        )
    }
}

private const val INDICATOR_BOTTOM = 8f
