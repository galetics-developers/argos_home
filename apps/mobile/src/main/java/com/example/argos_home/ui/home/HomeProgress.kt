package com.example.argos_home.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.HomeSummary
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary500
import com.example.argos_home.ui.theme.Primary600
import java.time.LocalDate

/** Day progress: a ring whose arc is the share of today's activities that are done, with the day's counts beside it. */
@Composable
fun HomeProgress(
    summary: HomeSummary,
    modifier: Modifier = Modifier,
    today: LocalDate = LocalDate.now()
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ProgressRing(fraction = summary.fraction, percent = summary.percent)
        Spacer(modifier = Modifier.size(18.dp))
        Column {
            Text(
                text = stringResource(R.string.home_today_care, ActivityFormats.dayMonth(today)),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.size(2.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = summary.completed.toString(),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.size(6.dp))
                Text(
                    text = stringResource(R.string.home_tasks_done, summary.total),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }
    }
}

/** A circular gauge: a full track plus an arc swept to [fraction], with the percent in the middle. */
@Composable
private fun ProgressRing(fraction: Float, percent: Int, modifier: Modifier = Modifier) {
    Box(modifier = modifier.size(72.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(72.dp)) {
            val stroke = 8.dp.toPx()
            val inset = stroke / 2f
            val arcSize = Size(size.width - stroke, size.height - stroke)
            drawArc(
                color = Primary100,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
            // Sweep clockwise from the top by the completed share; round caps match the prototype.
            drawArc(
                color = Primary500,
                startAngle = -90f,
                sweepAngle = fraction.coerceIn(0f, 1f) * 360f,
                useCenter = false,
                topLeft = Offset(inset, inset),
                size = arcSize,
                style = Stroke(width = stroke, cap = StrokeCap.Round)
            )
        }
        Text(
            text = stringResource(R.string.home_percent, percent),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = Primary600
        )
    }
}
