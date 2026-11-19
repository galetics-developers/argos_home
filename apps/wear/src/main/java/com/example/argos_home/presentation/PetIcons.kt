package com.example.argos_home.presentation

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color
import com.example.argos_home.R
import com.example.argos_home.domain.PetType
import com.example.argos_home.presentation.theme.Primary100
import com.example.argos_home.presentation.theme.Primary500
import com.example.argos_home.presentation.theme.Quaternary200
import com.example.argos_home.presentation.theme.Quaternary900
import com.example.argos_home.presentation.theme.Secondary100
import com.example.argos_home.presentation.theme.Secondary500
import com.example.argos_home.presentation.theme.Tertiary100
import com.example.argos_home.presentation.theme.Tertiary600

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

/** Strong brand color of each species group, used for the avatar glyph and other accents. */
fun petTypeColor(type: PetType): Color = when (type) {
    PetType.DOG, PetType.SNAKE, PetType.REPTILE -> Primary500
    PetType.CAT, PetType.RODENT, PetType.POULTRY -> Secondary500
    PetType.BIRD, PetType.FISH, PetType.AMPHIBIAN -> Tertiary600
    PetType.RABBIT, PetType.ARACHNID, PetType.OTHER -> Quaternary900
}

/** Light tone of each species group, used as the avatar background so the strong glyph reads against it. */
fun petTypeColorSoft(type: PetType): Color = when (type) {
    PetType.DOG, PetType.SNAKE, PetType.REPTILE -> Primary100
    PetType.CAT, PetType.RODENT, PetType.POULTRY -> Secondary100
    PetType.BIRD, PetType.FISH, PetType.AMPHIBIAN -> Tertiary100
    PetType.RABBIT, PetType.ARACHNID, PetType.OTHER -> Quaternary200
}
