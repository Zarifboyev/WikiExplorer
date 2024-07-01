package com.example.newsapp.app
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
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
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}