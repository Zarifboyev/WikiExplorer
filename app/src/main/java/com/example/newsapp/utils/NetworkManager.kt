package com.example.newsapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object NetworkManager {

    private val _isNetworkAvailable = MutableStateFlow(false)
    val isNetworkAvailable: StateFlow<Boolean> = _isNetworkAvailable

    private val _isSlowOrInsufficientNetwork = MutableStateFlow(false)
    val isSlowOrInsufficientNetwork: StateFlow<Boolean> = _isSlowOrInsufficientNetwork

    fun registerNetworkCallback(context: Context) {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isNetworkAvailable.value = true
                checkNetworkSpeed(connectivityManager, network)
            }

            override fun onLost(network: Network) {
                _isNetworkAvailable.value = false
                _isSlowOrInsufficientNetwork.value = false
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                checkNetworkSpeed(connectivityManager, network)
            }
        })
    }

    private fun checkNetworkSpeed(connectivityManager: ConnectivityManager, network: Network) {
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        networkCapabilities?.let {
            val downSpeed = it.linkDownstreamBandwidthKbps

            // Consider network slow if download speed is less than 1Mbps (1024Kbps)
            _isSlowOrInsufficientNetwork.value = downSpeed < 1024
        } ?: run {
            _isSlowOrInsufficientNetwork.value = true
        }
    }
}