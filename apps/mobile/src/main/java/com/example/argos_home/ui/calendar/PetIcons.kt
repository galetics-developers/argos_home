package com.example.argos_home.ui.calendar

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.argos_home.R
import com.example.argos_home.domain.PetType
import com.example.argos_home.ui.theme.Info3
import com.example.argos_home.ui.theme.Primary500
import com.example.argos_home.ui.theme.Quaternary900
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.Success3
import com.example.argos_home.ui.theme.Tertiary600

/** Maps a pet species to its avatar drawable; species without a dedicated glyph fall back to the paw. */
@DrawableRes
fun petIconOf(type: PetType): Int = when (type) {
    PetType.DOG -> R.drawable.ic_pet_dog
    PetType.CAT -> R.drawable.ic_pet_cat
    PetType.RABBIT -> R.drawable.ic_pet_rabbit
    PetType.BIRD -> R.drawable.ic_pet_bird
    PetType.FISH -> R.drawable.ic_pet_fish
    else -> R.drawable.ic_pet
}

/** A distinct palette color per pet so avatars read apart at a glance; stable by name, cycled for unseeded pets. */
fun petAvatarColor(petName: String): Color {
    val index = ((petName.hashCode() % PALETTE.size) + PALETTE.size) % PALETTE.size
    return NAMED[petName] ?: PALETTE[index]
}

private val NAMED: Map<String, Color> = mapOf(
    "Boyka Sanchez" to Secondary500,
    "Luna" to Primary500,
    "Kiwi" to Quaternary900,
    "Milo" to Tertiary600,
    "Nina" to Info3,
    "Coco" to Success3
)

private val PALETTE: List<Color> =
    listOf(Secondary500, Primary500, Tertiary600, Info3, Success3, Quaternary900)
