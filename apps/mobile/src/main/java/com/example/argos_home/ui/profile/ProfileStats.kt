package com.example.argos_home.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.MemberRole
import com.example.argos_home.ui.household.roleBadgeColors
import com.example.argos_home.ui.theme.Primary100
import com.example.argos_home.ui.theme.Primary700
import com.example.argos_home.ui.theme.Quaternary100
import com.example.argos_home.ui.theme.Quaternary900

/** The three Profile counters: homes the user belongs to, pets in the active home, and the user's role. */
@Composable
fun ProfileStats(
    householdCount: Int,
    petCount: Int,
    role: MemberRole,
    modifier: Modifier = Modifier
) {
    val roleColors = roleBadgeColors(role)
    val roleLabel = stringResource(if (role == MemberRole.ADMIN) R.string.role_admin else R.string.role_member)
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = R.drawable.ic_nav_household,
            value = householdCount.toString(),
            label = stringResource(R.string.profile_stat_households),
            container = Primary100,
            content = Primary700,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = R.drawable.ic_nav_pets,
            value = petCount.toString(),
            label = stringResource(R.string.profile_stat_pets),
            container = Quaternary100,
            content = Quaternary900,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = R.drawable.ic_nav_profile,
            value = roleLabel,
            label = stringResource(R.string.profile_stat_role),
            container = roleColors.background,
            content = roleColors.content,
            modifier = Modifier.weight(1f)
        )
    }
}
