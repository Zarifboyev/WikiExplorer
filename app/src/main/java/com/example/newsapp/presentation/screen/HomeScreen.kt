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
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.screen_home), OnPlaceClickListener {

    private val binding by viewBinding(ScreenHomeBinding::bind)
    private lateinit var placesAdapter: PlacesAdapter

    private lateinit var linearProgressIndicator:LinearProgressIndicator

    private val globalViewModel: GlobalViewModel by activityViewModels()
    private val homeViewModel: HomeViewModelImpl by activityViewModels()


    private lateinit var locationManager: LocationManager
    private lateinit var networkManager: NetworkManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        linearProgressIndicator  = binding.linearProgressIndicator2

        setupRecyclerView()
        observeViewModel()
        observeStateFlows()

        locationManager = LocationManager(requireContext())
        networkManager = NetworkManager


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
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.places.collect { places ->
                Timber.d("Places list updated: $places")
                if (places.isEmpty()) {
                    Snackbar.make(binding.root, getString(R.string.no_places_found), Snackbar.LENGTH_SHORT).show()
                }
                placesAdapter.submitList(places)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.isLoading.collect { isLoading ->
                updateLoadingState(isLoading)
            }
        }
    }
    private fun observeStateFlows() {

        lifecycleScope.launchWhenStarted {
            locationManager.location.collect { location ->
                if (location != null) {
                    fetchPlaces(location)
                }
            }
        }

        // Observe network availability
        // TODO: @LaunchWhenStarted is deprecated, use repeatOnLifecycle instead
        lifecycleScope.launchWhenStarted {
            networkManager.isNetworkAvailable.collect { isAvailable ->
                if (!isAvailable)
                {
                    showNoNetworkUI()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            globalViewModel.location.collect { location ->
                location?.let {
                    fetchPlaces(it)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            globalViewModel.isLocationAvailable.collect { isLocationAvailable ->
                if (isLocationAvailable) {
                    Toast.makeText(requireContext(), "Location is not available", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.error.collect { isError ->
                if(isError.isNullOrBlank())
                {
                    return@collect
                }
                Toast.makeText(requireContext(), "No data", Toast.LENGTH_SHORT).show()
                showLocationSelectionPrompt()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            globalViewModel.languageCode.collect { languageCode ->
                globalViewModel.location.value?.let { location ->
                    globalViewModel.changeLanguage(languageCode)
                    fetchPlaces(location)
                }
            }
        }
    }

    private fun fetchPlaces(location: Location) {
        if (globalViewModel.languageCode.value.isBlank())
        {
            globalViewModel.changeLanguage("uz")
        }
        homeViewModel.fetchPlaces(location.latitude, location.longitude, globalViewModel.languageCode.value ?: "uz")
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
