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
fun NumericInputField(codeLength: Int = 5, onComplete: (String) -> Unit) {
    var code by remember { mutableStateOf("") }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
           // .padding(5.dp) // Opcional: Añade padding al contenedor
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .wrapContentWidth(Alignment.CenterHorizontally) // Eliminamos 'fillMaxWidth()' para corregir el ancho de los recuadros
        ) {
            for (i in 0 until codeLength) {
                val isCurrentDigit = i == code.length && code.length < codeLength // Verificamos si es el dígito actual

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(60.dp) // Asegura que todos los cuadros tengan el mismo tamaño
                        .then(
                            if (isCurrentDigit)
                                Modifier.scale(1.1f) // Efecto de escalado para el dígito actual
                            else
                                Modifier
                        )
                ) {
                    // Capa de fondo con efecto de desenfoque
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .border(1.dp, Color.White.copy(alpha = 0.5f), RoundedCornerShape(12.dp)) // Borde semitransparente
                            .background(
                                color = Color.White.copy(alpha = 0.3f), // Fondo semitransparente
                                shape = RoundedCornerShape(12.dp)
                            )
                            .blur(8.dp) // Efecto de desenfoque para glassmorfismo
                    )
                    // Texto por encima del fondo desenfocado
                    Text(
                        text = code.getOrNull(i)?.toString() ?: "",
                        color = Color.Black, // Letras negras
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
                    code = input
                    if (code.length == codeLength) {
                        onComplete(code) // Llama a la función cuando el código esté completo
                    }
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
