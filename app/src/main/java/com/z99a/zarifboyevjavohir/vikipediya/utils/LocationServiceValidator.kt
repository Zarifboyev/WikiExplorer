package com.z99a.zarifboyevjavohir.vikipediya.utils

import android.content.Context
import android.content.IntentSender
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.tasks.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject

class LocationServiceValidator @Inject constructor(@ApplicationContext val context: Context) {
    private val locationRequest = LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY,
        1000
    ).build()

    private val client: SettingsClient = LocationServices.getSettingsClient(context)
    private val builder: LocationSettingsRequest.Builder = LocationSettingsRequest
        .Builder()
        .addLocationRequest(locationRequest)
    private lateinit var gpsSettingTask: Task<LocationSettingsResponse>

    fun checkLocationServiceEnabled(
        onDisabled: (IntentSenderRequest) -> Unit,
        onEnabled: () -> Unit
    ) {
        gpsSettingTask = client.checkLocationSettings(builder.build())

        gpsSettingTask.addOnSuccessListener {
            Timber.d("addOnSuccessListener")
            onEnabled()
        }
        gpsSettingTask.addOnFailureListener { exception ->
            Timber.d("exception = $exception")
            if (exception is ResolvableApiException) {
                try {
                    val intentSenderRequest = IntentSenderRequest
                        .Builder(exception.resolution)
                        .build()
                    onDisabled(intentSenderRequest)
                } catch (sendEx: IntentSender.SendIntentException) {
                    Timber.d("IntentSender.SendIntentException = ${sendEx.message}")
                } catch (ex : Exception) {
                    Timber.d("Exception = ${ex.message}")
                }
            }
        }
    }
}
