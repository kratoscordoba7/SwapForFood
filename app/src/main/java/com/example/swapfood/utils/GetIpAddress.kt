package com.example.swapfood.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import androidx.core.content.ContextCompat.getSystemService
import com.example.swapfood.server.LobbyViewModel

fun getDeviceIpAddress(context: Context): String? {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetwork = connectivityManager.activeNetwork ?: return null
    val linkProperties = connectivityManager.getLinkProperties(activeNetwork) ?: return null

    for (address in linkProperties.linkAddresses) {
        val ip = address.address.hostAddress
        if (!ip.contains(":")) { // Filtrar IPv4
            return ip
        }
    }
    return null
}


