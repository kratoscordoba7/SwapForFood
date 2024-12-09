package com.example.swapfood.server

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.swapfood.dataStructures.Message
import com.example.swapfood.utils.getDeviceIpAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

class LobbyViewModel : ViewModel() {

    // Actualizar la URL a 10.0.2.2 para que el emulador acceda al host
    private val webSocketClient = WebSocketClient("ws://10.0.2.2:8080/ws")

    // Estado de conexión
    private val _connectionState = MutableStateFlow("Disconnected")
    val connectionState: StateFlow<String> = _connectionState

    // Estado para mantener el código recibido
    private val _roomCode = MutableStateFlow<String?>(null)
    val roomCode: StateFlow<String?> = _roomCode

    // Lista de usuarios dentro de la sala
    private val _usersInLobby = MutableStateFlow<List<String>>(emptyList())
    val usersInLobby: StateFlow<List<String>> = _usersInLobby

    // Información del usuario eliminado
    private val _recentUserEliminated = MutableStateFlow<String?>(null)
    val recentUserElimnated: StateFlow<String?> = _recentUserEliminated

    // Estado de la sala
    private val _roomStatus = MutableStateFlow("ACTIVE") // Valores: "ACTIVE", "CLOSED"
    val roomStatus: StateFlow<String> = _roomStatus



    init {
        // Ejecutar la conexión de forma asíncrona sin bloquear el hilo principal
        viewModelScope.launch(Dispatchers.IO) {
            try {
                webSocketClient.connect()
                _connectionState.value = "Connected"

                // Escuchar mensajes del servidor
                webSocketClient.incomingMessages.collect { message ->
                    handleServerMessage(message)
                }
            } catch (e: Exception) {
                Log.e("LobbyViewModel", "Error al conectarse al WebSocket: ${e.message}")
                _connectionState.value = "Error: ${e.message}"
            }
        }
    }


    private fun handleServerMessage(message: String) {
        try {
            val jsonObject = JSONObject(message) // Parsear el mensaje JSON
            val messageContent = jsonObject.getString("message") // Extraer el campo "message"

            when {
                // CREAR LA SALA
                messageContent.startsWith("0000") -> {
                    // Extraer el código de la sala del campo "message"
                    val roomCode = messageContent.substring(4) // Asumiendo que el código comienza después del prefijo "0000"
                    viewModelScope.launch {
                        _roomCode.value = roomCode // Actualizar el flujo con el código
                    }
                }
                // UNIRSE A LA SALA y recibir lista de usuarios
                messageContent.startsWith("0001") -> {
                    try {
                        // Formato: "0001<numUsuarios><usuario1>.<usuario2>..."
                        val numUsers = messageContent.substring(4, 5).toInt() // Número de usuarios
                        val usersConcatenated = messageContent.substring(5) // Lista de usuarios concatenados

                        // Separar los usuarios usando el delimitador '.'
                        val usersInLobby = usersConcatenated.split(".").filter { it.isNotEmpty() }

                        // Actualizar el flujo con la lista de usuarios
                        viewModelScope.launch {
                            _usersInLobby.value = usersInLobby // Actualizar la lista
                        }
                        Log.d("LobbyViewModel", "Usuarios en la sala: $usersInLobby")
                    } catch (e: Exception) {
                        Log.e("LobbyViewModel", "Error al procesar la lista de usuarios: ${e.message}")
                    }
                }

                // Notificación de nuevo usuario
                messageContent.startsWith("USER_JOINED.") -> {
                    val newUser = messageContent.substring("USER_JOINED.".length)
                    viewModelScope.launch {
                        val currentList = _usersInLobby.value.toMutableList()
                        currentList.add(newUser)
                        _usersInLobby.value = currentList
                    }
                    Log.d("LobbyViewModel", "Nuevo usuario unido: $newUser")
                }

                // Notificación de usuario que salió
                messageContent.startsWith("USER_LEFT.") -> {
                    val userLeft = messageContent.substring("USER_LEFT.".length)
                    viewModelScope.launch {
                        val currentList = _usersInLobby.value.toMutableList()
                        currentList.remove(userLeft)
                        _usersInLobby.value = currentList
                    }
                    Log.d("LobbyViewModel", "Usuario salió: $userLeft")
                }

                // El lider dejó la sala
                messageContent.contains("ROOM_CLOSED") -> {
                    Log.d("LobbyViewModel", "La sala ha sido cerrada.")
                    viewModelScope.launch {
                        _roomStatus.value = "CLOSED"
                    }
                }

                // El lider dejó la sala
                messageContent.contains("REMOVED") -> {
                    Log.d("LobbyViewModel", "La sala ha sido cerrada.")
                    viewModelScope.launch {
                        _roomStatus.value = "CLOSED"
                    }
                }

                // Mensaje de error del servidor
                messageContent.startsWith("Error:") -> {
                    _connectionState.value = "Server Error: $messageContent"
                    Log.w("LobbyViewModel", "Error del servidor: $messageContent")
                }
                // Otros mensajes
                else -> {
                    Log.d("LobbyViewModel", "Mensaje no reconocido: $messageContent")
                }
            }
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al parsear el mensaje: ${e.message}")
        }
    }


