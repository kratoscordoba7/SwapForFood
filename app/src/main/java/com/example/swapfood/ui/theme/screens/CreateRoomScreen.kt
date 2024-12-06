package com.example.swapfood.ui.screens

import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swapfood.server.LobbyViewModel
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.theme.components.ParticipantsList
import com.example.swapfood.utils.ConfirmationDialog

@Composable
fun CreateRoomScreen(
    showMore: Boolean,
    roomCode: String,
    participants: List<String>,
    onBackClick: () -> Unit,
    onStartClick: () -> Unit,
    lobbyViewModel: LobbyViewModel
) {
    // Crear una lista mutable para poder modificarla (remover participantes)
    val participantsState = remember { mutableStateListOf(*participants.toTypedArray()) }
    val text: String = if (showMore) "Lista de participantes" else "Esperando al lider"
    var delete by remember { mutableStateOf(false) }
    var currentParticipant by remember { mutableStateOf("") }

    // Observa el flujo de usuarios en la lobby
    val users by lobbyViewModel.usersInLobby.collectAsState()

    // Actualiza la lista de participantes cuando cambie el flujo
    LaunchedEffect(users) {
        participantsState.clear()
        participantsState.addAll(users)
    }

    Scaffold(
        topBar = { AppTopBar() }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xBBFDA403))
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Room Code Display
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Se podría añadir que si se clicka en el código se pegue en el portapapeles
                Text(
                    text = "Código: ",
                    color = Color.White,
                    fontSize = 24.sp,
                    modifier = Modifier.padding(end = 8.dp),
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = roomCode,
                    color = Color(0xFFFA4032),
                    fontSize = 30.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Participants List Title
            Text(
                text = text,
                color = Color.Black,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Participants List (Refactorizado)
            ParticipantsList(
                participants = participantsState,
                showMore = showMore,
                onRemoveParticipantClick = { participant ->
                    currentParticipant = participant
                    delete = true
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Row for "Atrás" y "Comenzar" botones
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Botón "Atrás"
                Button(
                    onClick = { onBackClick() },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFAF1740),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Atrás")
                }

                Spacer(modifier = Modifier.width(16.dp))

                if (showMore)
                // Botón "Comenzar"
                    Button(
                        onClick = {
                            /* Se ha de realizar una request al servidor para que se
                             * notifique a todos los usuarios de la sala de la siguiente vista */
                            onStartClick()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF898121),
                            contentColor = Color.White
                        )
                    ) {
                        Text(text = "Comenzar")
                    }
            }
            if (delete) {
                ConfirmationDialog(
                    onDismissRequest = {
                        delete = false
                        currentParticipant = ""
                    },
                    title = "Confirmar Expulsión",
                    text = "¿Estás seguro de que deseas expulsar a $currentParticipant?",
                    confirmButtonText = "Sí",
                    dismissButtonText = "No",
                    onConfirm = {
                        participantsState.remove(currentParticipant)
                        // Aquí también deberías notificar al servidor sobre la expulsión si es necesario
                        delete = false
                        currentParticipant = ""
                    },
                    onDismiss = {
                        // Acción al descartar, si es necesario
                        delete = false
                        currentParticipant = ""
                    },
                )
            }
        }
    }
}

