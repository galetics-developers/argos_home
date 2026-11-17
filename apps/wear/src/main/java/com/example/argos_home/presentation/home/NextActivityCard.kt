package com.example.argos_home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityFormats
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.presentation.petAvatarColor
import com.example.argos_home.presentation.petUsesWhiteGlyph
import com.example.argos_home.presentation.petIconOf
import com.example.argos_home.presentation.theme.Black900
import com.example.argos_home.presentation.theme.White50

// Design units are multiplied by [scale], which the caller derives from the real width of the screen.
/** The next activity as the watch shows it; takes plain data and knows nothing of the store or the Data Layer. */
@Composable
fun NextActivityCard(
    dateLabel: String,
    activity: ScheduledActivity,
    scale: Float,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // The date is a label of the seeded day, never a reading of the watch clock.
        Spacer(modifier = Modifier.height((HEADER_TOP * scale).dp))
        Text(
            text = dateLabel,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(((BLOCK_TOP - HEADER_TOP - HEADER_HEIGHT) * scale).dp))

        // Central block uses the full frame width with side insets, so the pet name, kind and time never clip.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = (SIDE_INSET * scale).dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy((BLOCK_SPACING * scale).dp)
        ) {
            Box(
                modifier = Modifier
                    .size((AVATAR_SIZE * scale).dp)
                    .clip(CircleShape)
                    .background(petAvatarColor(activity.petName)),
                contentAlignment = Alignment.Center
            ) {
                // White glyph on the darker circles; a dark glyph on the light sandy/orange/green ones for contrast.
                Icon(
                    painter = painterResource(petIconOf(SeedData.petTypeOf(activity.petName))),
                    contentDescription = stringResource(R.string.pet_avatar_description),
                    tint = if (petUsesWhiteGlyph(activity.petName)) White50 else Black900,
                    modifier = Modifier.size((AVATAR_ICON_SIZE * scale).dp)
                )
            }

            Text(
                text = activity.petName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            TitleRow(activity = activity, scale = scale)

            Text(
                text = activity.title,
                style = MaterialTheme.typography.bodyExtraSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = ActivityFormats.time(activity.startTime),
                style = MaterialTheme.typography.numeralSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** Kind of activity with its marker, taken from the first segment of the subtitle; the watch has room for no more. */
@Composable
private fun TitleRow(activity: ScheduledActivity, scale: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clock),
            contentDescription = stringResource(R.string.schedule_description),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size((TITLE_ICON_SIZE * scale).dp)
        )
        Text(
            text = activity.subtitle.substringBefore(KIND_SEPARATOR).trim(),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            modifier = Modifier.padding(start = (TITLE_ICON_GAP * scale).dp)
        )
    }
}

// Design units of the "next activity" screen, kept as written in the approved layout.
private const val HEADER_TOP = 12.96f
private const val HEADER_HEIGHT = 20f
private const val BLOCK_TOP = 46.66f
private const val SIDE_INSET = 12f
private const val BLOCK_SPACING = 4f
private const val AVATAR_SIZE = 50f
private const val AVATAR_ICON_SIZE = 30f
private const val TITLE_ICON_SIZE = 11.52f
private const val TITLE_ICON_GAP = 4f

/** The seeded subtitle joins the kind and the pet with this separator. */
private const val KIND_SEPARATOR = '·'
