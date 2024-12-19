package com.example.swapfood.dataStructures
import kotlinx.serialization.Serializable


@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val photo_url: String = "",
    val rating: String = "0",        // Calificación del restaurante (ej. "4.5")
    val distance: String = ""           // Distancia al restaurante en km (ej. "0.07")
)