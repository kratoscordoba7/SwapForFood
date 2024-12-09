package com.example.swapfood

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.lifecycleScope
import com.example.swapfood.server.LobbyViewModel
import com.example.swapfood.ui.screens.CreateRoomScreen
import com.example.swapfood.ui.screens.MainScreen
import com.example.swapfood.ui.theme.basics.SwapFoodTheme
import com.example.swapfood.ui.theme.screens.StartGameScreen
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val lobbyViewModel by viewModels<LobbyViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainScreen()
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

                    // Proceder a crear la vista con el código recibido
                    setContent {
                        SwapFoodTheme {
                            CreateRoomScreen(
                                showMore,
                                roomCode,
                                mutableListOf(""),
                                onBackClick = { showMainScreen() },
                                onStartClick = { showGameScreen() },
                                lobbyViewModel,
                                this@MainActivity
                            )
                        }
                    }
                } catch (e: Exception) {
                    Log.e("MainActivity", "Error al crear la sala: ${e.message}")
                    // Opcional: Mostrar un mensaje de error al usuario
                }
            }
        } else {
            lifecycleScope.launch{
                try {
                    val lista = lobbyViewModel.joinLobby(this@MainActivity, username, code)
                    println("Desde MainActivity, la lista de usuarios es: $lista")
                    setContent {
                        SwapFoodTheme {
                            CreateRoomScreen(
                                showMore,
                                code,
                                lista,
                                onBackClick = { showMainScreen() },
                                onStartClick = { showGameScreen() },
                                lobbyViewModel,
                                this@MainActivity
                            )
                        }
                    }
                } catch (e: Exception){
                    Log.e("MainActivity", "Error al unirse a la sala: ${e.message}")
                    throw e
                }
            }

        }
    }

    // Función para mostrar la pantalla principal
    private fun showMainScreen() {
        setContent {
            SwapFoodTheme {
                MainScreen(
                    onCreateRoomClick = { name -> createLobby( name, true) },
                    onJoinRoomClick = { name, code -> createLobby(name,false, code) }
                )
            }
        }
    }

    // Función para mostrar la pantalla de juego
    private fun showGameScreen(){
        setContent {
            SwapFoodTheme {
                StartGameScreen ()
            }
        }
    }
}
