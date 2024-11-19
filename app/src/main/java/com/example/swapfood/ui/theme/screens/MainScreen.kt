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
                val guideline10 = createGuidelineFromTop(0.10f)
                val guideline25 = createGuidelineFromTop(0.25f)
                val guideline40 = createGuidelineFromTop(0.40f)
                val guideline55 = createGuidelineFromTop(0.55f)
                val guideline70 = createGuidelineFromTop(0.70f)
                val guideline90 = createGuidelineFromTop(0.90f)


                // Texto del título
                Text(
                    text = "Introduce un código de sala",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.constrainAs(titleRef) {
                        top.linkTo(guideline10)
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

                )

                // Botón Unirse
                if (code.isNotEmpty()) {
                    Button(
                        onClick = {
                            onJoinRoomClick(code)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(54.dp)
                            .constrainAs(joinButtonRef) {
                                top.linkTo(guideline40)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF898121),
                            contentColor = Color.White
                        )
                    ) {
                        Text("Unirse a una sala")
                    }
                }

                // Botón Crear sala
                Button(
                    onClick = { onCreateRoomClick() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .constrainAs(createButtonRef) {
                            top.linkTo(guideline90)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFB4F3A), // Rojo del botón
                        contentColor = Color.White // Color del texto blanco
                    ),
                    shape = RoundedCornerShape(8.dp) // Bordes redondeados como el ejemplo
                ) {
                    Text("Crear sala")
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
