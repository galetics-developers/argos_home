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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.data.ProfileRepository
import com.example.argos_home.domain.isValidEmail
import com.example.argos_home.ui.components.PasswordField
import com.example.argos_home.ui.household.PressFillButton
import com.example.argos_home.ui.household.SubmoduleHeader
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Change email submodule: a new email plus the current password to confirm, each with a generic error on failure. */
@Composable
fun ChangeEmailScreen(onBack: () -> Unit, onSaved: () -> Unit, modifier: Modifier = Modifier) {
    var email by remember { mutableStateOf("") }
    var current by remember { mutableStateOf("") }
    var submitted by remember { mutableStateOf(false) }

    val emailError = submitted && !isValidEmail(email)
    val passwordError = submitted && !ProfileRepository.isCurrentPassword(current)

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(modifier = Modifier.fillMaxSize()) {
            SubmoduleHeader(title = stringResource(R.string.setting_change_email_title), onBack = onBack)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    LabeledField(
                        label = stringResource(R.string.field_new_email),
                        value = email,
                        onValueChange = { email = it },
                        placeholder = stringResource(R.string.email_hint),
                        leadingIcon = R.drawable.ic_mail,
                        keyboardType = KeyboardType.Email,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (emailError) {
                        Spacer(Modifier.size(6.dp))
                        Text(
                            text = stringResource(R.string.error_email_generic),
                            style = MaterialTheme.typography.bodySmall,
                            color = Error1
                        )
                    }
                }
                PasswordField(
                    value = current,
                    onValueChange = { current = it },
                    label = stringResource(R.string.field_current_password),
                    placeholder = stringResource(R.string.hint_current_password),
                    leadingIcon = R.drawable.ic_lock,
                    isError = passwordError,
                    errorText = stringResource(R.string.error_current_password)
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
                        if (isValidEmail(email) && ProfileRepository.isCurrentPassword(current)) {
                            ProfileRepository.updateEmail(email)
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
