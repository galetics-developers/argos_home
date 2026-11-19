package com.example.argos_home.presentation.home

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Icon
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Text
import com.example.argos_home.R
import com.example.argos_home.presentation.theme.Error2
import com.example.argos_home.presentation.theme.Success2
import com.example.argos_home.presentation.theme.White50

// Reusable full-screen status states of the watch, shared by Home, Actions and Notifications.
// Each emits centered children inside the caller's scrollable, centered Column.

/** The resolved/empty state: a green filled circle with a check, a title and a message. */
@Composable
fun GreenCheckState(scale: Float, title: String, message: String) {
    Spacer(modifier = Modifier.height((STATE_TOP * scale).dp))
    StateCircle(iconRes = R.drawable.ic_check, circleColor = Success2, scale = scale, contentDescription = null)
    StateTexts(title = title, titleColor = Success2, message = message, scale = scale)
}

/** The in-flight state: a primary circle with a spinning sync glyph, so a wait reads as progress. */
@Composable
fun SyncingState(scale: Float) {
    Spacer(modifier = Modifier.height((STATE_TOP * scale).dp))
    val spin = rememberInfiniteTransition(label = "sync")
    val angle by spin.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(durationMillis = 1200, easing = LinearEasing)),
        label = "sync-angle"
    )
    StateCircle(
        iconRes = R.drawable.ic_sync,
        circleColor = MaterialTheme.colorScheme.primary,
        scale = scale,
        contentDescription = stringResource(R.string.syncing_description),
        iconModifier = Modifier.graphicsLayer { rotationZ = angle }
    )
    StateTexts(
        title = stringResource(R.string.syncing_title),
        titleColor = MaterialTheme.colorScheme.onSurface,
        message = stringResource(R.string.syncing_message),
        scale = scale
    )
}

/** The failure state: a red circle with a cross, a generic message and a button back to the module. */
@Composable
fun ConnectionErrorState(scale: Float, message: String, onBack: () -> Unit) {
    Spacer(modifier = Modifier.height((STATE_TOP * scale).dp))
    StateCircle(iconRes = R.drawable.ic_circle_x, circleColor = Error2, scale = scale, contentDescription = null)
    StateTexts(
        title = stringResource(R.string.error_connection_title),
        titleColor = Error2,
        message = message,
        scale = scale
    )
    Spacer(modifier = Modifier.height((BACK_GAP * scale).dp))
    BackButton(onBack = onBack, scale = scale)
}

/** A filled status circle with a centered white glyph; the base of every state screen. */
@Composable
private fun StateCircle(
    iconRes: Int,
    circleColor: Color,
    scale: Float,
    contentDescription: String?,
    iconModifier: Modifier = Modifier
) {
    Box(
        modifier = Modifier
            .size((CIRCLE_SIZE * scale).dp)
            .clip(CircleShape)
            .background(circleColor),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = contentDescription,
            tint = White50,
            modifier = iconModifier.size((ICON_SIZE * scale).dp)
        )
    }
}

/** Title and supporting message shared by the three states; the title carries its own color. */
@Composable
private fun StateTexts(title: String, titleColor: Color, message: String, scale: Float) {
    Spacer(modifier = Modifier.height((TEXT_GAP * scale).dp))
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = titleColor,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
    Text(
        text = message,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = (TEXT_PADDING * scale).dp)
    )
}

/** A back control with an arrow and a label, returning to the start of the current module. */
@Composable
private fun BackButton(onBack: () -> Unit, scale: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy((BACK_ICON_GAP * scale).dp),
        modifier = Modifier
            .clip(RoundedCornerShape(percent = 50))
            .clickable(role = Role.Button, onClick = onBack)
            .padding(horizontal = (BACK_PADDING_H * scale).dp, vertical = (BACK_PADDING_V * scale).dp)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_left),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size((BACK_ICON_SIZE * scale).dp)
        )
        Text(
            text = stringResource(R.string.error_back),
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

// Design units of the status states, kept as written in the approved layout.
private const val STATE_TOP = 40f
private const val CIRCLE_SIZE = 56f
private const val ICON_SIZE = 28f
private const val TEXT_GAP = 10f
private const val TEXT_PADDING = 16f
private const val BACK_GAP = 10f
private const val BACK_ICON_GAP = 4f
private const val BACK_ICON_SIZE = 16f
private const val BACK_PADDING_H = 10f
private const val BACK_PADDING_V = 6f
