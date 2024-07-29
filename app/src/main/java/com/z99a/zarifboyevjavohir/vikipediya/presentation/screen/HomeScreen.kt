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
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.MenuHost
import androidx.core.view.MenuHostHelper
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.ScreenHomeBinding
import com.z99a.zarifboyevjavohir.vikipediya.CONST
import com.z99a.zarifboyevjavohir.vikipediya.data.SharedPreferencesManager
import com.z99a.zarifboyevjavohir.vikipediya.data.model.Place
import com.z99a.zarifboyevjavohir.vikipediya.presentation.adapters.PlacesAdapter
import com.z99a.zarifboyevjavohir.vikipediya.presentation.ui.components.widgets.LanguageSelectionBottomSheet
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.GlobalViewModel
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.impl.HomeViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.screen_home), PlacesAdapter.OnPlaceClickListener {

    private val binding by viewBinding(ScreenHomeBinding::bind)
    private lateinit var placesAdapter: PlacesAdapter
    private lateinit var linearProgressIndicator: LinearProgressIndicator

    private val globalViewModel: GlobalViewModel by activityViewModels()
    private val homeViewModel: HomeViewModelImpl by activityViewModels()

    private var isFirstLoading = false
    @Inject
    lateinit var sharedPreferencesManager: SharedPreferencesManager

    private val defaultLocation = Location("default").apply {
        latitude = 41.311081 // Default latitude for Tashkent, Uzbekistan
        longitude = 69.240562 // Default longitude for Tashkent, Uzbekistan
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated: HomeScreen view created")
        linearProgressIndicator = binding.linearProgressIndicator2

        setupRecyclerView()
        observeViewModel()
        retrieveAndDisplaySavedData()

        binding.btnSearch.setOnClickListener {
            Timber.d("Search button clicked")
            showMapDialogFragment()
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            Timber.d("Swipe to refresh triggered")
            refreshPlaces()
        }

        binding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_select_languages -> {
                    showLanguageSelectionDialog()
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun showLanguageSelectionDialog() {
        val dialog = LanguageSelectionBottomSheet()
        dialog.show(parentFragmentManager, "LanguageSelectionBottomSheet")
    }

    override fun onResume() {
        super.onResume()
        Timber.d("onResume: HomeScreen resumed")
        val languageCode = globalViewModel.languageCode.value
        if (languageCode.isNotBlank()) {
            updateTitleTextView(languageCode)
        }
    }

    private fun updateTitleTextView(languageCode: String) {
        try {
            Timber.d("Updating title text view with language code: $languageCode")
            val titleResId = when (languageCode) {
                "ru" -> R.string.russian_wikipedia
                "uz" -> R.string.uzbek_wikipedia
                "en" -> R.string.english_wikipedia
                else -> R.string.russian_wikipedia
            }
            binding.titleTextView.setText(titleResId)
        } catch (e: Exception) {
            Timber.e(e, "Error updating title text view")
        }
    }

    private fun setupRecyclerView() {
        Timber.d("Setting up RecyclerView")
        placesAdapter = PlacesAdapter(requireActivity(), this, sharedPreferencesManager)
        binding.placesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placesAdapter
        }
    }

    private fun observeViewModel() {
        Timber.d("Observing ViewModel")
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.places.collect { places ->
                Timber.d("Places list updated: $places")
                if (places.isNotEmpty())
                {
                    binding.placesList.visibility = View.VISIBLE
                    binding.noPlacesFoundLayout.root.visibility = View.INVISIBLE
                    placesAdapter.submitList(places)
                    placesAdapter.notifyDataSetChanged()
                    sharedPreferencesManager.savePlaces(places)
                    isFirstLoading = true
                } else if(isFirstLoading){
                    binding.placesList.visibility = View.GONE
                    binding.noPlacesFoundLayout.root.visibility = View.VISIBLE
                }

            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            globalViewModel.languageCode.collect { languageCode ->
                Timber.d("Language code updated: $languageCode")
                placesAdapter.notifyDataSetChanged()
                updateTitleTextView(languageCode)
                refreshPlaces()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.isLoading.collect { isLoading ->
                Timber.d("Loading state updated: $isLoading")
                updateLoadingState(isLoading)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            globalViewModel.location.collect { location ->
                location?.let {
                    Timber.d("Fetching places based on the location => ${location}")
                    fetchPlaces(it)
                }
            }
        }
    }

    private fun retrieveAndDisplaySavedData() {
        Timber.d("Retrieving and displaying saved data")
        val savedPlaces = sharedPreferencesManager.getPlaces()

        if (savedPlaces.isNotEmpty()) {
            Timber.d("Saved places found")
            placesAdapter.submitList(savedPlaces)
        } else {
            Timber.d("No saved places found, fetching default location places")
            fetchPlaces(defaultLocation)
        }
    }

    private fun fetchPlaces(location: Location) {
        val languageCode = globalViewModel.languageCode.value.ifBlank { "uz" }
        Timber.d("Fetching places for location: $location and language: $languageCode")
        homeViewModel.fetchPlaces(location.latitude, location.longitude, languageCode)
    }

    private fun showMapDialogFragment() {
        if (childFragmentManager.findFragmentByTag(MAP_DIALOG_TAG) == null) {
            Timber.d("Showing map dialog fragment")
            MapDialogFragment().show(childFragmentManager, MAP_DIALOG_TAG)
        }
    }

    private fun refreshPlaces() {
        Timber.d("Refreshing places")
        globalViewModel.location.value?.let {
            fetchPlaces(Location("default").apply {
                latitude = it.latitude
                longitude = it.longitude
            })
        } ?: fetchPlaces(defaultLocation)
        binding.swipeRefreshLayout.isRefreshing = false
    }

    private fun updateLoadingState(isLoading: Boolean) {
        Timber.d("Updating loading state: $isLoading")
        linearProgressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onPlaceClick(place: Place) {
        Timber.d("Place clicked: ${place.title}")
        openUrlInCustomTab(requireContext(), place.fullUrl ?: CONST.DOMAIN)
    }

    override fun onDistanceClick(place: Place) {
        Timber.d("Distance clicked for place: ${place.title}")
        openLocationInMaps(place.title)
    }

    override fun onLocationIconClick(place: Place) {
        Timber.d("Location icon clicked for place: ${place.title}")
        openLocationInMaps(place.title)
    }

    override fun onFavoriteClick(position: Int) {
        val place = placesAdapter.currentList[position]
        place.isFavorite = !place.isFavorite
        Timber.d("Favorite clicked for place: ${place.title}, isFavorite: ${place.isFavorite}")
        placesAdapter.notifyItemChanged(position)
        toggleFavoritePlace(place)
    }

    private fun openUrlInCustomTab(context: Context, url: String) {
        Timber.d("Opening URL in custom tab: $url")
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }

    private fun openLocationInMaps(placeName: String?) {
        Timber.d("Opening location in maps for place: $placeName")
        val gmmIntentUri = Uri.parse("geo:0,0?q=${Uri.encode(placeName)}")
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(mapIntent)
    }

    private fun toggleFavoritePlace(place: Place) {
        Timber.d("Toggling favorite for place: ${place.title}, isFavorite: ${place.isFavorite}")
        if (place.isFavorite) {
            sharedPreferencesManager.addPlace(place)
        } else {
            sharedPreferencesManager.removePlace(place.title)
        }
    }

    private fun showToast(messageResId: Int) {
        Timber.d("Showing toast message: ${getString(messageResId)}")
        Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Timber.d("Fragment view destroyed")
        viewLifecycleOwner.lifecycleScope.coroutineContext.cancelChildren()
    }

    companion object {
        private const val MAP_DIALOG_TAG = "mapDialog"
    }
}
