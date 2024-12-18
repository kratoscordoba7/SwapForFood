package com.example.swapfood.dataStructures
import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    val id: String,
    val name: String,
    val description: String = "No description available",
    val photo_url: String = ""
)