package com.example.swapfood.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Función para calcular un color más oscuro dinámicamente
fun Color.darker(factor: Float): Color {
    return Color(
        red = this.red * (1 - factor),
        green = this.green * (1 - factor),
        blue = this.blue * (1 - factor),
        alpha = this.alpha // Conserva el nivel de transparencia original
    )
}

// Definimos los colores
val BackgroundColor = Color(0xBBFDA403)

@Composable
fun ParticipantsList(
    participants: List<String>,
    showMore: Boolean,
    onRemoveParticipantClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Padding interno
    ) {
        items(participants) { participant ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(BackgroundColor) // Fondo más oscuro para cada usuario
                        .padding(horizontal = 16.dp, vertical = 12.dp), // Padding interno en cada tarjeta
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = if (!showMore) Arrangement.Center else Arrangement.SpaceBetween
                ) {
                    // Texto del participante
                    Text(
                        text = participant,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    if (showMore) {
                        Button(
                            onClick = { onRemoveParticipantClick(participant) },
                            modifier = Modifier.height(36.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFAF1740),
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                text = "Expulsar",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
