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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.argos_home.R
import com.example.argos_home.domain.ScheduledActivity
import com.example.argos_home.domain.SeedData
import com.example.argos_home.presentation.activityTimeText
import com.example.argos_home.presentation.activityTypeColor
import com.example.argos_home.presentation.categoryColor
import com.example.argos_home.presentation.kindLabel
import com.example.argos_home.presentation.petIconOf
import com.example.argos_home.presentation.petTypeColor
import com.example.argos_home.presentation.petTypeColorSoft
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
    val type = SeedData.petTypeOf(activity.petName)
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

        // Central block uses the full frame width with side insets, so the name, labels and time never clip.
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
                    .background(petTypeColor(type)),
                contentAlignment = Alignment.Center
            ) {
                // Strong circle, light same-color glyph: a monochrome avatar where the icon stays readable.
                Icon(
                    painter = painterResource(petIconOf(type)),
                    contentDescription = stringResource(R.string.pet_avatar_description),
                    tint = petTypeColorSoft(type),
                    modifier = Modifier.size((AVATAR_ICON_SIZE * scale).dp)
                )
            }

            Text(
                text = activity.petName,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = activity.title.take(TITLE_CHAR_LIMIT),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )

            // Two filled labels on one level: the kind and the category, each in its own palette color.
            Row(horizontalArrangement = Arrangement.spacedBy((LABEL_GAP * scale).dp)) {
                FilledLabel(text = kindLabel(activity.type), background = activityTypeColor(activity.type), scale = scale)
                FilledLabel(text = activity.category, background = categoryColor(activity.category), scale = scale)
            }

            // A single task time stays large; an event range uses a smaller style so both ends and periods fit.
            Text(
                text = activityTimeText(activity),
                style = if (activity.endTime == null) {
                    MaterialTheme.typography.numeralSmall
                } else {
                    MaterialTheme.typography.titleSmall
                },
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/** One filled pill with light text, scaled with the card so the pair fits the round screen. */
@Composable
private fun FilledLabel(text: String, background: androidx.compose.ui.graphics.Color, scale: Float) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = White50,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .background(background)
            .padding(horizontal = (LABEL_PADDING_H * scale).dp, vertical = (LABEL_PADDING_V * scale).dp)
    )
}

// Design units of the "next activity" screen, kept as written in the approved layout.
private const val HEADER_TOP = 12.96f
private const val HEADER_HEIGHT = 20f
private const val BLOCK_TOP = 46.66f
private const val SIDE_INSET = 12f
private const val BLOCK_SPACING = 4f
private const val AVATAR_SIZE = 50f
private const val AVATAR_ICON_SIZE = 30f
private const val LABEL_GAP = 5f
private const val LABEL_PADDING_H = 7f
private const val LABEL_PADDING_V = 2f

/** Upper bound on the title so a very long one cannot push the card off the round screen. */
private const val TITLE_CHAR_LIMIT = 40
