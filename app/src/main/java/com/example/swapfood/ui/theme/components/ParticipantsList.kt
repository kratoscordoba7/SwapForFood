package com.example.swapfood.ui.theme.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
    primary: Boolean,
    myName: String,
    onRemoveParticipantClick: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp) // Padding interno
    ) {
        itemsIndexed(participants) { index, participant ->
            val (displayName, leaderLabel, myColor) = when {
                // Si el líder no soy yo
                index == 0 && participant != myName -> {
                    Triple(participant, "Líder", Color(0xBBFDA403))
                }
                // Si el líder soy yo
                index == 0 && participant == myName -> {
                    Triple(participant, "Líder - Tú", Color(0x99FDA403))
                }
                // Si no es el líder, pero soy yo
                index != 0 && participant == myName -> {
                    Triple("$participant (Tú)", "", Color(0x99FDA403))
                }
                // Caso por defecto: otros participantes
                else -> {
                    Triple(participant, "", Color(0xBBFDA403))
                }
            }

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
                        .background(myColor) // Fondo más oscuro para cada usuario
                        .padding(horizontal = 16.dp, vertical = 12.dp), // Padding interno en cada tarjeta
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Texto del participante a la izquierda
                    Text(
                        text = displayName,
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )

                    // Tarjeta con Rol a la derecha
                    if (leaderLabel.isNotEmpty()) {
                        Surface(
                            color = Color(0xFF3E3E3E), // Fondo oscuro para el rol
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Text(
                                text = leaderLabel,
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Botón "Expulsar"
                    if (showMore && (!primary || index != 0)) {
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
