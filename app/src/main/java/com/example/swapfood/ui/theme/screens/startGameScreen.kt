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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.swapfood.R
import com.example.swapfood.dataStructures.Message
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.theme.components.RestaurantCard
import com.example.swapfood.dataStructures.Restaurant

import com.example.swapfood.server.LobbyViewModel // Importar LobbyViewModel


@Composable
fun StartGameScreen(restaurants: List<Restaurant>, lobbyViewModel: LobbyViewModel) {
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
                                            when {
                                                dragOffset.value > 300 -> {
                                                    dragOffset.animateTo(
                                                        targetValue = 1000f,
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                    score += 1 //esto se podrá quitar
                                                    sendVoteToServer(lobbyViewModel, "0", currentRestaurant.id)
                                                    swipedRestaurants.add(currentRestaurantIndex)
                                                    currentRestaurantIndex++
                                                    dragOffset.snapTo(0f)
                                                }
                                                dragOffset.value < -300 -> {
                                                    dragOffset.animateTo(
                                                        targetValue = -1000f,
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                    sendVoteToServer(lobbyViewModel, "1", currentRestaurant.id)
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
                Text(
                    text = "Juego terminado! Puntuación: $score",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
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