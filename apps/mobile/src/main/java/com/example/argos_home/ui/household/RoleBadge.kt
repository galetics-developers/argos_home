package com.example.argos_home.ui.household

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.domain.MemberRole

/** Role pill: a shield for admin, a person for member, always with the role's text (never color alone). */
@Composable
fun RoleBadge(role: MemberRole, modifier: Modifier = Modifier) {
    val colors = roleBadgeColors(role)
    val icon = if (role == MemberRole.ADMIN) R.drawable.ic_shield else R.drawable.ic_nav_profile
    val label = if (role == MemberRole.ADMIN) R.string.role_admin else R.string.role_member
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(colors.background)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = colors.content,
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = stringResource(label),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = colors.content
        )
    }
}
