package com.example.argos_home.presentation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.presentation.actions.ActionsScreen
import com.example.argos_home.presentation.home.HomeScreen

// Two swipeable modules today, Home and Actions; the third dot of the indicator belongs to a later release.
/** Root of the watch app: a horizontal pager over the Home and Actions modules. */
@Composable
fun WearApp(
    onRequestTransition: (String, ActivityState) -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    HorizontalPager(state = pagerState, modifier = modifier.fillMaxSize()) { page ->
        when (page) {
            0 -> HomeScreen(onRequestTransition = onRequestTransition)
            else -> ActionsScreen(onRequestTransition = onRequestTransition)
        }
    }
}
