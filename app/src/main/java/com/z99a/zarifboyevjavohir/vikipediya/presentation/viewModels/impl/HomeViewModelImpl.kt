package com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.z99a.zarifboyevjavohir.vikipediya.data.model.Place
import com.z99a.zarifboyevjavohir.vikipediya.di.NetworkModule
import com.z99a.zarifboyevjavohir.vikipediya.domain.service.ApiService
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.HomeViewModel
import com.z99a.zarifboyevjavohir.vikipediya.utils.WikiUtility
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.Retrofit
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class HomeViewModelImpl @Inject constructor(
    @NetworkModule.RussianRetrofit private val russianRetrofit: Retrofit,
    @NetworkModule.EnglishRetrofit private val englishRetrofit: Retrofit,
    @NetworkModule.UzbekRetrofit private val uzbekRetrofit: Retrofit,
    private val wikiUtility: WikiUtility
) : ViewModel(), HomeViewModel {

    private val _places = MutableStateFlow<List<Place>>(emptyList())
    override val places: StateFlow<List<Place>> = _places.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    override val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isExisted = MutableStateFlow<Boolean?>(true)
    override val isExisted: StateFlow<Boolean?> = _isExisted.asStateFlow()

    override fun fetchPlaces(lat: Double, lon: Double, langCode: String) {
        viewModelScope.launch {
            Timber.d("Fetching places for coordinates: ($lat, $lon) and langCode: $langCode")
            _isLoading.value = true
            try {
                val placesList = fetchPlacesWithRetry(lat, lon, langCode)
                _places.value = placesList
                _isExisted.value = placesList.isNotEmpty()
            } catch (e: Exception) {
                Timber.e("Error fetching places: ${e.message}")
                _places.value = listOf()
                _isExisted.value = false
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchPlacesWithRetry(
        lat: Double, lon: Double, langCode: String
    ): List<Place> {
        return withContext(Dispatchers.IO) {
            val apiService = when (langCode) {
                "uz" -> uzbekRetrofit.create(ApiService::class.java)
                "ru" -> russianRetrofit.create(ApiService::class.java)
                else -> englishRetrofit.create(ApiService::class.java)
            }

            val response = apiService.fetchPlaces(ggscoord = "$lat|$lon")
            if (response.isSuccessful) {
                val pages = response.body()?.query?.pages?.values ?: emptyList()
                processPagesInParallel(pages, lat, lon, langCode, wikiUtility)
            } else {
                Timber.e("Response not successful: Code ${response.code()}, Message: ${response.message()}, Error body: ${response.errorBody()?.string()}")
                emptyList()
            }
        }
    }

    private suspend fun processPagesInParallel(
        pages: Collection<Place>, lat: Double, lon: Double, langCode: String, wikiUtility: WikiUtility
    ): List<Place> = coroutineScope {
        pages.map { page ->
            async { processPage(page, lat, lon, langCode, wikiUtility) }
        }.awaitAll()
    }
}

private suspend fun processPage(
    page: Place, lat: Double, lon: Double, langCode: String, wikiUtility: WikiUtility
): Place = withContext(Dispatchers.Default) {
    val distance = page.coordinates?.firstOrNull()?.let {
        calculateDistance(lat, lon, it.lat, it.lon)
    } ?: 0.0
    val isExisted: Boolean = if (langCode == "uz")
    {
        true
    } else{
        wikiUtility.isArticleInBothWikis(langCode, page.title)
    }

    page.copy(
        fullUrl = "https://$langCode.m.wikipedia.org/wiki/${page.title.replace(" ", "_")}",
        distance = roundDistance(distance),
        isExisted = isExisted
    )
}

private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val earthRadius = 6371.0 // kilometers
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return earthRadius * c
}

private fun roundDistance(distance: Double): String {
    val df = DecimalFormat("#.#")
    return df.format(distance)
}

@Suppress("UNCHECKED_CAST")
class HomeViewModelFactory @Inject constructor(
    @NetworkModule.RussianRetrofit private val russianRetrofit: Retrofit,
    @NetworkModule.EnglishRetrofit private val englishRetrofit: Retrofit,
    @NetworkModule.UzbekRetrofit private val uzbekRetrofit: Retrofit,
    private val wikiUtility: WikiUtility
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModelImpl::class.java)) {
            return HomeViewModelImpl(
                russianRetrofit,
                englishRetrofit,
                uzbekRetrofit,
                wikiUtility
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
