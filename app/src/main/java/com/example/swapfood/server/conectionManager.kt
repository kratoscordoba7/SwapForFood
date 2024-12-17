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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONObject

class LobbyViewModel : ViewModel() {
    private var isInitialized = false

    // Convertir a var para permitir la reasignación
    private var webSocketClient = WebSocketClient("ws://10.0.2.2:8080/ws")

    // Estado de conexión
    private val _connectionState = MutableStateFlow("Disconnected")
    val connectionState: StateFlow<String> = _connectionState

    // Estado para mantener el código recibido
    private val _roomCode = MutableStateFlow<String?>(null)
    val roomCode: StateFlow<String?> = _roomCode

    // Lista de usuarios dentro de la sala (usando List y evitando duplicados manualmente)
    private val _usersInLobby = MutableStateFlow<List<String>>(emptyList())
    val usersInLobby: StateFlow<List<String>> = _usersInLobby

    // Información del usuario eliminado
    private val _recentUserEliminated = MutableStateFlow<String?>(null)
    val recentUserEliminated: StateFlow<String?> = _recentUserEliminated

    // Estado de la sala
    private val _roomStatus = MutableStateFlow("ACTIVE") // Valores: "ACTIVE", "CLOSED"
    val roomStatus: StateFlow<String> = _roomStatus

    // Nombre del usuario de la app, cuando entre a una sala
    private var currentUsername: String? = null

    private var you: String = ""
    fun setCurrentUsername(username: String) {
        currentUsername = username
    }

    fun getCurrentUsername(): String? {
        return currentUsername
    }

    private fun initialize() {
        if (isInitialized) return
        isInitialized = true

        // Lógica inicial del ViewModel
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (webSocketClient.session == null || !webSocketClient.session!!.isActive) {
                    webSocketClient.connect()
                    _connectionState.value = "Connected"
                }

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
                    creatingTheLobbyAsLeader(messageContent)
                }
                // UNIRSE A LA SALA y recibir lista de usuarios
                messageContent.startsWith("0001") -> {
                    joiningToTheLobby(messageContent)
                }

                // Notificación de nuevo usuario
                messageContent.startsWith("USER_JOINED.") -> {
                    notifyingExistingUsers(messageContent)
                }

                // Notificación de usuario que salió
                messageContent.startsWith("USER_LEFT.") -> {
                    notifyingSomeoneLeft(messageContent)
                }

                messageContent.startsWith("USER_REMOVED.") -> {
                    handleUserRemoved(messageContent)
                }


                // El lider dejó la sala
                messageContent.contains("ROOM_CLOSED") ||
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


    private fun handleUserRemoved(messageContent: String) {
        val userRemoved = messageContent.substring("USER_REMOVED.".length) // Extraer el usuario expulsado

        if (userRemoved == currentUsername) { // Suponiendo que el cliente almacena el nombre del usuario actual
            // Notificar que el usuario fue expulsado
            viewModelScope.launch {
                _roomStatus.value = "CLOSED" // Actualizar estado para que lo observe la interfaz
            }
            Log.d("LobbyViewModel", "Has sido expulsado de la sala.")
        } else {
            // Si otro usuario fue eliminado, actualizar la lista de usuarios
            viewModelScope.launch {
                val currentList = _usersInLobby.value.toMutableList()
                currentList.remove(userRemoved)
                _usersInLobby.value = currentList
            }
            Log.d("LobbyViewModel", "Usuario eliminado: $userRemoved")
        }
    }


    private fun creatingTheLobbyAsLeader(messageContent: String) {
        // Extraer el código de la sala del campo "message"
        Log.d("Vamoh por aqui", "El mensaje es: $messageContent")
        val roomCode = messageContent.substring(4, 9) // Asumiendo que el código comienza después del prefijo "0000", descartamos los 4 primeros y pillamos los 5 siguientes.
        you =  messageContent.substring(9)
        viewModelScope.launch {
            val currentList = _usersInLobby.value.toMutableList()
            if (!currentList.contains(you)) { // Evitar duplicados
                currentList.add(you)
                _usersInLobby.value = currentList
            }
            _roomCode.value = roomCode // Actualizar el flujo con el código
        }
    }

    private fun notifyingSomeoneLeft(messageContent: String) {
        val userLeft = messageContent.substring("USER_LEFT.".length)
        viewModelScope.launch {
            val currentList = _usersInLobby.value.toMutableList()
            currentList.remove(userLeft)
            _usersInLobby.value = currentList
        }
        Log.d("LobbyViewModel", "Usuario salió: $userLeft")
    }

