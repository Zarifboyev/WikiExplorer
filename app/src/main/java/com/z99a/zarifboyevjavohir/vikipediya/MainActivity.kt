/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.z99a.zarifboyevjavohir.vikipediya

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var navView: BottomNavigationView
    private lateinit var navHostFragment: NavHostFragment
    private var lastSelectedItemId: Int = R.id.navigation_home
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private var networkConnectedButIsItAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navView = binding.navView
        navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_activity_main) as NavHostFragment
        val navController = navHostFragment.navController

        navView.setupWithNavController(navController)

        navView.setOnItemSelectedListener { item ->
            if (isConnectedToInternet() && networkConnectedButIsItAvailable) {
                if (item.itemId != navController.currentDestination?.id) {
                    lastSelectedItemId = item.itemId
                    findNavController(R.id.nav_host_fragment_activity_main).navigate(item.itemId)
                }
                true
            } else {
                navigateToNoInternet()
                true
            }
        }
    }

    override fun onResume() {
        super.onResume()
        registerNetworkCallback()
    }

    override fun onPause() {
        super.onPause()
        unregisterNetworkCallback()
    }

    private fun setupNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                if (isConnectedToInternet()) {
                    runOnUiThread { navigateToLastSelected() }
                } else {
                    runOnUiThread { navigateToNoInternet() }
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                showNetworkStatus("Lost internet connection")
                runOnUiThread { navigateToNoInternet() }
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                super.onCapabilitiesChanged(network, networkCapabilities)
                if (networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)) {
                    if (isConnectedToInternet()) {
                        networkConnectedButIsItAvailable = true
                        runOnUiThread { navigateToLastSelected() }
                    } else {
                        lifecycleScope.launch {
                            delay(2500)
                            showNetworkStatus("No internet access")
                            runOnUiThread { navigateToNoInternet() }
                            networkConnectedButIsItAvailable = false
                        }
                    }
                }
            }
        }
    }

    private fun registerNetworkCallback() {
        setupNetworkCallback()
        connectivityManager = getSystemService(ConnectivityManager::class.java) as ConnectivityManager
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
        connectivityManager.requestNetwork(networkRequest, networkCallback)
    }

    private fun unregisterNetworkCallback() {
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }

    private fun isConnectedToInternet(): Boolean {
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }

    private fun showNetworkStatus(message: String) {
        Timber.e(message)
        Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG)
            .setAnchorView(binding.navView)
            .show()
    }

    private fun navigateToNoInternet() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        lastSelectedItemId = navView.selectedItemId
        if (navController.currentDestination?.id != R.id.noInternetScreenFragment) {
            navController.navigate(R.id.noInternetScreenFragment)
        }
    }

    private fun navigateToLastSelected() {
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val currentDestination = navController.currentDestination?.id
        if (currentDestination == R.id.noInternetScreenFragment && lastSelectedItemId != R.id.noInternetScreenFragment) {
            navController.navigate(lastSelectedItemId)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = navHostFragment.navController
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
