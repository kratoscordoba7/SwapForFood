// Archivo: NumericInputField.kt
package com.example.swapfood.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumericInputField(
    code: String, // Estado del código controlado desde el padre
    onCodeChange: (String) -> Unit, // Función para actualizar el código
    codeLength: Int = 5,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally)
        ) {
            for (i in 0 until codeLength) {
                val isCurrentDigit = i == code.length && code.length < codeLength

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp)
                        .then(
                            if (isCurrentDigit)
                                Modifier.scale(1.1f)
                            else
                                Modifier
                        )
                ) {
                    // Capa de fondo con efecto de desenfoque
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .background(
                                color = Color.White.copy(alpha = 0.3f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .blur(8.dp)
                    )
                    // Texto por encima del fondo desenfocado
                    Text(
                        text = code.getOrNull(i)?.toString() ?: "",
                        color = Color.Black,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TextField oculto para captura de la entrada
        TextField(
            value = code,
            onValueChange = { input ->
                if (input.length <= codeLength && input.all { it.isDigit() }) {
                    onCodeChange(input) // Actualiza el estado del código en el padre
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp) // Ocultar el TextField visualmente
                .background(Color.Transparent),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.Transparent,
                unfocusedTextColor = Color.Transparent,
                disabledTextColor = Color.Gray,
                cursorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent
            )
        )
    }
}
