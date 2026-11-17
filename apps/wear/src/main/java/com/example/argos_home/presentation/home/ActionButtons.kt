package com.example.argos_home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.argos_home.R
import com.example.argos_home.domain.ActivityState
import com.example.argos_home.domain.ItemType
import com.example.argos_home.domain.TransitionRules
import com.example.argos_home.presentation.theme.Error1
import com.example.argos_home.presentation.theme.Info1
import com.example.argos_home.presentation.theme.Success1

// Only the transitions legal for the current state and kind are shown; a resolved activity shows none, not three dimmed.
/** The actions the watch offers for an activity, driven by the shared rules so the set matches the phone's. */
@Composable
fun ActionButtons(
    type: ItemType,
    state: ActivityState,
    sendBlocked: Boolean,
    onAction: (ActivityState) -> Unit,
    scale: Float,
    modifier: Modifier = Modifier
) {
    val targets = TransitionRules.allowedTargets(state, type)
    if (targets.isEmpty()) {
        return
    }
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy((BUTTON_GAP * scale).dp)
    ) {
        targets.forEach { target ->
            val spec = specOf(target)
            ActionButton(
                target = target,
                iconRes = spec.iconRes,
                labelRes = spec.labelRes,
                descriptionRes = spec.descriptionRes,
                accent = spec.accent,
                // Only disabled while a request is in flight, so a second tap cannot ask the phone twice.
                enabled = !sendBlocked,
                onAction = onAction,
                scale = scale
            )
        }
    }
}

private data class ButtonSpec(
    val iconRes: Int,
    val labelRes: Int,
    val descriptionRes: Int,
    val accent: Color
)

private fun specOf(target: ActivityState): ButtonSpec = when (target) {
    ActivityState.CANCELED -> ButtonSpec(R.drawable.ic_circle_x, R.string.button_canceled, R.string.button_canceled_description, Error1)
    ActivityState.DONE -> ButtonSpec(R.drawable.ic_check, R.string.button_done, R.string.button_done_description, Success1)
    ActivityState.CONFIRMED -> ButtonSpec(R.drawable.ic_circle_plus, R.string.button_confirmed, R.string.button_confirmed_description, Info1)
    ActivityState.PENDING -> ButtonSpec(R.drawable.ic_check, R.string.button_done, R.string.button_done_description, Success1)
}

@Composable
private fun ActionButton(
    target: ActivityState,
    iconRes: Int,
    labelRes: Int,
    descriptionRes: Int,
    accent: Color,
    enabled: Boolean,
    onAction: (ActivityState) -> Unit,
    scale: Float
) {
    Box(
        modifier = Modifier
            .size((BUTTON_SIZE * scale).dp)
            .clip(RoundedCornerShape(percent = BUTTON_CORNER_PERCENT))
            .background(accent.copy(alpha = CONTAINER_ALPHA))
            .alpha(if (enabled) 1f else DISABLED_ALPHA)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = { onAction(target) }
            )
    ) {
        Column(
            modifier = Modifier.size((BUTTON_SIZE * scale).dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height((ICON_TOP * scale).dp))
            Icon(
                painter = painterResource(iconRes),
                contentDescription = stringResource(descriptionRes),
                tint = accent,
                modifier = Modifier.size((ICON_SIZE * scale).dp)
            )
            Spacer(modifier = Modifier.height(((LABEL_TOP - ICON_TOP - ICON_SIZE) * scale).dp))
            Text(
                text = stringResource(labelRes),
                // Scaled with the button, not taken from the theme: at theme size the longest label would not fit.
                style = MaterialTheme.typography.labelSmall.copy(fontSize = (LABEL_FONT_SIZE * scale).sp),
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                softWrap = false,
                maxLines = 1
            )
        }
    }
}

// Design units of the action row, shrunk so three buttons with their labels fit inside the round screen.
private const val BUTTON_SIZE = 44f
private const val BUTTON_GAP = 6f
private const val ICON_SIZE = 16f
private const val ICON_TOP = 7f
private const val LABEL_TOP = 26f
private const val BUTTON_CORNER_PERCENT = 30
private const val LABEL_FONT_SIZE = 6.5f

/** Soft wash of the action color behind the icon, light enough to keep the wording readable. */
private const val CONTAINER_ALPHA = 0.16f

/** A dimmed button is visibly out of reach while a request is in flight, and it also stops answering taps. */
private const val DISABLED_ALPHA = 0.38f
