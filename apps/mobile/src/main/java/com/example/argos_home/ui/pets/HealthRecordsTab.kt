package com.example.argos_home.ui.pets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.MedicalRecordRepository
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.MedicalRecord
import com.example.argos_home.domain.effectiveStatus
import com.example.argos_home.ui.theme.White50
import java.time.LocalDate

/** Health records of one pet: a read-only history; the add action lives in the pinned actions below. */
@Composable
fun HealthRecordsTab(petId: String, modifier: Modifier = Modifier) {
    val records = MedicalRecordRepository.recordsFor(petId)
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if (records.isEmpty()) {
            item {
                Text(
                    text = stringResource(R.string.health_empty),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(records, key = { it.id }) { RecordRow(it) }
        }
    }
}

/** A medical record row: type avatar, title, date and time range, who is responsible, and the date-derived status. */
@Composable
private fun RecordRow(record: MedicalRecord) {
    val typeColor = medicalTypeColor(record.type)
    val status = effectiveStatus(record, LocalDate.now())
    val statusColor = medicalStatusColor(status)
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(14.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(typeColor),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(medicalTypeIcon(record.type)),
                    contentDescription = null,
                    tint = White50,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.size(10.dp))
            Text(
                text = record.title,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = stringResource(medicalStatusLabel(status)),
                style = MaterialTheme.typography.labelSmall,
                color = statusColor,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(statusColor.copy(alpha = 0.14f))
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        val timeText = record.startTime?.let { start ->
            record.endTime?.let { end -> "${ActivityFormats.time(start)} – ${ActivityFormats.time(end)}" }
                ?: ActivityFormats.time(start)
        }
        val dateLine = ActivityFormats.dayMonth(record.date) + if (timeText == null) "" else " · $timeText"
        Text(
            text = dateLine,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        if (record.assignedTo.isNotBlank()) {
            Text(
                text = stringResource(R.string.record_assigned_to, record.assignedTo),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
