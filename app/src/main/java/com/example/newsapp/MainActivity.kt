package com.example.newsapp

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.presentation.screen.ContainerMain
import com.example.newsapp.utils.createFragment
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.JsonObject
import dagger.hilt.android.AndroidEntryPoint
import io.github.fastily.jwiki.core.Wiki

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityMainBinding.inflate(layoutInflater)
        createFragment(R.id.fragment_container_view, ContainerMain())
    }
}
