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
            .height(65.dp) // Ajuste de altura con la sombra
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
                    Image(
                        painter = painterResource(id = R.drawable.logo),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .height(60.dp) // Tamaño adecuado del logo
                            .aspectRatio(1f) // Mantiene la proporción del logo
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0xFFFDA403) // Color de fondo profesional
            )
        )
    }
}
