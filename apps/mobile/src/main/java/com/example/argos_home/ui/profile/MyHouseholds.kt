package com.example.argos_home.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.MemberRole
import com.example.argos_home.domain.ProfileHousehold
import com.example.argos_home.domain.ProfileHouseholds
import com.example.argos_home.ui.household.PressFillButton
import com.example.argos_home.ui.household.householdTones
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Secondary600
import com.example.argos_home.ui.theme.White50

/** "My households" section: the homes the user belongs to, with role and active mark, plus create and sign-out actions. */
@Composable
fun MyHouseholds(
    households: List<ProfileHousehold>,
    onCreateOrJoin: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(
            icon = R.drawable.ic_nav_household,
            title = stringResource(R.string.my_households),
            trailing = stringResource(R.string.households_count, households.size, ProfileHouseholds.MAX_HOUSEHOLDS)
        )
        households.forEach { home -> HouseholdCard(home) }

        PressFillButton(
            onClick = onCreateOrJoin,
            fillColor = Primary600,
            restContentColor = Primary600,
            restContainerColor = White50,
            borderColor = Primary600,
            modifier = Modifier.fillMaxWidth()
        ) { contentColor ->
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.create_or_join_household),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }

        PressFillButton(
            onClick = onSignOut,
            fillColor = Secondary600,
            restContentColor = Secondary600,
            restContainerColor = White50,
            borderColor = Secondary600,
            modifier = Modifier.fillMaxWidth()
        ) { contentColor ->
            Icon(
                painter = painterResource(R.drawable.ic_log_out),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.size(8.dp))
            Text(
                text = stringResource(R.string.sign_out),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = contentColor
            )
        }
    }
}

/** One home the user belongs to: a house avatar in the home's color, name, "N members · role" and an Active mark. */
@Composable
private fun HouseholdCard(home: ProfileHousehold, modifier: Modifier = Modifier) {
    val tones = householdTones(home.color)
    val roleLabel = stringResource(if (home.role == MemberRole.ADMIN) R.string.role_admin else R.string.role_member)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(tones.headerBackground)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(tones.avatar),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_house_fill),
                contentDescription = null,
                tint = White50,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(Modifier.size(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = home.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                color = tones.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = stringResource(R.string.profile_members_role, home.memberCount, roleLabel.lowercase()),
                style = MaterialTheme.typography.bodySmall,
                color = tones.subtitle,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        if (home.active) {
            Text(
                text = stringResource(R.string.household_active),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = White50,
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(tones.avatar)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            )
        }
    }
}
