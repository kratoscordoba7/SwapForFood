package com.example.swapfood.dataStructures
import kotlinx.serialization.Serializable

@Serializable
data class Restaurant(
    val imageRes: Int,
    val name: String,
    val carta: String
)

