package com.example.argos_home.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argos_home.R
import com.example.argos_home.data.ActivityRepository
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50
import java.time.LocalDate

/** Month grid of the selected month; a dot with a count marks days that have activities, tap a day to open it. */
@Composable
fun CalendarMonthView(
    month: LocalDate,
    petFilter: String?,
    onDaySelected: (LocalDate) -> Unit,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNewEvent: () -> Unit,
    modifier: Modifier = Modifier
) {
    val firstOfMonth = month.withDayOfMonth(1)
    val daysInMonth = month.lengthOfMonth()
    val leadingBlanks = firstOfMonth.dayOfWeek.value % 7 // Sunday-first grid: Sunday maps to column 0.
    val counts = ActivityRepository.countsByDay(month.year, month.monthValue, petFilter)
    val today = LocalDate.now()

    // One flat list of cells: null before the 1st, then each day of the month, padded to full weeks.
    val cells = buildList {
        repeat(leadingBlanks) { add(null) }
        for (day in 1..daysInMonth) add(month.withDayOfMonth(day))
        while (size % 7 != 0) add(null)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_chevron_left),
                contentDescription = stringResource(R.string.previous_month),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onPreviousMonth)
                    .padding(4.dp)
            )
            Text(
                text = ActivityFormats.monthTitle(month),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
            Icon(
                painter = painterResource(R.drawable.ic_chevron_right),
                contentDescription = stringResource(R.string.next_month),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .clickable(onClick = onNextMonth)
                    .padding(4.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            WEEKDAY_INITIALS.forEach { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        cells.chunked(7).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { day ->
                    Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                        if (day != null) {
                            DayCell(
                                date = day,
                                count = counts[day] ?: 0,
                                selected = day == month,
                                today = day == today,
                                onClick = onDaySelected
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        NewEventButton(onClick = onNewEvent)
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/** A single day: the number, highlighted when selected, with a dot and count when it has activities. */
@Composable
private fun DayCell(
    date: LocalDate,
    count: Int,
    selected: Boolean,
    today: Boolean,
    onClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    val numberColor = when {
        selected -> White50
        today -> Primary600
        else -> MaterialTheme.colorScheme.onSurface
    }
    val markColor = if (selected) White50 else Primary600
    Column(
        modifier = modifier
            .padding(2.dp)
            .size(44.dp)
            .clip(CircleShape)
            .background(if (selected) Primary600 else androidx.compose.ui.graphics.Color.Transparent)
            .clickable { onClick(date) },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (today || selected) FontWeight.Bold else FontWeight.Normal,
            color = numberColor
        )
        if (count > 0) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(markColor)
                )
                Spacer(modifier = Modifier.size(2.dp))
                Text(
                    text = count.toString(),
                    fontSize = 9.sp,
                    color = markColor
                )
            }
        }
    }
}

private val WEEKDAY_INITIALS = listOf("S", "M", "T", "W", "T", "F", "S")
