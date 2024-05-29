package com.example.newsapp.utils

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import android.widget.SearchView
import android.widget.Toast
import java.util.Locale


class VoiceInputHandler(
    private val activity: Activity,
    private val searchView: SearchView
) {
    fun startVoiceInput() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...")
        try {
            activity.startActivityForResult(intent, VOICE_INPUT_REQUEST_CODE)
        } catch (e: Exception) {
            Toast.makeText(
                activity,
                "Voice input not supported on this device.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == VOICE_INPUT_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (result != null && !result.isEmpty()) {
                val voiceInput = result[0]
                //searchBar.setText(voiceInput)
                //searchView.setText(voiceInput)
            }
        }
    }

    companion object {
        private const val VOICE_INPUT_REQUEST_CODE = 1001
    }
}
