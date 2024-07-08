package com.example.newsapp

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Lifecycle.State.STARTED
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.presentation.screen.ContainerMain
import com.example.newsapp.presentation.viewModels.GlobalViewModel
import com.example.newsapp.presentation.viewModels.impl.HomeViewModelImpl
import com.example.newsapp.utils.LocationManager
import com.example.newsapp.utils.NetworkManager
import com.example.newsapp.utils.createFragment
import com.google.android.material.color.DynamicColors
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // Use activityViewModels() to share view models across fragments
    private val globalViewModel: GlobalViewModel by viewModels()
    private val homeViewModel: HomeViewModelImpl by viewModels()


    private lateinit var locationManager: LocationManager
    private lateinit var networkManager: NetworkManager
    override fun onCreate(savedInstanceState: Bundle?) {
        DynamicColors.applyToActivitiesIfAvailable(/* application = */ application)
        super.onCreate(savedInstanceState)
        locationManager = LocationManager(this)
        networkManager = NetworkManager


        lifecycleScope.launchWhenStarted {
            locationManager.location.collect { location ->
                // Update UI with location
            }
        }

        // Observe network availability
        // TODO: @LaunchWhenStarted is deprecated, use repeatOnLifecycle instead
        lifecycleScope.launchWhenStarted {
            networkManager.isNetworkAvailable.collect { isAvailable ->
                // Update UI with network status
            }
        }

        // Register network callback
        networkManager.registerNetworkCallback(this)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            createFragment(R.id.fragment_container_view, ContainerMain())
        }
    }

    suspend fun lifecycles()
    {
        // Observe location updates
        lifecycle.repeatOnLifecycle(STARTED){

        }
    }
    override fun onResume() {
        super.onResume()
        networkManager.registerNetworkCallback(this)
        // Update location enabled status
        locationManager.updateLocationEnabled()
    }


}
