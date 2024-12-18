package com.example.swapfood.ui.theme.components

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swapfood.R
import com.example.swapfood.dataStructures.Restaurant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    // Estado para almacenar el Bitmap de la imagen
    var imageBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var hasError by remember { mutableStateOf(false) }

    // Cargar la imagen cuando el composable se compone
    LaunchedEffect(restaurant.photo_url) {
        if (restaurant.photo_url.isNotEmpty()) {
            try {
                val bitmap = loadImage(restaurant.photo_url)
                imageBitmap = bitmap
                isLoading = false
            } catch (e: Exception) {
                e.printStackTrace()
                hasError = true
                isLoading = false
            }
        } else {
            isLoading = false
        }
    }

    Card(
        modifier = modifier
            .padding(8.dp) // Espaciado exterior de la tarjeta
            .fillMaxWidth()
            .height(200.dp), // Altura fija para mantener el tamaño
        shape = RoundedCornerShape(16.dp), // Bordes redondeados de la tarjeta
        colors = CardDefaults.cardColors(containerColor = Color.White), // Color de fondo de la tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Elevación de la tarjeta
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            when {
                isLoading -> {
                    // Placeholder mientras se carga la imagen
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.LightGray, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Cargando...",
                            color = Color.DarkGray,
                            fontSize = 16.sp
                        )
                    }
                }
                hasError -> {
                    // Mostrar un color o una imagen de error
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Red, RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Error al cargar imagen",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                    }
                }
                imageBitmap != null -> {
                    // Mostrar la imagen descargada
                    Image(
                        bitmap = imageBitmap!!.asImageBitmap(),
                        contentDescription = "Imagen de ${restaurant.name}",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                else -> {
                    // Si no hay URL, mostrar un color de fondo
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Gray, RoundedCornerShape(16.dp))
                    )
                }
            }

            // Superposición para mejorar la legibilidad del texto
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.7f)
                            ),
                            startY = 300f
                        )
                    )
            )

            // Contenido de texto sobre la imagen
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start
            ) {
                // Nombre del restaurante
                Text(
                    text = restaurant.name,
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Descripción del restaurante
                Text(
                    text = restaurant.description,
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Función para cargar una imagen desde una URL y devolverla como Bitmap.
 */
suspend fun loadImage(url: String): Bitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val urlConnection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            urlConnection.doInput = true
            urlConnection.connect()
            val inputStream: InputStream = urlConnection.inputStream
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
