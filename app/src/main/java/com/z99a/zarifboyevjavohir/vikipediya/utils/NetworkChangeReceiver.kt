package com.z99a.zarifboyevjavohir.vikipediya.utils
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context?.let {
            val connectivityManager = it.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val isConnected = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val activeNetwork = connectivityManager.activeNetwork
                val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                networkCapabilities != null && networkCapabilities.hasCapability(
                    NetworkCapabilities.NET_CAPABILITY_INTERNET)
            } else {
                val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
                activeNetwork?.isConnectedOrConnecting == true
            }
            val message = if (isConnected) "Network is ON" else "Network is OFF"
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}

