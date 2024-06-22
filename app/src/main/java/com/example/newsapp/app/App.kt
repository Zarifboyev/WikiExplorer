package com.example.newsapp.app
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.HiltAndroidApp
import io.github.fastily.jwiki.core.Wiki
import timber.log.Timber

@HiltAndroidApp
class App : Application() {
    // ...
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        setupNotification()
    }
    private fun setupNotification() {
        // MainActivity.kt or in Application class
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Goal Channel"
            val descriptionText = "Channel for goal reminders"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel("goal_channel", name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

}