package com.example.argos_home.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argos_home.R

/** "Account settings" section: a gear header and the three account actions (edit profile, password, email). */
@Composable
fun AccountSettings(
    onEditProfile: () -> Unit,
    onChangePassword: () -> Unit,
    onChangeEmail: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionHeader(icon = R.drawable.ic_settings, title = stringResource(R.string.account_settings))
        SettingRow(
            icon = R.drawable.ic_pencil,
            title = stringResource(R.string.setting_edit_profile_title),
            subtitle = stringResource(R.string.setting_edit_profile_subtitle),
            onClick = onEditProfile
        )
        SettingRow(
            icon = R.drawable.ic_key,
            title = stringResource(R.string.setting_change_password_title),
            subtitle = stringResource(R.string.setting_credentials_subtitle),
            onClick = onChangePassword
        )
        SettingRow(
            icon = R.drawable.ic_mail,
            title = stringResource(R.string.setting_change_email_title),
            subtitle = stringResource(R.string.setting_credentials_subtitle),
            onClick = onChangeEmail
        )
    }
}

/** Section header used across Profile: a small icon and an uppercase-weight title. */
@Composable
fun SectionHeader(icon: Int, title: String, trailing: String? = null, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.size(8.dp))
        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f)
        )
        if (trailing != null) {
            Text(
                text = trailing,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
