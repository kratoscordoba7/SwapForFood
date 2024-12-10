package com.example.swapfood.ui.theme.screens

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
import kotlinx.coroutines.launch
import com.example.swapfood.R
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.theme.components.RestaurantCard
import com.example.swapfood.dataStructures.Restaurant

@Composable
fun StartGameScreen() {
    val restaurants = listOf(
        Restaurant(R.drawable.restaurant_1, "McDonald's", "Hamburguesas"),
        Restaurant(R.drawable.restaurant_2, "Telepizza", "Pizza"),
        Restaurant(R.drawable.restaurant_3, "ADK", "Kebab")
    )
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
                                                    // Continuar animación hacia la derecha
                                                    dragOffset.animateTo(
                                                        targetValue = 1000f, // Fuera de la pantalla
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                    score += 1
                                                    swipedRestaurants.add(currentRestaurantIndex)
                                                    currentRestaurantIndex++
                                                    dragOffset.snapTo(0f) // Reseteamos el offset para la siguiente carta
                                                }
                                                dragOffset.value < -300 -> {
                                                    // Continuar animación hacia la izquierda
                                                    dragOffset.animateTo(
                                                        targetValue = -1000f, // Fuera de la pantalla
                                                        animationSpec = tween(durationMillis = 300)
                                                    )
                                                    swipedRestaurants.add(currentRestaurantIndex)
                                                    currentRestaurantIndex++
                                                    dragOffset.snapTo(0f) // Reseteamos el offset para la siguiente carta
                                                }
                                                else -> {
                                                    // Volver al centro si no se supera el umbral
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