    // Función para crear una lobby y esperar el código de la sala
    suspend fun createLobby(context: Context, username: String = "Kratos"): String {
        try {
            // Asegurarse de que la conexión está activa
            if (webSocketClient.session == null || !webSocketClient.session!!.isActive) {
                webSocketClient.connect()
                _connectionState.value = "Connected"
            }
            // Obtención de la ip
            val ip = getDeviceIpAddress(context)
            if (ip.isNullOrEmpty()) {
                Log.e("LobbyViewModel", "No se pudo obtener la IP del dispositivo")
                webSocketClient.close()
                throw IllegalStateException("No se pudo obtener la IP del dispositivo")
            }

            // Enviar mensaje de creación de sala
            val message = Message( ip, "0$username", System.currentTimeMillis())
            webSocketClient.sendMessage(message)

            // Esperar hasta que se reciba el código de la sala
            return _roomCode.filterNotNull().first()
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al crear la sala: ${e.message}")
            throw e
        }
    }

    suspend fun deleteUser(username: String, context: Context): Int{
        try {
            // Asegurarse de que la conexión está activa
            if (webSocketClient.session == null || !webSocketClient.session!!.isActive) {
                webSocketClient.connect()
                _connectionState.value = "Connected"
            }

            // Obtención de la ip
            val ip = getDeviceIpAddress(context)
            if (ip.isNullOrEmpty()) {
                Log.e("LobbyViewModel", "No se pudo obtener la IP del dispositivo")
                webSocketClient.close()
                throw IllegalStateException("No se pudo obtener la IP del dispositivo")
            }

            val message = Message(ip, "21$username", System.currentTimeMillis())
            webSocketClient.sendMessage(message)

            return 1
        } catch (e: Exception){
            Log.e("LobbyViewModel", "Error al eliminar usuario: ${e.message}")
            throw e
        }
    }

    suspend fun joinLobby(context: Context, username: String = "Kratos", code: String): List<String> {
        try {
            // Asegurarse de que la conexión está activa
            if (webSocketClient.session == null || !webSocketClient.session!!.isActive) {
                webSocketClient.connect()
                _connectionState.value = "Connected"
            }

            // Obtención de la IP
            val ip = getDeviceIpAddress(context)
            if (ip.isNullOrEmpty()) {
                Log.e("LobbyViewModel", "No se pudo obtener la IP del dispositivo")
                webSocketClient.close()
                throw IllegalStateException("No se pudo obtener la IP del dispositivo")
            }

            // Enviar mensaje para unirse a la sala
            val message = Message(ip, "1-$code$username", System.currentTimeMillis())
            webSocketClient.sendMessage(message)

            // Esperar hasta que se reciba la lista de usuarios
            return _usersInLobby.filter { it.isNotEmpty() }.first()
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al unirse a la sala: ${e.message}")
            throw e
        }
    }

    suspend fun endConnection(){
        try {
            webSocketClient.close()
        } catch (e: Exception){
            Log.e("LobbyViewModel", "Error al terminar la conexión: ${e.message}")
            throw e
        }
    }

    fun resetRoomStatus() {
        _roomStatus.value = "ACTIVE"
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient.close()
        }
    }
}
