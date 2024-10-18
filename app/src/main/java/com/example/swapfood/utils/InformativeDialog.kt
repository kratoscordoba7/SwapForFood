package com.example.swapfood.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun InformativeDialog(
    onDismissRequest: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    buttonText: String = "Aceptar", // Valor por defecto
    icon: ImageVector? = null // Icono opcional
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            if (dialogTitle.isNotEmpty()) {
                Text(text = dialogTitle)
            }
        },
        text = {
            Text(text = dialogText)
        },
        confirmButton = {
            TextButton(
                onClick = onDismissRequest
            ) {
                Text(text = buttonText)
            }
        },
        // Eliminamos el botón de "Dismiss" para tener solo un botón
        icon = {
            icon?.let {
                Icon(imageVector = it, contentDescription = "Dialog Icon")
            }
        }
    )
}
