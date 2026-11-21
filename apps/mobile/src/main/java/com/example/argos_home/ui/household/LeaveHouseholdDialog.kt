package com.example.argos_home.ui.household

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Warning dialog before leaving the household; same design as Remove, with the leave action in red. */
@Composable
fun LeaveHouseholdDialog(
    householdName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_alert_triangle),
                contentDescription = null,
                tint = Error1,
                modifier = Modifier.size(28.dp)
            )
        },
        title = { Text(stringResource(R.string.leave_household_title, householdName)) },
        text = { Text(stringResource(R.string.leave_household_message)) },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Error1)
            ) {
                Text(stringResource(R.string.action_leave), color = White50, fontWeight = FontWeight.SemiBold)
            }
        },
        dismissButton = {
            PressFillButton(
                onClick = onDismiss,
                fillColor = Primary600,
                shape = RoundedCornerShape(12.dp)
            ) { contentColor ->
                Text(
                    text = stringResource(R.string.action_cancel),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = contentColor
                )
            }
        }
    )
}
