package com.example.newsapp.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.newsapp.R;
import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity2 extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Make sure you set your activity layout

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_main_container);

        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
                appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
                NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        } else {
            // Handle the case when NavHostFragment is null
        }
    }


    private void showNoInternetUI() {
//         mLoadingIndicator.setVisibility(View.GONE);
//         mRecyclerView.setVisibility(View.GONE);
//         mNoInternet.setVisibility(View.VISIBLE);
//         mNoInternet.setImageResource(R.drawable.no_internet);
//         mEmptyStateTextView.setVisibility(View.VISIBLE);
//        mEmptyStateTextView.setText(R.string.no_internet_connection);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //voiceInputHandler.handleActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.fragment_main_container);
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_app_bar, menu);
        return true;
    }
}
