package com.example.argos_home.ui.household

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.argos_home.R
import com.example.argos_home.domain.HouseholdColor
import com.example.argos_home.domain.MemberRole
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.Secondary600
import com.example.argos_home.ui.theme.White50

/** Household header: color by home, members counter, house avatar, name, role badge and the two actions. */
@Composable
fun HouseholdHeader(
    householdName: String,
    color: HouseholdColor,
    memberCount: Int,
    maxMembers: Int,
    localRole: MemberRole,
    onInvite: () -> Unit,
    onViewSchedule: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tones = householdTones(color)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(tones.headerBackground)
            .clipToBounds()
            // Soft decorative circles behind the content, clipped to the header bounds.
            .drawBehind {
                drawCircle(tones.headerCircle, radius = size.height * 0.42f, center = Offset(size.width * 0.9f, size.height * 0.08f))
                drawCircle(tones.headerCircle, radius = size.height * 0.18f, center = Offset(size.width * 0.62f, size.height * 0.02f))
            }
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.household_title).uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                color = tones.subtitle
            )
            Text(
                text = stringResource(R.string.household_members_limit, memberCount, maxMembers),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = tones.title
            )
        }
        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(tones.avatar),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_house_fill),
                    contentDescription = null,
                    tint = White50,
                    modifier = Modifier.size(28.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(
                    text = householdName,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = tones.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RoleBadge(localRole)
                    Text(
                        text = stringResource(R.string.home_members_count, memberCount),
                        style = MaterialTheme.typography.bodySmall,
                        color = tones.subtitle
                    )
                }
            }
        }
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilledAction(
                onClick = onInvite,
                icon = R.drawable.ic_user_plus,
                label = R.string.invite_member,
                modifier = Modifier.weight(1f)
            )
            // Outlined at rest; fills Secondary with white content on press/hover (RF-08).
            PressFillButton(
                onClick = onViewSchedule,
                fillColor = Secondary600,
                restContentColor = Primary600,
                restContainerColor = White50,
                borderColor = Primary600,
                modifier = Modifier.weight(1f)
            ) { contentColor ->
                Icon(
                    painter = painterResource(R.drawable.ic_calendar),
                    contentDescription = null,
                    tint = contentColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.view_schedule),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    }
}

/** Strong filled action (Invite member): dark primary with white content, as the prototype shows. */
@Composable
private fun FilledAction(
    onClick: () -> Unit,
    @DrawableRes icon: Int,
    @StringRes label: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(Primary600)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = White50,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = White50
        )
    }
}
