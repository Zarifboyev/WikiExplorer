/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.z99a.zarifboyevjavohir.vikipediya.presentation.screen

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.ScreenYoutubePlaceholderBinding
import com.z99a.zarifboyevjavohir.vikipediya.CONST
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.YouTubePlaylistItem
import com.z99a.zarifboyevjavohir.vikipediya.presentation.adapters.VideoAdapter
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.YouTubeViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import timber.log.Timber

@AndroidEntryPoint
class YouTubePlaylistsScreen : Fragment(R.layout.screen_youtube_placeholder) {

    private val viewModel: YouTubeViewModel by activityViewModels()
    private var _binding: ScreenYoutubePlaceholderBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding is null")
    private val YOUTUBE_API_KEY = CONST.YOUTUBE_API_KEY
    private val YOUTUBE_CHANNEL_ID = CONST.YOUTUBE_CHANNEL_ID
    private lateinit var videoAdapter: VideoAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private val ICON_STATE_KEY = "icon_state_key"

    override fun onAttach(context: Context) {
        super.onAttach(context)
        sharedPreferences = context.getSharedPreferences("youtube_preferences", Context.MODE_PRIVATE)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.tag("YouTubePlaylistsScreen").d("onViewCreated: View is created.")
        _binding = ScreenYoutubePlaceholderBinding.bind(view)

        setupRecyclerView()
        setupObservers()
        restoreIconState()
        setupTopAppBar()
        fetchYouTubeData()

    }

