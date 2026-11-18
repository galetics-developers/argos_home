package com.example.argos_home.ui.pets

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.argos_home.R
import com.example.argos_home.ui.theme.Error1
import com.example.argos_home.ui.theme.Primary600
import com.example.argos_home.ui.theme.White50

/** Warning dialog before removing a pet; the destructive action is red and labeled, never color alone. */
@Composable
fun DeletePetDialog(petName: String, onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_alert_triangle),
                contentDescription = null,
                tint = Error1,
                modifier = Modifier.size(24.dp)
            )
        },
        title = { Text(stringResource(R.string.delete_pet_title, petName)) },
        text = { Text(stringResource(R.string.delete_pet_message)) },
        confirmButton = {
            // Filled red for the destructive action, matching the prototype's button styling.
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Error1)
            ) {
                Text(stringResource(R.string.action_delete), color = White50)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary600)
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
