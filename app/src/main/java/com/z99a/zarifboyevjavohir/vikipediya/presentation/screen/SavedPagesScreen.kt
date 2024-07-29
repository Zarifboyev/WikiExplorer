package com.z99a.zarifboyevjavohir.vikipediya.presentation.screen

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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.z99a.zarifboyevjavohir.databinding.ScreenWikiTasksBinding
import com.z99a.zarifboyevjavohir.vikipediya.data.SharedPreferencesManager
import com.z99a.zarifboyevjavohir.vikipediya.data.model.Place
import com.z99a.zarifboyevjavohir.vikipediya.presentation.adapters.PlacesAdapter
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.GlobalViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SavedPagesScreen : Fragment(), PlacesAdapter.OnPlaceClickListener {

    private lateinit var binding: ScreenWikiTasksBinding
    private lateinit var savedPlacesAdapter: PlacesAdapter

    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val globalViewModel:GlobalViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenWikiTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        updateFavoritePlacesList()
    }

    private fun setupRecyclerView() {
        savedPlacesAdapter = PlacesAdapter(requireContext(), this, sharedPreferencesManager)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = savedPlacesAdapter
            setHasFixedSize(true)
        }
    }

    private fun updateFavoritePlacesList() {
        val favoritePlaces = sharedPreferencesManager.getFavoritePlaces()
        savedPlacesAdapter.submitList(favoritePlaces)
    }

    override fun onPlaceClick(place: Place) {
        place.fullUrl?.let {
            openUrlInCustomTab(requireContext(), it)
        } ?: run {
            // Log or handle the null case
        }
    }



    override fun onDistanceClick(place: Place) {
        openLocationInMaps(place.title)
    }

    override fun onLocationIconClick(place: Place) {
        openLocationInMaps(place.title)
    }



    override fun onFavoriteClick(position: Int) {
        val place = savedPlacesAdapter.currentList[position]
        place.isFavorite = !place.isFavorite
        savedPlacesAdapter.notifyItemChanged(position)
        lifecycleScope.launch {
            toggleFavoriteClick(place)
        }
    }

    private suspend fun toggleFavoriteClick(place: Place) {
        try {
            if (place.isFavorite) {
                sharedPreferencesManager.addPlace(place)
            } else {
                sharedPreferencesManager.removePlace(place.title)
            }
            updateFavoritePlacesList()
        } catch (e: Exception) {
            // Handle exception (e.g., log error)
        }
    }

    private fun openLocationInMaps(placeName: String?) {
        placeName?.let {
            val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(it))
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
                setPackage("com.google.android.apps.maps")
            }
            startActivity(mapIntent)
        } ?: run {
            // Log or handle the null case
        }
    }

    private fun openUrlInCustomTab(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}
