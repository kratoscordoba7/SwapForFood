// Archivo: MainScreen.kt
package com.example.swapfood.ui.screens

import androidx.compose.foundation.BorderStroke
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

// Agrega este import
import androidx.constraintlayout.compose.ConstraintLayout

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
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Crea referencias para los composables a posicionar
                val (
                    titleRef,
                    numericInputFieldRef,
                    joinButtonRef,
                    createButtonRef
                // bluetoothImageRef
                ) = createRefs()

                // Crea guías en porcentajes específicos
                val guidelines = List(101) { if (it == 0) null else createGuidelineFromTop(it / 100f) }


                // Texto del título
                Text(
                    text = "Introduce un código de sala",
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(guidelines[11]!!)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                // Campo de entrada numérica
                NumericInputField(
                    codeLength = 5,
                    onComplete = { inputCode ->
                        code = inputCode
                    },
                    modifier = Modifier.constrainAs(numericInputFieldRef) {
                        top.linkTo(guidelines[17]!!) // Ajusta la posición según necesites
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                )

                // Botón Unirse
                if (code.isNotEmpty()) {

                    Button(
                        onClick = { onJoinRoomClick(code) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp) // Altura igual que el botón de referencia
                            .constrainAs(joinButtonRef) {
                                top.linkTo(guidelines[32]!!)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4CAF50), // Verde del botón
                            contentColor = Color.White // Color del texto blanco
                        ),
                        shape = RoundedCornerShape(8.dp) // Bordes redondeados similares al botón de referencia
                    ) {
                        Text(
                            text = "Unirse a una sala",
                            fontSize = 17.sp // Tamaño de fuente consistente
                        )
                    }


                }

                // Botón Crear sala
                Button(
                    onClick = { onCreateRoomClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .constrainAs(createButtonRef) {
                            top.linkTo(guidelines[88]!!)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFB4F3A), // Rojo del botón
                        contentColor = Color.White // Color del texto blanco
                    ),
                    shape = RoundedCornerShape(8.dp) // Bordes redondeados como el ejemplo
                ) {
                    Text("Crear sala", fontSize = 17.sp)
                }

                /*
                // Botón de Bluetooth
                Image(
                    painter = painterResource(id = R.drawable.bluetooth),
                    contentDescription = "Bluetooth",
                    modifier = Modifier
                        .size(80.dp)
                        .clickable(onClick = { byBluetooth() })
                        .padding(8.dp)
                        .constrainAs(bluetoothImageRef) {
                            top.linkTo(guideline70)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                )
                */
            }
        }
    }
}
