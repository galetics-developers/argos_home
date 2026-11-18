package com.example.argos_home.ui.pets

import androidx.annotation.StringRes
import com.example.argos_home.R
import com.example.argos_home.domain.PetType

/** Localized label of each pet species, shared by the list, the detail and the type grid. */
@StringRes
fun petTypeLabel(type: PetType): Int = when (type) {
    PetType.DOG -> R.string.pet_type_dog
    PetType.CAT -> R.string.pet_type_cat
    PetType.BIRD -> R.string.pet_type_bird
    PetType.RABBIT -> R.string.pet_type_rabbit
    PetType.SNAKE -> R.string.pet_type_snake
    PetType.RODENT -> R.string.pet_type_rodent
    PetType.FISH -> R.string.pet_type_fish
    PetType.ARACHNID -> R.string.pet_type_arachnid
    PetType.REPTILE -> R.string.pet_type_reptile
    PetType.POULTRY -> R.string.pet_type_poultry
    PetType.AMPHIBIAN -> R.string.pet_type_amphibian
    PetType.OTHER -> R.string.pet_type_other
}
