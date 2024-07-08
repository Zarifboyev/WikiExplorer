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
    private val _isExisted = MutableStateFlow<Boolean?>(true)
    override val isExisted: StateFlow<Boolean?> = _isExisted.asStateFlow()

    private val cache = mutableMapOf<Pair<String, Pair<Double, Double>>, List<Place>>()

    override fun fetchPlaces(lat: Double, lon: Double, langCode: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val coordinates = Pair(lat, lon)
            val cacheKey = Pair(langCode, coordinates)

            // Check cache
            val cachedPlaces = getCachedPlaces(cacheKey)
            if (cachedPlaces != null) {
                _places.value = cachedPlaces
                _isLoading.value = false
                return@launch
            }

            // Fetch from network with retry
            val placesList = fetchPlacesWithRetry(lat, lon, langCode)
            if (placesList.isNotEmpty()) {
                _places.value = placesList
                cachePlaces(cacheKey, placesList)
            } else {
                _isExisted.value = false
            }
            _isLoading.value = false
        }
    }

    private fun getCachedPlaces(cacheKey: Pair<String, Pair<Double, Double>>): List<Place>? {
        return cache[cacheKey]
    }

    private fun cachePlaces(cacheKey: Pair<String, Pair<Double, Double>>, placesList: List<Place>) {
        cache[cacheKey] = placesList
    }

    private suspend fun fetchPlacesWithRetry(lat: Double, lon: Double, langCode: String): List<Place> {
        repeat(MAX_ATTEMPTS) { attempt ->
            try {
                val response = withContext(Dispatchers.IO) {
                    RetrofitInstance.getRetrofitInstance(langCode).fetchPlaces(
                        ggscoord = "$lat|$lon"
                    )
                }

                if (response.isSuccessful) {
                    val pages = response.body()?.query?.pages?.values ?: emptyList()
                    return processPagesInParallel(pages, lat, lon, langCode)
                } else {
                    Timber.e("Response not successful: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching places, attempt $attempt")
            }

            delay(RETRY_DELAY_MS)
        }

        return emptyList()
    }

    private suspend fun processPagesInParallel(pages: Collection<Page>, lat: Double, lon: Double, langCode: String): List<Place> =
        coroutineScope {
            pages.chunked(5).map { chunk ->
                async(Dispatchers.Default) {
                    chunk.map { page ->
                        processPage(page, lat, lon, langCode)
                    }
                }
            }.awaitAll().flatten()
        }

    private suspend fun processPage(page: Page, lat: Double, lon: Double, langCode: String): Place =
        withContext(Dispatchers.Default) {
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
