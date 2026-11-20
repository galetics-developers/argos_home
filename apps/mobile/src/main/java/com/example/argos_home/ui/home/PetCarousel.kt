package com.example.argos_home.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.argos_home.domain.Pet

/** Horizontal carousel of every household pet; the content padding lets the next card peek at the edge. */
@Composable
fun PetCarousel(
    pets: List<Pet>,
    onOpenProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(items = pets, key = { it.id }) { pet ->
            HomePetCard(pet = pet, onOpenProfile = onOpenProfile)
        }
    }
}
