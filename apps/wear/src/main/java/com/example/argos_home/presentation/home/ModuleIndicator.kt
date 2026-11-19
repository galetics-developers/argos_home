package com.example.argos_home.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.MaterialTheme
import com.example.argos_home.R

// All three dots are always drawn, fixed at the foot of the pager; the active one is widened.
/** The three module dots; when [onDotClick] is set, tapping a dot jumps to that module. */
@Composable
fun ModuleIndicator(
    activeIndex: Int,
    scale: Float,
    modifier: Modifier = Modifier,
    moduleCount: Int = MODULE_COUNT,
    onDotClick: ((Int) -> Unit)? = null
) {
    val description = stringResource(
        R.string.module_indicator_description,
        activeIndex + 1,
        moduleCount
    )
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy((DOT_GAP * scale).dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (index in 0 until moduleCount) {
            val active = index == activeIndex
            Box(
                modifier = Modifier
                    .width(((if (active) ACTIVE_DOT_WIDTH else DOT_SIZE) * scale).dp)
                    .height((DOT_SIZE * scale).dp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        if (active) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    .then(
                        if (onDotClick != null) {
                            Modifier.clickable(role = Role.Button) { onDotClick(index) }
                        } else {
                            Modifier
                        }
                    )
            )
        }
    }
}

// Design units of the indicator, kept as written in the approved layout.
private const val ACTIVE_DOT_WIDTH = 12.96f
private const val DOT_SIZE = 5.04f
private const val DOT_GAP = 4f

/** The watch home is designed as three swipeable modules; only the first one exists today. */
const val MODULE_COUNT = 3
