package com.example.swapfood.dataStructures
import kotlinx.serialization.Serializable

@Serializable
data class ResultVote(
    val nombreDelRestaurante: String,
    val votantes: List<String>,
    val totalVotos: Int
)