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
import com.example.swapfood.utils.getCurrentGPSLocation
import kotlinx.coroutines.launch

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
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

                    /*Si queremos obtener la ubicación al crear la sala
                    val location = getCurrentGPSLocation(this@MainActivity)
                    if (location != null) {
                        Log.d("LiderUbicacion", "Latitud: ${location.first}, Longitud: ${location.second}")
                        // Aquí puedes enviar la ubicación al servidor o guardarla localmente
                    }
                    */

                    // Proceder a crear la vista con el código recibido
                    setContent {
                        SwapFoodTheme {
                            CreateRoomScreen(
                                showMore,
                                roomCode,
                                mutableListOf(""),
                                onBackClick = { showMainScreen() },
                                onStartClick = {
                                    //Aquí podemos mandar la ubicación al servidor
                                    val location = getCurrentGPSLocation(this@MainActivity)
                                    if (location != null) {
                                        Log.d("LiderUbicacion", "Latitud: ${location.first}, Longitud: ${location.second}")
                                        // Aquí puedes enviar la ubicación al servidor o guardarla localmente
                                    }
                                    showGameScreen()
                                               },
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
