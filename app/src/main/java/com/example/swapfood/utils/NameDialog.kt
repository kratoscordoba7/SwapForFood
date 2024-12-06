package com.example.swapfood.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun InputNameDialog(
    onDismissRequest: () -> Unit,
    onConfirm: (String) -> Unit,
    title: String = "Introducir Nombre",
    placeholder: String = "Escribe tu nombre aquí",
    confirmButtonText: String = "Aceptar",
    dismissButtonText: String = "Cancelar"
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text(text = title)
        },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text(text = placeholder) },
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name)
                        onDismissRequest()
                    }
                }
            ) {
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = dismissButtonText)
            }
        }
    )
}
