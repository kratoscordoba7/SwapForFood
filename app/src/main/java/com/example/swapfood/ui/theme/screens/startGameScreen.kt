package com.example.swapfood.ui.theme.screens

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
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

import com.example.swapfood.server.LobbyViewModel

@Composable
fun StartGameScreen(
    restaurants: List<Restaurant>,
    lobbyViewModel: LobbyViewModel,
    gameResults: List<ResultVote> = emptyList(),
    onRestartClick: () -> Unit
) {
    var currentRestaurantIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    val swipedRestaurants = remember { mutableSetOf<Int>() }
    val dragOffset = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    val totalWaitTime = restaurants.size * 10 // 10 segundos por restaurante

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
            if (restaurants.isNotEmpty()) {
                if (currentRestaurantIndex < restaurants.size) {
                    val currentRestaurant = restaurants[currentRestaurantIndex]

                    val backgroundColor = when {
                        dragOffset.value > 250 -> Color(0xDDA7D477)  // Fondo si se desliza a la derecha
                        dragOffset.value < -250 -> Color(0xBBAF1740) // Fondo si se desliza a la izquierda
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
                                    onDragStart = { },
                                    onDrag = { change, dragAmount ->
                                        coroutineScope.launch {
                                            dragOffset.snapTo(dragOffset.value + dragAmount.x)
                                        }
                                        change.consume()
                                    },
                                    onDragEnd = {
                                        coroutineScope.launch {
                                            if (!swipedRestaurants.contains(currentRestaurantIndex)) {
                                                val currentRestaurantId = restaurants[currentRestaurantIndex].id
                                                when {
                                                    dragOffset.value > 300 -> {
                                                        dragOffset.animateTo(
                                                            targetValue = 1000f,
                                                            animationSpec = tween(durationMillis = 300)
                                                        )
                                                        score += 1 // provisional
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
                    // Pantalla de espera mientras acaban el resto de jugadores
                    WaitingScreen(
                        totalWaitTime = totalWaitTime
                    )
                }
            } else {
                // Mostrar resultados si no hay restaurantes
                val top3 = gameResults.sortedByDescending { it.totalVotos }.take(3)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E1E1E)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Resultados de los Votos",
                            style = MaterialTheme.typography.headlineMedium.copy(color = Color.White),
                            modifier = Modifier.padding(bottom = 24.dp),
                            textAlign = TextAlign.Center
                        )

                        top3.forEachIndexed { index, resultVote ->
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
                                    val medalIcon = when (index) {
                                        0 -> Icons.Default.EmojiEvents // Oro
                                        1 -> Icons.Default.Star       // Plata
                                        2 -> Icons.Default.Grade      // Bronce
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

@Composable
fun WaitingScreen(totalWaitTime: Int) {
    // Fondo con degradado bonito
    val backgroundBrush = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0D0D0D),
            Color(0xFF1E1E1E),
            Color(0xFF323232)
        )
    )

    var progress by remember { mutableStateOf(1f) }

    LaunchedEffect(key1 = totalWaitTime) {
        val startTime = System.currentTimeMillis()
        val endTime = startTime + totalWaitTime * 1000L
        while (System.currentTimeMillis() < endTime) {
            val currentTime = System.currentTimeMillis()
            val elapsed = (currentTime - startTime).toFloat()
            val total = (endTime - startTime).toFloat()
            progress = 1f - (elapsed / total).coerceIn(0f, 1f)
            delay(100L)
        }
        progress = 0f
        // Aquí puedes agregar la lógica para lo que sucede cuando el tiempo se acaba
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = backgroundBrush),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Título llamativo
            Text(
                text = "¡Genial!",
                style = MaterialTheme.typography.headlineLarge.copy(color = Color.White),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            // Subtítulo informativo
            Text(
                text = "Esperando a que terminen el resto de jugadores...",
                style = MaterialTheme.typography.titleMedium.copy(color = Color.White),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(bottom = 40.dp)
            )

            // Indicador de progreso circular con tiempo restante
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(220.dp)
                    .padding(bottom = 40.dp)
            ) {
                // Fondo del círculo externo para contraste
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                    //.padding(8.dp) // Eliminado para igualar tamaños
                ) {
                    // Un círculo gris tenue de fondo
                    CircularProgressIndicator(
                        progress = 1f,
                        color = Color.DarkGray.copy(alpha = 0.5f),
                        strokeWidth = 10.dp,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                CircularProgressIndicator(
                    progress = progress,
                    color = Color(0xFFFFA500), // Color naranja vibrante
                    strokeWidth = 10.dp,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.height(20.dp)) // Espacio añadido para mover el texto hacia abajo

            // Mensaje adicional
            Text(
                text = "¡No te vayas! Pronto terminamos...",
                style = MaterialTheme.typography.bodyLarge.copy(color = Color(0xFFB0B0B0)),
                textAlign = TextAlign.Center
            )
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
