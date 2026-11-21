package com.example.argos_home.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.ProfileRepository
import com.example.argos_home.domain.isSecurePassword
import com.example.argos_home.domain.passwordsMatch
import com.example.argos_home.ui.components.PasswordField
import com.example.argos_home.ui.household.PressFillButton
import com.example.argos_home.ui.household.SubmoduleHeader
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Change password submodule: current, new and confirm fields, each with an eye and a generic error on failure. */
@Composable
fun ChangePasswordScreen(onBack: () -> Unit, onSaved: () -> Unit, modifier: Modifier = Modifier) {
    var current by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    // Errors are only surfaced after the first Update attempt, then recomputed live as the user corrects them.
    val currentError = submitted && !ProfileRepository.isCurrentPassword(current)
    val newError = submitted && !isSecurePassword(newPassword)
    val confirmError = submitted && !passwordsMatch(newPassword, confirm)

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SubmoduleHeader(title = stringResource(R.string.setting_change_password_title), onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PasswordField(
                    value = current,
                    onValueChange = { current = it },
                    label = stringResource(R.string.field_current_password),
                    placeholder = stringResource(R.string.hint_current_password),
                    leadingIcon = R.drawable.ic_lock,
                    isError = currentError,
                    errorText = stringResource(R.string.error_current_password)
                )
                PasswordField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = stringResource(R.string.field_new_password),
                    placeholder = stringResource(R.string.hint_new_password),
                    leadingIcon = R.drawable.ic_key,
                    isError = newError,
                    errorText = stringResource(R.string.error_new_password)
                )
                PasswordField(
                    value = confirm,
                    onValueChange = { confirm = it },
                    label = stringResource(R.string.field_confirm_password),
                    placeholder = stringResource(R.string.hint_confirm_password),
                    leadingIcon = R.drawable.ic_key,
                    isError = confirmError,
                    errorText = stringResource(R.string.error_password_mismatch)
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PressFillButton(
                    onClick = onBack,
                    fillColor = Primary600,
                    restContentColor = Primary600,
                    restContainerColor = White50,
                    borderColor = Primary600,
                    modifier = Modifier.weight(1f)
                ) { contentColor ->
                    Text(
                        text = stringResource(R.string.action_cancel),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = contentColor
                    )
                }
                Button(
                    onClick = {
                        submitted = true
                        val ok = ProfileRepository.isCurrentPassword(current) &&
                            isSecurePassword(newPassword) &&
                            passwordsMatch(newPassword, confirm)
                        if (ok) {
                            ProfileRepository.changePassword(newPassword)
                            onSaved()
                        }
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary600),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_check),
                        contentDescription = null,
                        tint = White50,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(stringResource(R.string.action_update), color = White50)
                }
            }
        }
    }
}
