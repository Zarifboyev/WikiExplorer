package com.example.newsapp.utils;


import android.app.Activity;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

import java.util.ArrayList;
import java.util.Locale;

public class VoiceInputHandler {
    private static final int VOICE_INPUT_REQUEST_CODE = 1001;
    private Activity activity;
    private SearchBar searchBar;
    private SearchView searchView;

    public VoiceInputHandler(Activity activity, SearchBar searchBar, SearchView searchView) {
        this.activity = activity;
        this.searchBar = searchBar;
        this.searchView = searchView;
    }

    public void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");
        try {
            activity.startActivityForResult(intent, VOICE_INPUT_REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(activity, "Voice input not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    public void handleActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == VOICE_INPUT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                String voiceInput = result.get(0);
                searchBar.setText(voiceInput);
                searchView.setText(voiceInput);
            }
        }
    }
}
