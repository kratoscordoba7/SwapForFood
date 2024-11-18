package com.example.swapfood.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.swapfood.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp) // Mantiene el tamaño fijo del header
            .shadow(
                elevation = 30.dp,
                shape = RectangleShape,
            )
    ) {
        TopAppBar(
            modifier = Modifier.fillMaxSize(),
            title = {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Box(
                        modifier = Modifier
                            .size(100.dp) // Tamaño fijo para la imagen, independiente del contenedor
                            .align(Alignment.Center)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo),
                            contentDescription = "App Logo",
                            modifier = Modifier
                                .fillMaxSize() // La imagen ocupa todo el espacio del Box
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDA403) // Color de fondo profesional
            )
        )
    }
}
