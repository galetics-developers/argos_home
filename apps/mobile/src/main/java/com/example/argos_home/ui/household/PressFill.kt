package com.example.argos_home.ui.household

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.example.argos_home.ui.theme.White50

// Pressing or hovering fills the button with its accent and turns the content white, as the prototype shows.
/** Outlined button that fills with [fillColor] on press/hover; content goes white while active. */
@Composable
fun PressFillButton(
    onClick: () -> Unit,
    fillColor: Color,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(14.dp),
    restContentColor: Color = fillColor,
    restContainerColor: Color = Color.Transparent,
    borderColor: Color? = fillColor,
    contentPadding: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Center,
    content: @Composable RowScope.(contentColor: Color) -> Unit
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    val hovered by interaction.collectIsHoveredAsState()
    val active = pressed || hovered
    val container = if (active) fillColor else restContainerColor
    val contentColor = if (active) White50 else restContentColor
    Row(
        modifier = modifier
            .clip(shape)
            .then(if (borderColor != null && !active) Modifier.border(1.5.dp, borderColor, shape) else Modifier)
            .background(container)
            .hoverable(interaction)
            .clickable(interactionSource = interaction, indication = ripple()) { onClick() }
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = horizontalArrangement
    ) {
        content(contentColor)
    }
}
