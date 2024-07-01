package com.example.newsapp.presentation.viewModels.impl
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.data.model.Place
import com.example.newsapp.di.RetrofitInstance
import com.example.newsapp.presentation.viewModels.HomeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.fastily.jwiki.core.Wiki.Builder
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.text.DecimalFormat
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.round
import kotlin.math.sin
import kotlin.math.sqrt

@HiltViewModel
class HomeViewModelImpl @Inject constructor() : ViewModel(), HomeViewModel {

    private val _moveToInfoScreen = MutableLiveData<Boolean>()
    override val moveToInfoScreen: LiveData<Boolean> get() = _moveToInfoScreen

    private val _places = MutableLiveData<List<Place>>()
    override val places: LiveData<List<Place>> get() = _places

    private val cache = mutableMapOf<Pair<Double, Double>, List<Place>>()

    override fun fetchPlaces(lat: Double, lon: Double) {
        viewModelScope.launch {
            val coordinates = Pair(lat, lon)
            if (cache.containsKey(coordinates)) {
                _places.postValue(cache[coordinates])
                return@launch
            }

            var attempts = 0
            val maxAttempts = 3
            var success = false

            while (attempts < maxAttempts && !success) {
                try {
                    val response = RetrofitInstance.api.fetchPlaces(
                        action = "query",
                        prop = "coordinates|pageimages|description|info",
                        inprop = "url",
                        pithumbsize = 144,
                        generator = "geosearch",
                        ggsradius = 10000,
                        ggslimit = 10,
                        ggscoord = "$lat|$lon",
                        format = "json"
                    )
                    if (response.isSuccessful) {
                        val placesList = response.body()?.query?.pages?.values?.map { page ->
                            Place(
                                title = page.title,
                                description = page.description,
                                distance = calculateDistance(lat, lon, page.coordinates?.get(0)?.lat ?: 0.0, page.coordinates?.get(0)?.lon ?: 0.0),
                                articleUrl = page.fullurl,
                                thumbnail = page.thumbnail?.source
                            )
                        } ?: emptyList()
                        _places.postValue(placesList)
                        cache[coordinates] = placesList
                        success = true
                    } else {
                        Timber.e("Response not successful: ${response.errorBody()}")
                    }
                } catch (e: Exception) {
                    Timber.e(e, "Error fetching places")
                }
                attempts++
                if (!success) {
                    delay(2000) // Wait for 2 seconds before retrying
                }
            }
        }
    }

    override fun loadData(builder: Builder) {
        viewModelScope.launch {
            // Load data logic here
        }
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Radius of the Earth in kilometers
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) + cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) * sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c
        return round(distance)  // Convert to miles
    }
    fun roundMiles(miles: Double): String {
        val decimalFormat = DecimalFormat("#.##")
        return decimalFormat.format(miles)
    }
    override fun moveToInfoScreen() {
        _moveToInfoScreen.value = true
    }
}
