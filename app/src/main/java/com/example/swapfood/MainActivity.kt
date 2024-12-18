package com.example.swapfood

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

import androidx.lifecycle.lifecycleScope
import com.example.swapfood.dataStructures.Message
import com.example.swapfood.dataStructures.Restaurant
import com.example.swapfood.server.LobbyViewModel
import com.example.swapfood.ui.screens.CreateRoomScreen
import com.example.swapfood.ui.screens.MainScreen
import com.example.swapfood.ui.theme.basics.SwapFoodTheme
import com.example.swapfood.ui.theme.screens.StartGameScreen
import com.example.swapfood.utils.LoadingOverlay
import com.example.swapfood.utils.getCurrentGPSLocation
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class MainActivity : ComponentActivity() {
    private val PERMISSION_REQUEST_LOCATION = 1
    private val lobbyViewModel by viewModels<LobbyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkLocationPermissions() // Verificar permisos al iniciar
        showMainScreen()
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Si no está concedido, solicitar permiso
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                PERMISSION_REQUEST_LOCATION
            )
        }
    }

    // Función que reemplaza la vista actual con CreateRoomScreen
    private fun createLobby(username: String, showMore: Boolean, code: String = "99999") {
        println(showMore)
        if (showMore) {
            // Iniciar una coroutine en el scope de la actividad
            lifecycleScope.launch {
                try {
                    // Llamar a createLobby de forma suspendida y esperar el código
                    val roomCode = lobbyViewModel.createLobby(this@MainActivity, username)
                    val myName = lobbyViewModel.getMyUsername()

                    setContent {
                        SwapFoodTheme {
                            CreateRoomScreen(
                                showMore,
                                roomCode,
                                mutableListOf(""),
                                true,
                                myName,
                                onBackClick = { showMainScreen() },
                                onStartClick = {
                                    startGameAndWaitMessages()
                                },
                                lobbyViewModel,
                                this@MainActivity
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al crear la sala: ${e.message}")
                }
            }
        } else {
            lifecycleScope.launch {
                try {
                    val lista = lobbyViewModel.joinLobby(this@MainActivity, username, code)
                    val myName = lobbyViewModel.getMyUsername()
                    println("Desde MainActivity, la lista de usuarios es: $lista")
                    setContent {
                        SwapFoodTheme {
                            CreateRoomScreen(
                                showMore,
                                code,
                                lista,
                                false,
                                myName,
                                onBackClick = { showMainScreen() },
                                onStartClick = { showGameScreen(emptyList()) },
                                lobbyViewModel,
                                this@MainActivity
                            )
                        }
                    }
                    startGameAndWaitMessages() //Nos quedamos atentos a los mensajes
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al unirse a la sala: ${e.message}")
                    throw e
                }
            }

        }
    }

    private fun startGameAndWaitMessages() {
        lifecycleScope.launch {
            val location = getCurrentGPSLocation(this@MainActivity)
            if (location != null) {
                try {
                    val isLoading: MutableState<Boolean> = mutableStateOf(true)

                    // Enviar la ubicación y esperar los mensajes del servidor
                    lobbyViewModel.startGameWithLocation(this@MainActivity, location.first, location.second)

                    // Observar el estado de la sala
                    lobbyViewModel.roomStatus.collect { status ->
                        when (status) {
                            "LOADING" -> {
                                setContent {
                                    SwapFoodTheme {
                                        Box {
                                            if (isLoading.value) {
                                                LoadingOverlay("Esperando inicio del juego...")
                                            }
                                        }
                                    }
                                }
                            }
                            "NEW_RESTAURANT" -> {
                                // Cuando recibimos NEW_RESTAURANT estamos en el lobby y ya se nos han enviado los datos.
                                isLoading.value = false
                                val restaurants = lobbyViewModel.restaurants.value
                                showGameScreen(restaurants)
                            }
                            "GAME_RESULTS" -> { // Estado para resultados
                                val ourVoteResults = lobbyViewModel.gameResults.value
                                setContent {
                                    SwapFoodTheme {
                                        StartGameScreen(emptyList(), lobbyViewModel, ourVoteResults) // Sin restaurantes, finaliza
                                    }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al iniciar el juego: ${e.message}")
                }
            }
        }
    }

    // Función para mostrar la pantalla principal
    private fun showMainScreen() {
        setContent {
            SwapFoodTheme {
                MainScreen(
                    onCreateRoomClick = { name -> createLobby(name, true) },
                    onJoinRoomClick = { name, code -> createLobby(name, false, code) }
                )
            }
        }
    }

    // Función para mostrar la pantalla de juego con datos dinámicos
    private fun showGameScreen(restaurants: List<Restaurant>) {
        setContent {
            SwapFoodTheme {
                StartGameScreen(restaurants, lobbyViewModel)
            }
        }
    }
}