    private fun setupTopAppBar() {
        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_notify_about_changes -> {
                    toggleNotificationIcon(it)
                    true
                }
                else -> false
            }
        }
    }

    private fun toggleNotificationIcon(menuItem: MenuItem) {
        val isIconFilled = sharedPreferences.getBoolean(ICON_STATE_KEY, false)
        val newIcon = if (isIconFilled) {
            R.drawable.ic_notify_outline
        } else {
            R.drawable.ic_notify_icon_filled
        }
        menuItem.setIcon(newIcon)
        sharedPreferences.edit().putBoolean(ICON_STATE_KEY, !isIconFilled).apply()

        if (!isIconFilled) {
            Snackbar.make(requireView(), "You will be notified about the new video uploads!", Snackbar.LENGTH_SHORT)
                .setAction("Undo") {
                    binding.topAppBar.menu.findItem(R.id.action_notify_about_changes).setIcon(R.drawable.ic_notify_outline)
                    sharedPreferences.edit().putBoolean(ICON_STATE_KEY, false).apply()
                }.show()
        }
    }

    private fun restoreIconState() {
        val isIconFilled = sharedPreferences.getBoolean(ICON_STATE_KEY, false)
        val icon = if (isIconFilled) {
            R.drawable.ic_notify_icon_filled
        } else {
            R.drawable.ic_notify_outline
        }
        binding.topAppBar.menu.findItem(R.id.action_notify_about_changes).setIcon(icon)
    }

    private fun setupRecyclerView() {
        Timber.tag("YouTubePlaylistsScreen").d("Setting up RecyclerView.")
        binding.linearProgressIndicator.show()
        videoAdapter = VideoAdapter(
            onVideoClick = { videoId ->
                Timber.tag("YouTubePlaylistsScreen").d("Video selected with ID: $videoId")
                openYouTubeVideo(videoId)
            },
            onChannelClick = { channelId ->
                openYouTubeChannel(channelId)
            }
        )

        binding.youtubeRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = videoAdapter
        }
        Timber.tag("YouTubePlaylistsScreen").d("RecyclerView setup complete.")
    }

    private fun fetchYouTubeData() {
        Timber.tag("YouTubePlaylistsScreen").d("Fetching YouTube data with API key: $YOUTUBE_API_KEY and Channel ID: $YOUTUBE_CHANNEL_ID")
        binding.linearProgressIndicator.show()
        viewLifecycleOwner.lifecycleScope.launch {
            val result = withTimeoutOrNull(10000L) {
                try {
                    viewModel.fetchPlaylists(YOUTUBE_API_KEY, YOUTUBE_CHANNEL_ID)
                    Timber.tag("YouTubePlaylistsScreen").d("YouTube data fetch initiated.")
                } catch (e: Exception) {
                    Timber.tag("YouTubePlaylistsScreen").e(e, "Failed to fetch YouTube data: ${e.localizedMessage}")
                    showSlowOrInsufficientNetworkConnection()
                }
            }
            if (result == null) {
                Timber.tag("YouTubePlaylistsScreen").e("YouTube data fetch timed out.")
                showSlowOrInsufficientNetworkConnection()
            }
        }
    }

    private fun showSlowOrInsufficientNetworkConnection() {
        Timber.tag("YouTubePlaylistsScreen").d("Showing slow or insufficient network connection message.")
        _binding?.linearProgressIndicator?.hide()
        Toast.makeText(requireContext(), getString(R.string.slow_or_insufficient_network), Toast.LENGTH_LONG).show()
    }

    private fun setupObservers() {
        Timber.tag("YouTubePlaylistsScreen").d("Setting up observers for YouTube playlists and video statistics.")

        viewModel.playlists.observe(viewLifecycleOwner) { playlists ->
            val reversedPlaylists = playlists.reversed()
            if (reversedPlaylists.isNotEmpty()) {
                Timber.tag("YouTubePlaylistsScreen").d("Received ${reversedPlaylists.size} playlists.")
                setupPlaylistSpinner(reversedPlaylists)
            } else {
                Timber.tag("YouTubePlaylistsScreen").d("No playlists received.")
            }
        }

        viewModel.playlistItems.observe(viewLifecycleOwner) { playlistItems ->
            playlistItems?.let {
                Timber.tag("YouTubePlaylistsScreen").d("Playlist items updated: ${it.size} items.")
                videoAdapter.submitList(it)
                _binding?.linearProgressIndicator?.hide()
            }
        }

        viewModel.videoStatistics.observe(viewLifecycleOwner) { videoStatistics ->
            videoStatistics?.let {
                Timber.tag("YouTubePlaylistsScreen").d("Video statistics updated.")
                videoAdapter.updateVideoStatistics(it)
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                Timber.tag("YouTubePlaylistsScreen").e("Error observed: $it")
                _binding?.linearProgressIndicator?.hide()
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupPlaylistSpinner(playlists: List<YouTubePlaylistItem>) {
        val titles = playlists.map { it.snippet.title }
        val autoCompleteTextView = (_binding?.playlistSpinnerLayout?.editText as? MaterialAutoCompleteTextView)
        val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, titles)
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        autoCompleteTextView?.setAdapter(adapter)

        autoCompleteTextView?.setOnItemClickListener { _, _, position, _ ->
            val selectedPlaylist = playlists[position]
            Timber.tag("YouTubePlaylistsScreen").d("Spinner selected: ${selectedPlaylist.snippet.title} with ID: ${selectedPlaylist.id}")
            fetchPlaylistItems(selectedPlaylist.id)
        }

        // Automatically select the first playlist after 100 ms
        Handler(Looper.getMainLooper()).postDelayed({
            autoCompleteTextView?.takeIf { (it.adapter?.count ?: 0) > 0 }?.apply {
                val firstPlaylist = playlists.first()
                setText(firstPlaylist.snippet.title, false)
                fetchPlaylistItems(firstPlaylist.id)
            }
        }, 100)
    }

    private fun fetchPlaylistItems(playlistId: String) {
        _binding?.let {
            Timber.tag("YouTubePlaylistsScreen").d("Fetching playlist items for playlist ID: $playlistId.")
            binding.linearProgressIndicator.show()
            viewLifecycleOwner.lifecycleScope.launch {
                val result = withTimeoutOrNull(10000L) {
                    try {
                        viewModel.fetchPlaylistItems(YOUTUBE_API_KEY, playlistId)
                        Timber.tag("YouTubePlaylistsScreen").d("Playlist items fetch initiated for playlist ID: $playlistId.")
                    } catch (e: Exception) {
                        Timber.tag("YouTubePlaylistsScreen").e(e, "Failed to fetch playlist items: ${e.localizedMessage}")
                        showSlowOrInsufficientNetworkConnection()
                    }
                }
                if (result == null) {
                    Timber.tag("YouTubePlaylistsScreen").e("Playlist items fetch timed out.")
                    showSlowOrInsufficientNetworkConnection()
                }
            }
        } ?: run {
            Timber.tag("YouTubePlaylistsScreen").e("Binding is null, cannot fetch playlist items")
        }
    }

    private fun openYouTubeVideo(videoId: String) {
        Timber.tag("YouTubePlaylistsScreen").d("Opening YouTube video with ID: $videoId")
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:$videoId")).apply {
            putExtra("force_fullscreen", true)
        }
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Timber.tag("YouTubePlaylistsScreen").e(e, "Failed to open YouTube video: ${e.localizedMessage}")
            Toast.makeText(requireContext(), getString(R.string.failed_to_open_video), Toast.LENGTH_SHORT).show()
        }
    }

    private fun openYouTubeChannel(channelId: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/$channelId"))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            Timber.tag("YouTubePlaylistsScreen").e(e, "Failed to open YouTube channel: ${e.localizedMessage}")
            Toast.makeText(requireContext(), getString(R.string.failed_to_open_channel), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.tag("YouTubePlaylistsScreen").d("onDestroyView: View is being destroyed.")
        binding.youtubeRecyclerView.adapter = null
        _binding = null
    }
}
