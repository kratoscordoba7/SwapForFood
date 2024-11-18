package com.example.swapfood

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.swapfood.ui.screens.CreateRoomScreen
import com.example.swapfood.ui.screens.MainScreen
import com.example.swapfood.ui.theme.basics.SwapFoodTheme
import com.example.swapfood.ui.theme.screens.StartGameScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showMainScreen()
    }

    // Función que reemplaza la vista actual con CreateRoomScreen
    private fun createLobby(showMore: Boolean, code: String = "99999") {
        // Aquí en el servidor se debería se debe crear una sala, con un código único, devolver
        // el código a esta función y pasarlo a la vista
        setContent {
            SwapFoodTheme {
                CreateRoomScreen(showMore,
                    code,
                    mutableListOf("Participant 1", "Participant 2", "Participant 3", "Participant 4"),
                    onBackClick = { showMainScreen() },
                    onStartClick = { showGameScreen() }
                )
            }
        }
    }

    // Función para mostrar a la pantalla principal
    private fun showMainScreen() {
        // Entiendo que cuando se van añadiendo participantes se deberá notificar al usuario
        // Para que la vista se actualice
        setContent {
            SwapFoodTheme {
                MainScreen(
                    onCreateRoomClick = { createLobby(true) },
                    onJoinRoomClick = { code -> createLobby(false, code) }
                )
            }
        }
    }

    private fun showGameScreen(){
        setContent {
            SwapFoodTheme {
                StartGameScreen ()
            }
        }
    }
}