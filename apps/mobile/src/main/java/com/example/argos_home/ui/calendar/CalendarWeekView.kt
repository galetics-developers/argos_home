package com.example.argos_home.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argos_home.R
import androidx.compose.foundation.clickable
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50
import java.time.LocalDate

/** Week view: a card with the selected day and a mini month, then that day's schedule. */
@Composable
fun CalendarWeekView(
    date: LocalDate,
    petFilter: String?,
    onDaySelected: (LocalDate) -> Unit,
    onAction: (ScheduledActivity, ActivityState) -> Unit,
    onNewEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activities = ActivityRepository.activitiesOn(date, petFilter)
    // Week header (with the mini month), section head and "New event" stay put; only the cards scroll.
    Column(modifier = modifier.fillMaxSize()) {
        WeekHeaderCard(
            date = date,
            onDaySelected = onDaySelected,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp, top = 16.dp)
        )
        ScheduleSectionHeader(
            count = activities.size,
            isToday = date == LocalDate.now(),
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            scheduleCards(activities, onAction)
        }
        NewEventButton(
            onClick = onNewEvent,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
        )
    }
}

/** The big day summary on the left and a compact month on the right, as in the week prototype. */
@Composable
private fun WeekHeaderCard(date: LocalDate, onDaySelected: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
    val parts = ActivityFormats.dayMonth(date).split(" ")
    val monthAbbrev = parts.getOrElse(0) { "" }.uppercase()
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(
                modifier = Modifier.width(96.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = monthAbbrev,
                    style = MaterialTheme.typography.labelMedium,
                    color = Primary600
                )
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 40.sp,
                    fontWeight = FontWeight.Bold,
                    color = Primary600
                )
                Text(
                    text = ActivityFormats.weekdayFull(date),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (date == LocalDate.now()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.chip_today),
                        style = MaterialTheme.typography.labelSmall,
                        color = White50,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(Primary600)
                            .padding(horizontal = 10.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            MiniMonth(date = date, onDaySelected = onDaySelected, modifier = Modifier.weight(1f))
        }
    }
}

/** A small month that marks the selected day; tapping any day opens that day in the Day view. */
@Composable
private fun MiniMonth(date: LocalDate, onDaySelected: (LocalDate) -> Unit, modifier: Modifier = Modifier) {
    val firstOfMonth = date.withDayOfMonth(1)
    val leadingBlanks = firstOfMonth.dayOfWeek.value % 7
    val cells = buildList {
        repeat(leadingBlanks) { add(null) }
        for (day in 1..date.lengthOfMonth()) add(day)
        while (size % 7 != 0) add(null)
    }
    Column(modifier = modifier) {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("S", "M", "T", "W", "T", "F", "S").forEach {
                Text(
                    text = it,
                    fontSize = 9.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (day != null) {
                            val selected = day == date.dayOfMonth
                            Box(
                                modifier = Modifier
                                    .padding(1.dp)
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(if (selected) Primary600 else Color.Transparent)
                                    .clickable { onDaySelected(date.withDayOfMonth(day)) },
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = day.toString(),
                                    fontSize = 9.sp,
                                    color = if (selected) White50 else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
