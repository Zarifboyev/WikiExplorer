package com.example.newsapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.presentation.screen.ContainerMain
import com.example.newsapp.presentation.screen.HomeScreen
import com.example.newsapp.presentation.screen.InfoScreen
import com.example.newsapp.presentation.screen.ProfileScreen
import com.example.newsapp.utils.createFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by viewBinding(ActivityMainBinding::bind)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ActivityMainBinding.inflate(layoutInflater)
        createFragment(R.id.fragment_container_view, ContainerMain())


        }


    //        Picasso.get().apply {
//            setIndicatorsEnabled(true)
//            isLoggingEnabled = true
//        }
//
//        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
//        setSupportActionBar(toolbar)
//
//        val navHostFragment =
//            supportFragmentManager.findFragmentById(R.id.fragment_main_container) as? NavHostFragment
//
//        navHostFragment?.let {
//            val navController = it.navController
//            appBarConfiguration = AppBarConfiguration.Builder(navController.graph).build()
//            setupActionBarWithNavController(this, navController, appBarConfiguration)
//        } ?: run {
//            // Handle the case when NavHostFragment is null
//            showNoInternetUI()
//        }
//    }
//
//    private fun showNoInternetUI() {
//        Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show()
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        // Handle activity result if necessary
//        // voiceInputHandler.handleActivityResult(requestCode, resultCode, data)
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.fragment_main_container)
//        return navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        menuInflater.inflate(R.menu.top_app_bar, menu)
//        return true
//    }
    }
