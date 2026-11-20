package com.example.argos_home.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.HomeSummary
import com.example.argos_home.ui.theme.Gray200
import com.example.argos_home.ui.theme.Success1
import com.example.argos_home.ui.theme.Tertiary700

/** The day in four cells: total, pending and completed counts, and the title of the next pending event. */
@Composable
fun HomeStats(summary: HomeSummary, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CountCell(
            value = summary.total.toString(),
            label = stringResource(R.string.home_stat_total),
            valueColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        CellDivider()
        CountCell(
            value = summary.pending.toString(),
            label = stringResource(R.string.home_stat_pending),
            valueColor = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f)
        )
        CellDivider()
        CountCell(
            value = summary.completed.toString(),
            label = stringResource(R.string.home_stat_completed),
            valueColor = Success1,
            modifier = Modifier.weight(1f)
        )
        CellDivider()
        NextEventCell(
            title = summary.nextEventTitle,
            modifier = Modifier.weight(1f)
        )
    }
}

/** A big colored number over its label; the label keeps the meaning when colors cannot be told apart. */
@Composable
private fun CountCell(value: String, label: String, valueColor: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** The next pending event's title, or a dash when nothing is pending ahead. */
@Composable
private fun NextEventCell(title: String?, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = title ?: stringResource(R.string.home_next_event_empty),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = Tertiary700,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(R.string.home_stat_next_event),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

/** Hairline separator between the cells, as tall as the tallest cell. */
@Composable
private fun CellDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .width(1.dp)
            .fillMaxHeight()
            .padding(vertical = 4.dp)
            .background(Gray200)
    )
}
