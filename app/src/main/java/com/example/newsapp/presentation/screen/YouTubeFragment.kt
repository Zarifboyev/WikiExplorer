package com.example.newsapp.presentation.screen

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.newsapp.R
import com.example.newsapp.data.model.ApiKey
import com.example.newsapp.utils.startFragment
import com.google.api.client.extensions.android.http.AndroidHttp
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube

class YouTubeFragment : Fragment(R.layout.fragment_youtube) {

    private lateinit var youtubeDataApi: YouTube
    private val jsonFactory = GsonFactory()
    private val transport: HttpTransport = AndroidHttp.newCompatibleTransport()

    companion object {
        private val YOUTUBE_PLAYLISTS = arrayOf(
            "PLTiLidqsHkJPdIptjgMK9IamUTNvXoZCH",
            "PLTiLidqsHkJMY1CSQiUuweKqly5LmYp_L"
        )

        fun newInstance(): YouTubeFragment {
            return YouTubeFragment()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!isConnected()) {
            Toast.makeText(context, "No Internet Connection Detected", Toast.LENGTH_LONG).show()
            return
        }

        if (ApiKey.YOUTUBE_API_KEY.startsWith("YOUR_API_KEY")) {
            showMissingApiKeyDialog()
        } else if (savedInstanceState == null) {
            youtubeDataApi = YouTube.Builder(transport, jsonFactory, null)
                .setApplicationName(getString(R.string.app_name))
                .build()

            childFragmentManager.beginTransaction()
                .add(R.id.container, YouTubeRecyclerViewFragment.newInstance(youtubeDataApi, YOUTUBE_PLAYLISTS))
                .commit()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.you_tube, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_recyclerview -> {
                startFragment(YouTubeRecyclerViewFragment.newInstance(youtubeDataApi, YOUTUBE_PLAYLISTS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun isConnected(): Boolean {
        val connectivityManager = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun showMissingApiKeyDialog() {
        AlertDialog.Builder(requireContext())
            .setMessage("Edit ApiKey.java and replace \"YOUR_API_KEY\" with your Application's Browser API Key")
            .setTitle("Missing API Key")
            .setNeutralButton("Ok, I got it!") { _, _ -> requireActivity().finish() }
            .create()
            .show()
    }
}
