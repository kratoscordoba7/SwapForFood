// Archivo: MainScreen.kt
package com.example.swapfood.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swapfood.R
import com.example.swapfood.ui.components.AppTopBar
import com.example.swapfood.ui.components.NumericInputField
import com.example.swapfood.utils.byBluetooth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    onCreateRoomClick: () -> Unit,
    onJoinRoomClick: (String) -> Unit
) {
    var code by remember { mutableStateOf("") }

    Scaffold(
        topBar = { AppTopBar() },
        containerColor = Color(0xBBFDA403)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentAlignment = Alignment.Center // Centra el contenido tanto vertical como horizontalmente
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center // Centra los elementos verticalmente dentro de la Column
            ) {
                Text(
                    text = "Introduce un código de sala",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(20.dp))
                NumericInputField(codeLength = 6) { inputCode ->
                    code = inputCode
                }

                Spacer(modifier = Modifier.height(48.dp))

                // Botón Unirse
                if (code.isNotEmpty()) {
                    Button(
                        onClick = {
                            onJoinRoomClick(code)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp),
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
                    onClick = { onCreateRoomClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFB4F3A), // Rojo del botón
                        contentColor = Color.White // Color del texto blanco
                    ),
                    shape = RoundedCornerShape(8.dp) // Bordes redondeados como el ejemplo
                ) {
                    Text("Crear sala")
                }

                Spacer(modifier = Modifier.height(40.dp))

                /* Botón de Bluetooth
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.bluetooth),
                        contentDescription = "Bluetooth",
                        modifier = Modifier
                            .size(80.dp)
                            .clickable(onClick = { byBluetooth() })
                            .padding(8.dp),
                    )
                }
                 */
            }
        }
    }
}
