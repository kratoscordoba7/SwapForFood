package com.example.swapfood.dataStructures
import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val sender: String,
    val content: String,
    val timestamp: Long
)
