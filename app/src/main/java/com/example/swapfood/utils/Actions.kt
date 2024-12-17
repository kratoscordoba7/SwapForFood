package com.example.swapfood.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.util.Log

fun byBluetooth() {
    // Implementa la lógica para la funcionalidad Bluetooth aquí
    // Por ahora, simplemente registra una entrada
    Log.d("byBluetooth", "Función byBluetooth llamada")
}

@SuppressLint("MissingPermission") // Asegúrate de verificar permisos antes de llamar
fun getCurrentGPSLocation(context: Context): Pair<Double, Double>? {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    val providers = locationManager.getProviders(true)

    var location: Location? = null
    for (provider in providers) {
        location = locationManager.getLastKnownLocation(provider)
        if (location != null) break
    }

    return location?.let {
        val latitude = it.latitude
        val longitude = it.longitude
        Log.d("GPS", "Ubicación obtenida: Latitud = $latitude, Longitud = $longitude")
        Pair(latitude, longitude)
    } ?: run {
        Log.e("GPS", "No se pudo obtener la ubicación")
        null
    }
}