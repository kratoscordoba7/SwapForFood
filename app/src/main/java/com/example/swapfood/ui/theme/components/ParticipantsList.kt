package com.example.swapfood.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ParticipantsList(
    participants: List<String>,
    showMore: Boolean,
    onRemoveParticipantClick: (String) -> Unit // Nuevo callback para manejar el clic en "Expulsar"
) {
    LazyColumn(
        horizontalAlignment = if (!showMore) Alignment.CenterHorizontally else Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 250.dp) // Altura máxima de 250 dp
            .border(
                width = 1.5.dp,
                color = Color.Black, // Color del borde
                shape = RoundedCornerShape(8.dp) // Bordes redondeados
            )
            .background(Color.Transparent) // Fondo transparente
            .padding(8.dp) // Padding interno
    ) {
        items(participants) { participant ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = if (!showMore) Arrangement.Center else Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = participant,
                    color = Color.Black,
                    fontSize = 16.sp
                )
                if (showMore) {
                    Button(
                        onClick = { onRemoveParticipantClick(participant) }, // Invoca el callback con el participante
                        modifier = Modifier
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFAF1740),
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Expulsar",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                }
            }
        }
    }
}