    private fun notifyingExistingUsers(messageContent: String) {
        val newUser = messageContent.substring("USER_JOINED.".length)
        viewModelScope.launch {
            val currentList = _usersInLobby.value.toMutableList()
            if (!currentList.contains(newUser)) { // Evitar duplicados
                currentList.add(newUser)
                _usersInLobby.value = currentList
            }
        }
        Log.d("LobbyViewModel", "Nuevo usuario unido: $newUser")
    }

    private fun joiningToTheLobby(messageContent: String) {
        try {
            // Formato: "0001<numUsuarios><usuario1>.<usuario2>..."
            val numUsers = messageContent.substring(4, 5).toInt() // Número de usuarios
            val usersConcatenated = messageContent.substring(5) // Lista de usuarios concatenados

            // Separar los usuarios usando el delimitador '.'
            val usersInLobby = usersConcatenated.split(".").filter { it.isNotEmpty() }
            you = usersInLobby.last()

            // Actualizar el flujo con la lista de usuarios
            viewModelScope.launch {
                _usersInLobby.value = usersInLobby // Actualizar la lista
            }
            Log.d("LobbyViewModel", "Usuarios en la sala: $usersInLobby")
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al procesar la lista de usuarios: ${e.message}")
        }
    }

    // Función para crear una lobby y esperar el código de la sala
    suspend fun createLobby(context: Context, username: String = "Kratos"): String {
        try {
            val ip = ensureConnectionAndGetIP(context)

            // Enviar mensaje de creación de sala
            val message = Message(ip, "0$username", System.currentTimeMillis())
            webSocketClient.sendMessage(message)

            // Esperar hasta que se reciba el código de la sala
            return _roomCode.filterNotNull().first()
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al crear la sala: ${e.message}")
            throw e
        }
    }

    // Función para eliminar un usuario
    suspend fun deleteUser(username: String, context: Context): Int {
        try {
            val ip = ensureConnectionAndGetIP(context)

            val message = Message(ip, "21$username", System.currentTimeMillis())
            webSocketClient.sendMessage(message)

            return 1
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al eliminar usuario: ${e.message}")
            throw e
        }
    }

    // Función para unirse a una lobby
    suspend fun joinLobby(context: Context, username: String = "Kratos", code: String): List<String> {
        try {
            val ip = ensureConnectionAndGetIP(context)

            // Enviar mensaje para unirse a la sala
            val message = Message(ip, "1$code$username", System.currentTimeMillis())
            webSocketClient.sendMessage(message)

            // Esperar hasta que se reciba la lista de usuarios
            return _usersInLobby.filter { it.isNotEmpty() }.first()
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al unirse a la sala: ${e.message}")
            throw e
        }
    }

    private suspend fun ensureConnectionAndGetIP(context: Context): String {
        try {
            // Asegurar conexión
            if (_connectionState.value != "Connected") {
                initialize()
                _connectionState.filter { it == "Connected" }.first() // Esperar hasta que se conecte
            }

            // Obtener la IP del dispositivo
            val ip = getDeviceIpAddress(context)
            if (ip.isNullOrEmpty()) {
                Log.e("LobbyViewModel", "No se pudo obtener la IP del dispositivo")
                webSocketClient.close()
                throw IllegalStateException("No se pudo obtener la IP del dispositivo")
            }

            return ip
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error en la conexión: ${e.message}")
            throw e
        }
    }

    // Función para cerrar la conexión y resetear el estado
    suspend fun endConnection() {
        try {
            resetState()
        } catch (e: Exception) {
            Log.e("LobbyViewModel", "Error al cerrar la conexión: ${e.message}")
            throw e
        }
    }

    fun resetRoomStatus() {
        _roomStatus.value = "ACTIVE"
    }

    private suspend fun resetState() {
        _connectionState.value = "Disconnected"
        _roomCode.value = null
        _usersInLobby.value = emptyList()
        _recentUserEliminated.value = null
        _roomStatus.value = "ACTIVE"
        isInitialized = false // Permite una nueva inicialización

        // Cerrar la conexión actual
        webSocketClient.close()

        // Crear una nueva instancia de WebSocketClient
        webSocketClient = WebSocketClient("ws://10.0.2.2:8080/ws")
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.launch(Dispatchers.IO) {
            webSocketClient.close()
        }
    }

    fun getMyUsername(): String{
        return you
    }
}
