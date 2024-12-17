package com.example.swapfood.utils

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

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

@Composable
fun LoadingOverlay(message: String = "Cargando...") {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray.copy(alpha = 0.3f)), // Fondo gris con 30% de opacidad
        color = Color.Transparent
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}