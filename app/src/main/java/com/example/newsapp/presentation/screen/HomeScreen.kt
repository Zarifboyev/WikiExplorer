package com.example.newsapp.presentation.screen

import android.content.Context
import android.location.Location
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.data.model.Place
import com.example.newsapp.databinding.ScreenHomeBinding
import com.example.newsapp.presentation.adapters.OnPlaceClickListener
import com.example.newsapp.presentation.adapters.PlacesAdapter
import com.example.newsapp.presentation.viewModels.GlobalViewModel
import com.example.newsapp.presentation.viewModels.impl.HomeViewModelImpl
import com.example.newsapp.utils.LocationManager
import com.example.newsapp.utils.NetworkManager
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.screen_home), OnPlaceClickListener {

    private val binding by viewBinding(ScreenHomeBinding::bind)
    private lateinit var placesAdapter: PlacesAdapter

    private lateinit var linearProgressIndicator: LinearProgressIndicator

    private val globalViewModel: GlobalViewModel by activityViewModels()
    private val homeViewModel: HomeViewModelImpl by activityViewModels()

    @Inject
    lateinit var locationManager: LocationManager

    @Inject
    lateinit var networkManager: NetworkManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearProgressIndicator = binding.linearProgressIndicator2

        setupRecyclerView()
        observeViewModel()
        observeStateFlows()

        // Fetch data using the latest location if available
        globalViewModel.location.value?.let {
            fetchPlaces(it)
        } ?: showLocationSelectionPrompt()

        binding.btnSearch.setOnClickListener {
            Timber.d("Search button clicked")
            val mapDialogFragment = MapDialogFragment()
            if (childFragmentManager.findFragmentByTag("mapDialog") == null) {
                mapDialogFragment.show(childFragmentManager, "mapDialog")
            } else {
                Timber.d("MapDialogFragment is already shown")
            }
            binding.animationView.visibility = View.INVISIBLE
        }

        binding.swipeRefreshLayout.setOnRefreshListener {
            globalViewModel.location.value?.let { location ->
                fetchPlaces(location)
            }
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }

    private fun setupRecyclerView() {
        placesAdapter = PlacesAdapter(requireContext(), this)
        binding.placesList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = placesAdapter
            setHasFixedSize(true)
        }
    }

    private fun observeViewModel() {
        homeViewModel.places
            .onEach { places ->
                Timber.d("Places list updated: $places")
                if (places.isEmpty()) {
                    Snackbar.make(binding.root, getString(R.string.no_places_found), Snackbar.LENGTH_SHORT).show()
                }
                placesAdapter.submitList(places)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        homeViewModel.isLoading
            .onEach { isLoading ->
                updateLoadingState(isLoading)
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeStateFlows() {
        observeLocationUpdates()
        observeNetworkAvailability()
        observeGlobalViewModel()
    }

    private fun observeLocationUpdates() {
        locationManager.location
            .onEach { location ->
                location?.let {
                    fetchPlaces(it)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeNetworkAvailability() {
        networkManager.isNetworkAvailable
            .onEach { isAvailable ->
                if (!isAvailable) {
                    showNoNetworkUI()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun observeGlobalViewModel() {
        globalViewModel.location
            .onEach { location ->
                location?.let {
                    fetchPlaces(it)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        globalViewModel.isLocationAvailable
            .onEach { isLocationAvailable ->
                if (!isLocationAvailable) {
                    Toast.makeText(requireContext(), "Location is not available", Toast.LENGTH_SHORT).show()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        homeViewModel.isExisted
            .onEach { isExisted ->
                if (isExisted == false) {
                    Toast.makeText(requireContext(), "No data", Toast.LENGTH_SHORT).show()
                    showLocationSelectionPrompt()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)

        globalViewModel.languageCode
            .onEach { languageCode ->
                globalViewModel.location.value?.let { location ->
                    globalViewModel.changeLanguage(languageCode)
                    fetchPlaces(location)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun fetchPlaces(location: Location) {
        if (globalViewModel.languageCode.value.isBlank()) {
            globalViewModel.changeLanguage("uz")
        }

        // Fetch places on a background thread
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                homeViewModel.fetchPlaces(location.latitude, location.longitude, globalViewModel.languageCode.value)
            }
        }
    }

    private fun showLocationSelectionPrompt() {
        binding.mapContainer.visibility = View.VISIBLE
        Snackbar.make(binding.root, getString(R.string.select_location), Snackbar.LENGTH_LONG).show()
        binding.animationView.playAnimation()
        binding.animationView.visibility = View.VISIBLE
    }

    private fun updateLoadingState(isLoading: Boolean) {
        linearProgressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showNoNetworkUI() {
        Timber.d("No network connection")
        Snackbar.make(binding.root, getString(R.string.no_internet_connection), Snackbar.LENGTH_LONG).show()
    }

    override fun onPlaceClick(place: Place?) {
        place?.let {
            Timber.d("Place clicked: ${it.title}")
            openUrlInCustomTab(requireContext(), it.articleUrl)
        }
    }

    override fun onDistanceClick(place: Place) {
        // Implement distance click functionality
    }

    override fun onLocationIconClick(place: Place) {
        // Implement location icon click functionality
    }

    override fun onFavoriteClick(position: Int) {
        // Implement favorite click functionality
    }

    private fun openUrlInCustomTab(context: Context, url: String) {
        val builder = CustomTabsIntent.Builder()
        val customTabsIntent = builder.build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}
