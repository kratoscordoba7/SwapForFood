package com.example.swapfood.server

import android.util.Log
import com.example.swapfood.dataStructures.Message
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Duration

class WebSocketClient(
    private val serverUrl: String
) {

    private val client = HttpClient(CIO) {
        install(WebSockets) {
            pingInterval = Duration.ofSeconds(15).toMillis() // Mantener la conexión viva con pings
        }
    }

    private val _incomingMessages = MutableSharedFlow<String>() // Flujo interno mutable
    val incomingMessages: SharedFlow<String> = _incomingMessages.asSharedFlow() // Flujo compartido para usuarios externos

    var session: DefaultClientWebSocketSession? = null
        private set

    suspend fun connect() {
        while (true) {
            try {
                Log.d("WebSocketClient", "Intentando conectar a $serverUrl")
                session = client.webSocketSession(serverUrl)
                Log.d("WebSocketClient", "Conectado a $serverUrl")
                break
            } catch (e: Exception) {
                Log.e("WebSocketClient", "Error al conectar: ${e.message}. Reintentando en 5 segundos...")
                delay(5000) // Reintentar cada 5 segundos
            }
        }

        // Escucha mensajes en un coroutine separado
        CoroutineScope(Dispatchers.IO).launch {
            session?.let { wsSession ->
                try {
                    for (frame in wsSession.incoming) {
                        if (frame is Frame.Text) {
                            val messageText = frame.readText()
                            _incomingMessages.emit(messageText) // Emitir el mensaje al flujo
                            Log.d("WebSocketClient", "Mensaje recibido: $messageText")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("WebSocketClient", "Error al recibir mensajes: ${e.message}")
                }
            }
        }
    }

    suspend fun sendMessage(message: Message) {
        session?.let {
            try {
                // Construir el JSON manualmente
                val jsonMessage = """{
                "sender": "${message.sender}",
                "content": "${message.content}",
                "timestamp": ${message.timestamp}
            }""".trimIndent() // Serialización manual del objeto Message

                it.send(Frame.Text(jsonMessage))
                Log.d("WebSocketClient", "Mensaje enviado: $jsonMessage")
            } catch (e: Exception) {
                Log.e("WebSocketClient", "Error al enviar mensaje: ${e.message}")
            }
        } ?: Log.e("WebSocketClient", "No hay sesión activa")
    }


    suspend fun close() {
        try {
            session?.close()
            client.close()
            Log.d("WebSocketClient", "Cliente WebSocket cerrado")
        } catch (e: Exception) {
            Log.e("WebSocketClient", "Error al cerrar WebSocket: ${e.message}")
        }
    }
}
