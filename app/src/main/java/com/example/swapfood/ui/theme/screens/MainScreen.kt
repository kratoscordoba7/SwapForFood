// Archivo: MainScreen.kt
package com.example.swapfood.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.components.NumericInputField
import com.example.swapfood.utils.InputNameDialog
import com.example.swapfood.server.LobbyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    lobbyViewModel: LobbyViewModel = viewModel(),
    onCreateRoomClick: (String) -> Unit,
    onJoinRoomClick: (String, String) -> Unit
) {
    var code by remember { mutableStateOf("") } // Estado del código gestionado por el padre
    var showDialog by remember { mutableStateOf(false) }
    var showDialog2 by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { AppTopBar() },
        containerColor = Color(0xBBFDA403)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Introduce un código de sala",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(20.dp))
                // Campo de entrada numérica
                NumericInputField(
                    code = code, // Pasar el estado del código
                    onCodeChange = { inputCode ->
                        code = inputCode
                    },
                    codeLength = 5
                )

                Spacer(modifier = Modifier.height(48.dp))

                // Botón Unirse: visible solo cuando el código está completo
                if (code.length == 5) {
                    Button(
                        onClick = { showDialog2 = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF898121),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Unirse a una sala")
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // Botón Crear sala
                Button(
                    onClick = { showDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF898121),
                        contentColor = Color.White
                    )
                ) {
                    Text("Crear sala")
                }

                Spacer(modifier = Modifier.height(40.dp))
                if (showDialog) {
                    InputNameDialog(
                        onDismissRequest = { showDialog = false },
                        onConfirm = { name ->
                            showDialog = false
                            onCreateRoomClick(name)
                        }
                    )
                }

                if (showDialog2) {
                    InputNameDialog(
                        onDismissRequest = { showDialog2 = false },
                        onConfirm = { name ->
                            showDialog2 = false
                            lobbyViewModel.setCurrentUsername(name)
                            onJoinRoomClick(name, code)
                        }
                    )
                }
            }
        }
    }
}
