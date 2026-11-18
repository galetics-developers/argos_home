package com.example.argos_home.ui.calendar

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.argos_home.R
import com.example.argos_home.domain.PetType
import com.example.argos_home.ui.theme.Info3
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary500
import com.example.argos_home.ui.theme.Quaternary200
import com.example.argos_home.ui.theme.Quaternary900
import com.example.argos_home.ui.theme.Secondary100
import com.example.argos_home.ui.theme.Secondary500
import com.example.argos_home.ui.theme.Success3
import com.example.argos_home.ui.theme.Tertiary100
import com.example.argos_home.ui.theme.Tertiary600

/** Maps a pet species to its avatar drawable; OTHER falls back to the neutral paw. */
@DrawableRes
fun petIconOf(type: PetType): Int = when (type) {
    PetType.DOG -> R.drawable.ic_pet_dog
    PetType.CAT -> R.drawable.ic_pet_cat
    PetType.RABBIT -> R.drawable.ic_pet_rabbit
    PetType.BIRD -> R.drawable.ic_pet_bird
    PetType.FISH -> R.drawable.ic_pet_fish
    PetType.SNAKE -> R.drawable.ic_pet_snake
    PetType.RODENT -> R.drawable.ic_pet_rodent
    PetType.ARACHNID -> R.drawable.ic_pet_arachnid
    PetType.REPTILE -> R.drawable.ic_pet_reptile
    PetType.POULTRY -> R.drawable.ic_pet_poultry
    PetType.AMPHIBIAN -> R.drawable.ic_pet_amphibian
    PetType.OTHER -> R.drawable.ic_pet
}

/** Brand color of each species group, so a type reads by color as well as by icon and label. */
fun petTypeColor(type: PetType): Color = when (type) {
    PetType.DOG, PetType.SNAKE, PetType.REPTILE -> Primary500
    PetType.CAT, PetType.RODENT, PetType.POULTRY -> Secondary500
    PetType.BIRD, PetType.FISH, PetType.AMPHIBIAN -> Tertiary600
    PetType.RABBIT, PetType.ARACHNID, PetType.OTHER -> Quaternary900
}

/** Light tone of each species group, used for avatar and card backgrounds. */
fun petTypeColorSoft(type: PetType): Color = when (type) {
    PetType.DOG, PetType.SNAKE, PetType.REPTILE -> Primary100
    PetType.CAT, PetType.RODENT, PetType.POULTRY -> Secondary100
    PetType.BIRD, PetType.FISH, PetType.AMPHIBIAN -> Tertiary100
    PetType.RABBIT, PetType.ARACHNID, PetType.OTHER -> Quaternary200
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
