package com.z99a.wikipedia.vikipediya.app

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.google.android.libraries.places.api.Places
import com.z99a.wikipedia.R
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    // Modern way to provide WorkManager configuration
    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()

        // Set default night mode
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        // Initialize Places API
        if (!Places.isInitialized()) {
            // Using 'this' refers to the application context safely
            Places.initializeWithNewPlacesApiEnabled(this, getString(R.string.google_maps_key))
        }

        // Plant Timber for logging (only in Debug mode is best practice)
        // if (BuildConfig.DEBUG) { Timber.plant(Timber.DebugTree()) }
        Timber.plant(Timber.DebugTree())
    }

    /**
     * Helper to check network connectivity
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            connectivityManager.activeNetworkInfo?.isConnected ?: false
        }
    }
}