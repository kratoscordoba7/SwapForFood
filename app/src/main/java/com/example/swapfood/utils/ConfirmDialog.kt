package com.example.swapfood.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier

@Composable
fun ConfirmationDialog(
    onDismissRequest: () -> Unit,
    title: String,
    text: String,
    confirmButtonText: String = "Confirmar",
    dismissButtonText: String = "Cancelar",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    icon: ImageVector? = null // Icono opcional
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            if (title.isNotEmpty()) {
                Text(text = title)
            }
        },
        text = {
            Text(text = text)
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismissRequest()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismiss()
                    onDismissRequest()
                }
            ) {
                Text(text = dismissButtonText)
            }
        },
        icon = {
            icon?.let {
                Icon(
                    imageVector = it,
                    contentDescription = "Icono de Confirmación",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
