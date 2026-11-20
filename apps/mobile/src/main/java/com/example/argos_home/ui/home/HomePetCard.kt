package com.example.argos_home.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.domain.Pet
import com.example.argos_home.ui.calendar.petIconOf
import com.example.argos_home.ui.calendar.petTypeColor
import com.example.argos_home.ui.calendar.petTypeColorSoft
import com.example.argos_home.ui.pets.petTypeLabel
import com.example.argos_home.ui.theme.White50

/** One carousel card: a type-colored top with a wavy edge and avatar, over the pet's name, type and breed. */
@Composable
fun HomePetCard(pet: Pet, onOpenProfile: (String) -> Unit, modifier: Modifier = Modifier) {
    val strong = petTypeColor(pet.type)
    val soft = petTypeColorSoft(pet.type)
    Column(
        modifier = modifier
            .width(150.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(soft)
            // A double tap opens the pet's profile, so a stray swipe in the carousel never navigates.
            .pointerInput(pet.id) { detectTapGestures(onDoubleTap = { onOpenProfile(pet.id) }) }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(116.dp),
            contentAlignment = Alignment.Center
        ) {
            CardTopWave(strong = strong)
            Box(
                modifier = Modifier
                    .size(62.dp)
                    .clip(CircleShape)
                    .background(soft),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(petIconOf(pet.type)),
                    contentDescription = null,
                    tint = strong,
                    modifier = Modifier.size(36.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = pet.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = stringResource(petTypeLabel(pet.type)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (pet.breed.isNotBlank()) {
                Text(
                    text = pet.breed,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/** The colored header shape: a filled block whose bottom edge waves, with faint decorative circles inside it. */
@Composable
private fun CardTopWave(strong: androidx.compose.ui.graphics.Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height
        val waveY = h * 0.82f
        val amp = h * 0.08f
        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(w, 0f)
            lineTo(w, waveY)
            // Two smooth bumps across the bottom edge make the divider wave inside the card.
            quadraticTo(w * 0.75f, waveY + amp, w * 0.5f, waveY)
            quadraticTo(w * 0.25f, waveY - amp, 0f, waveY)
            close()
        }
        drawPath(path, color = strong)
        // Decorative translucent circles, clipped to the colored shape so they never spill past the wave.
        clipPath(path) {
            drawCircle(color = White50.copy(alpha = 0.12f), radius = w * 0.22f, center = Offset(w * 0.18f, h * 0.22f))
            drawCircle(color = White50.copy(alpha = 0.10f), radius = w * 0.16f, center = Offset(w * 0.86f, h * 0.30f))
            drawCircle(color = White50.copy(alpha = 0.10f), radius = w * 0.12f, center = Offset(w * 0.70f, h * 0.70f))
        }
    }
}
