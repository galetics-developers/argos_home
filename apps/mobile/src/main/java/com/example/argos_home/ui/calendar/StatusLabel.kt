package com.example.argos_home.ui.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ItemType
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Info1
import com.example.argos_home.ui.theme.Success1
import com.example.argos_home.ui.theme.Warning1

/** Wording and color of a state travel together, so painting a state without its text is impossible. */
@Immutable
data class StatusLabel(
    val text: String,
    val color: Color
)

/** Every state shows its real name with its own color; the type is kept for call-site symmetry with the card. */
@Composable
fun statusLabelOf(state: ActivityState, @Suppress("UNUSED_PARAMETER") type: ItemType): StatusLabel = when (state) {
    ActivityState.PENDING -> StatusLabel(stringResource(R.string.status_pending), Warning1)
    ActivityState.CONFIRMED -> StatusLabel(stringResource(R.string.status_confirmed), Info1)
    ActivityState.DONE -> StatusLabel(stringResource(R.string.status_done), Success1)
    ActivityState.CANCELED -> StatusLabel(stringResource(R.string.status_canceled), Error1)
}

/** Renders a state as a tinted pill that always carries its wording. */
@Composable
fun StatusPill(label: StatusLabel, modifier: Modifier = Modifier) {
    Text(
        text = label.text,
        style = MaterialTheme.typography.labelSmall,
        color = label.color,
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(label.color.copy(alpha = PILL_BACKGROUND_ALPHA))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

/** Soft wash of the state color, light enough to keep the wording readable on top of it. */
private const val PILL_BACKGROUND_ALPHA = 0.14f
