package com.example.swapfood.ui.theme.screens

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import com.example.swapfood.R
import kotlinx.coroutines.launch

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp

import com.example.swapfood.dataStructures.Message
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.theme.components.RestaurantCard
import com.example.swapfood.dataStructures.Restaurant
import com.example.swapfood.dataStructures.ResultVote

import com.example.swapfood.server.LobbyViewModel // Importar LobbyViewModel


@Composable
fun StartGameScreen(restaurants: List<Restaurant>, lobbyViewModel: LobbyViewModel, gameResults: List<ResultVote> = emptyList(), onRestartClick: () -> Unit) {
    var currentRestaurantIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val swipedRestaurants = remember { mutableSetOf<Int>() }
    val dragOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = { AppTopBar() }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xBBFDA403)),
            contentAlignment = Alignment.Center
        ) {
            if (currentRestaurantIndex < restaurants.size) {
                val currentRestaurant = restaurants[currentRestaurantIndex]

                val backgroundColor = when {
                    dragOffset.value > 250 -> Color(0xDDA7D477)  // Deslizado a la derecha
                    dragOffset.value < -250 -> Color(0xBBAF1740)  // Deslizado a la izquierda
                    else -> Color.Transparent
                }

                RestaurantCard(
                    restaurant = currentRestaurant,
                    modifier = Modifier
                        .size(width = 350.dp, height = 600.dp)
                        .graphicsLayer(
                            translationX = dragOffset.value,
                            rotationZ = dragOffset.value * 0.05f
                        )
                        .background(backgroundColor)
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    coroutineScope.launch {
                                        dragOffset.snapTo(dragOffset.value + dragAmount.x)
                                    }
                                    change.consume()
                                },
                                onDragEnd = {
                                    coroutineScope.launch {
                                        if (!swipedRestaurants.contains(currentRestaurantIndex)) {
                                            val currentRestaurantId = restaurants[currentRestaurantIndex].id // Captura el ID aquí
                                            when {
                                                dragOffset.value > 300 -> {
                                                    dragOffset.animateTo(
                                                        targetValue = 1000f,
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                    score += 1 //esto se podrá quitar
                                                    sendVoteToServer(lobbyViewModel, "0", currentRestaurantId)
                                                    swipedRestaurants.add(currentRestaurantIndex)
                                                    currentRestaurantIndex++
                                                    dragOffset.snapTo(0f)
                                                }
                                                dragOffset.value < -300 -> {
                                                    dragOffset.animateTo(
                                                        targetValue = -1000f,
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                    sendVoteToServer(lobbyViewModel, "1", currentRestaurantId)
                                                    swipedRestaurants.add(currentRestaurantIndex)
                                                    currentRestaurantIndex++
                                                    dragOffset.snapTo(0f)
                                                }
                                                else -> {
                                                    dragOffset.animateTo(
                                                        targetValue = 0f,
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                }
                                            }
                                        } else {
                                            dragOffset.snapTo(0f)
                                        }
                                    }
                                }
                            )
                        }
                )
            } else {
                // Ordenar los resultados por total de votos de forma descendente y tomar los top 3
                val top3 = gameResults.sortedByDescending { it.totalVotos }.take(3)

                // Fondo decorativo opcional, por ejemplo, con un degradado o imagen
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1E1E)), // Fondo oscuro elegante
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Título de la pantalla de resultados
                        Text(
                            text = "Resultados de los Votos",
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                            modifier = Modifier.padding(bottom = 24.dp)
                        )

                        // Lista de los top 3 restaurantes
                        top3.forEachIndexed { index, resultVote ->
                            // Card para cada restaurante
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp)
                                    .shadow(8.dp, shape = MaterialTheme.shapes.medium),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF2C2C2C)),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    // Icono de medalla según la posición usando Material Icons
                                    val medalIcon = when (index) {
                                        0 -> Icons.Default.EmojiEvents // Oro
                                        1 -> Icons.Default.Star // Plata
                                        2 -> Icons.Default.Grade // Bronce
                                        else -> Icons.Default.Star
                                    }
                                    val medalColor = when (index) {
                                        0 -> Color(0xFFFFD700) // Oro
                                        1 -> Color(0xFFC0C0C0) // Plata
                                        2 -> Color(0xFFCD7F32) // Bronce
                                        else -> Color.Gray
                                    }

                                    Icon(
                                        imageVector = medalIcon,
                                        contentDescription = "Medalla",
                                        tint = medalColor,
                                        modifier = Modifier.size(40.dp)
                                    )

                                    Spacer(modifier = Modifier.width(16.dp))

                                    // Información del restaurante
                                    Column(
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(
                                            text = resultVote.nombreDelRestaurante,
                                            style = MaterialTheme.typography.titleLarge.copy(color = Color.White)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = "Votantes: ${resultVote.votantes.joinToString(", ")}",
                                            style = MaterialTheme.typography.bodyMedium.copy(color = Color(0xFFB0B0B0))
                                        )
                                    }

                                    // Total de votos con icono de voto
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ThumbUp,
                                            contentDescription = "Votos",
                                            tint = Color(0xFF4CAF50),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = resultVote.totalVotos.toString(),
                                            style = MaterialTheme.typography.bodyLarge.copy(color = Color.White)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Botón para reiniciar el juego o regresar
                        Button(
                            onClick = { onRestartClick() },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .fillMaxWidth(0.6f)
                                .height(50.dp)
                        ) {
                            Text(
                                text = "Pantalla de Inicio",
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

// Función para enviar voto al servidor
private fun sendVoteToServer(lobbyViewModel: LobbyViewModel, voteType: String, restaurantId: String) {
    val voteMessage = "5$voteType$restaurantId"
    lobbyViewModel.viewModelScope.launch {
        try {
            val message = Message(sender = "0", content = voteMessage, timestamp = System.currentTimeMillis())
            lobbyViewModel.sendRestaurantVote(message)
        } catch (e: Exception) {
            Log.e("StartGameScreen", "Error al enviar voto: ${e.message}")
        }
    }
}