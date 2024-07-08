package com.example.newsapp.presentation.viewModels.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.Page
import com.example.newsapp.data.model.Place
import com.example.newsapp.di.RetrofitInstance
import com.example.newsapp.presentation.viewModels.HomeViewModel
import com.example.newsapp.utils.WikiUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import java.math.BigDecimal
import java.math.RoundingMode
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.*

@HiltViewModel
class HomeViewModelImpl @Inject constructor() : ViewModel(), HomeViewModel {
    companion object {
        private const val MAX_ATTEMPTS = 3
        private const val RETRY_DELAY_MS = 2000L
        private const val EARTH_RADIUS_KM = 6371.0
    }

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    override val places: StateFlow<List<Place>> = _places.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _error = MutableStateFlow<String?>(null)
    override val error: StateFlow<String?> = _error.asStateFlow()

    private val cache = mutableMapOf<Pair<String, Pair<Double, Double>>, List<Place>>()

    override fun fetchPlaces(lat: Double, lon: Double, langCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val coordinates = Pair(lat, lon)
            val cacheKey = Pair(langCode, coordinates)

            // Check if data is cached first
            cache[cacheKey]?.let { cachedPlaces ->
                _places.value = cachedPlaces
                _isLoading.value = false
                return@launch
            }

            // Fetch data from network with retries
            val placesList = fetchPlacesWithRetry(lat, lon, langCode)
            if (placesList.isNotEmpty()) {
                _places.value = placesList
                cache[cacheKey] = placesList // Cache the result with langCode and coordinates
            } else {
                _error.value = "Failed to fetch places after $MAX_ATTEMPTS attempts"
            }
            _isLoading.value = false
        }
    }

    private suspend fun fetchPlacesWithRetry(lat: Double, lon: Double, langCode: String): List<Place> {

        val response = withContext(Dispatchers.IO) {
            RetrofitInstance.getRetrofitInstance(langCode).fetchPlaces(
                action = "query",
                prop = "coordinates|pageimages|description|info",
                inprop = "url",
                pithumbsize = 144,
                generator = "geosearch",
                ggsradius = 10000,
                ggslimit = 20,
                ggscoord = "$lat|$lon",
                format = "json"
            )
        }

        if (response.isSuccessful) {
            val pages = response.body()?.query?.pages?.values ?: emptyList()
            return pages.map { page ->
                processPage(page, lat, lon, langCode)
            }
        } else {
            Timber.e("Response not successful: ${response.errorBody()}")
        }

        return emptyList()
    }



    private suspend fun processPage(page: Page, lat: Double, lon: Double, langCode: String): Place
            = withContext(Dispatchers.Default) {
        val isExisted = suspendCoroutine { continuation ->
            WikiUtility.isArticleInBothWikis(langCode, page.title) { exists ->
                continuation.resume(exists)
            }
        }

        val distance = page.coordinates?.firstOrNull()?.let {
            calculateDistance(lat, lon, it.lat, it.lon)
        } ?: 0.0

        Place(
            title = page.title,
            description = page.description,
            distance = distance,
            articleUrl = page.fullurl,
            thumbnail = page.thumbnail?.source,
            isArticleExistedInUzWiki = isExisted
        )
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = EARTH_RADIUS_KM * c
        return BigDecimal(distance).setScale(2, RoundingMode.HALF_UP).toDouble()
    }
}