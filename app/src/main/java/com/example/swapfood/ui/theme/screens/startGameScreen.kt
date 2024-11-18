package com.example.swapfood.ui.theme.screens

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
import com.example.swapfood.R
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.theme.components.RestaurantCard
import com.example.swapfood.ui.theme.dataStructures.Restaurant

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
    var dragOffset by remember { mutableStateOf(0f) }

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

                // Determinamos el color de fondo basado en el offset
                val backgroundColor = when {
                    dragOffset > 250 -> Color(0xBB00FF9C)  // Deslizado a la derecha
                    dragOffset < -250 -> Color(0xBBAF1740)  // Deslizado a la izquierda
                    else -> Color.Transparent        // Fondo transparente cuando no se ha superado el umbral
                }

                RestaurantCard(
                    restaurant = currentRestaurant,
                    modifier = Modifier
                        .size(width = 350.dp, height = 600.dp)
                        .graphicsLayer(
                            translationX = dragOffset,
                            rotationZ = dragOffset * 0.05f  // Ajusta este valor para mayor o menor curvatura
                        )
                        .background(backgroundColor) // Aplicamos el color de fondo dinámico
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDrag = { change, dragAmount ->
                                    dragOffset += dragAmount.x
                                    change.consume()
                                },
                                onDragEnd = {
                                    if (!swipedRestaurants.contains(currentRestaurantIndex)) {
                                        when {
                                            dragOffset > 250 -> {
                                                // Deslizar a la derecha (like)
                                                score += 1
                                                swipedRestaurants.add(currentRestaurantIndex)
                                                currentRestaurantIndex++
                                            }
                                            dragOffset < -250 -> {
                                                // Deslizar a la izquierda (dislike)
                                                swipedRestaurants.add(currentRestaurantIndex)
                                                currentRestaurantIndex++
                                            }
                                            else -> {
                                                // Volver a la posición original si no se supera el umbral
                                            }
                                        }
                                    }
                                    dragOffset = 0f
                                }
                            )
                        }
                )
            } else {
                // Mostrar puntuación al final
                Text(
                    text = "Juego terminado! Puntuación: $score",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

