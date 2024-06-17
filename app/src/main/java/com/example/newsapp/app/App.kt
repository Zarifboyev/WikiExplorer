package com.example.newsapp.app
import android.app.Application
import android.graphics.drawable.Drawable
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
    }

}