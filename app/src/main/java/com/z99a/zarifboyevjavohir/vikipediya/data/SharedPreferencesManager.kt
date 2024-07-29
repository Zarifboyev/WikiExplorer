package com.z99a.zarifboyevjavohir.vikipediya.data

import android.content.Context
import android.content.SharedPreferences
import android.location.Location
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.z99a.zarifboyevjavohir.vikipediya.data.model.Place
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPreferencesManager @Inject constructor(
    @ApplicationContext context: Context,
    private val gson: Gson
) {

    companion object {
        private const val PREFS_NAME = "vikipeziya_prefs"
        private const val KEY_LAST_LOCATION = "key_last_location"
        private const val KEY_PLACES = "key_places"
        private const val KEY_FAVORITE_PLACES = "key_favorite_places"
    }

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveLastLocation(location: Location) {
        synchronized(this) {
            sharedPreferences.edit()
                .putString(KEY_LAST_LOCATION, gson.toJson(location))
                .apply()
        }
    }

    fun getLastLocation(): Location? {
        val locationJson = sharedPreferences.getString(KEY_LAST_LOCATION, null)
        return locationJson?.let {
            gson.fromJson(it, Location::class.java)
        }
    }

    fun savePlaces(places: List<Place>) {
        synchronized(this) {
            sharedPreferences.edit()
                .putString(KEY_PLACES, gson.toJson(places))
                .apply()
        }
    }

    fun getPlaces(): List<Place> {
        val placesJson = sharedPreferences.getString(KEY_PLACES, null)
        return placesJson?.let {
            val type = object : TypeToken<List<Place>>() {}.type
            gson.fromJson(it, type)
        } ?: emptyList()
    }

    private fun fetchFavoritePlaces(): MutableList<Place> {
        val json = sharedPreferences.getString(KEY_FAVORITE_PLACES, "[]")
        val type = object : TypeToken<MutableList<Place>>() {}.type
        return gson.fromJson(json, type)
    }

    fun addPlace(place: Place) {
        modifyFavoritePlaces { favoritePlaces ->
            if (favoritePlaces.none { it.title == place.title }) {
                favoritePlaces.add(place)
            }
        }
    }

    fun removePlace(placeTitle: String) {
        modifyFavoritePlaces { favoritePlaces ->
            favoritePlaces.removeAll { it.title == placeTitle }
        }
    }

    fun getFavoritePlaces(): List<Place> {
        return fetchFavoritePlaces()
    }

    private fun modifyFavoritePlaces(modification: (MutableList<Place>) -> Unit) {
        synchronized(this) {
            val favoritePlaces = fetchFavoritePlaces()
            modification(favoritePlaces)
            saveFavoritePlaces(favoritePlaces)
        }
    }

    private fun saveFavoritePlaces(places: List<Place>) {
        synchronized(this) {
            sharedPreferences.edit()
                .putString(KEY_FAVORITE_PLACES, gson.toJson(places))
                .apply()
        }
    }
}
