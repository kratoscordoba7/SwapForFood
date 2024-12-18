package com.example.swapfood.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.swapfood.dataStructures.Restaurant

@Composable
fun RestaurantCard(
    restaurant: Restaurant,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .padding(8.dp), // Espaciado exterior de la tarjeta
        shape = RoundedCornerShape(16.dp), // Bordes redondeados de la tarjeta
        colors = CardDefaults.cardColors(containerColor = Color.White), // Color de fondo de la tarjeta
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp) // Elevación de la tarjeta
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, // Centrar contenido horizontalmente
            modifier = Modifier
                .fillMaxWidth()
        ) {
            /* Imagen del restaurante
            Image(
                painter = painterResource(id = restaurant.imageRes),
                contentDescription = "Imagen de ${restaurant.name}",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                contentScale = ContentScale.Crop
            )
            */

            Spacer(modifier = Modifier.height(12.dp))

            // Nombre del restaurante y breve carta
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                // Nombre del restaurante
                Text(
                    text = restaurant.name,
                    color = Color.Black,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Breve carta
                Text(
                    text = "Breve carta",
                    color = Color.Gray,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Carta del restaurante
            Text(
                text = restaurant.description,
                color = Color.Gray,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                modifier = Modifier
                    .padding(horizontal = 12.dp)
                    .fillMaxWidth(),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}
