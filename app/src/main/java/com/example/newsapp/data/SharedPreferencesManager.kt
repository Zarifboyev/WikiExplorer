package com.example.newsapp.data

import android.content.Context
import android.content.SharedPreferences
import com.example.newsapp.data.model.CONST
import com.example.newsapp.data.model.Place
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPreferencesManager {

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(CONST.PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun getEditor(context: Context): SharedPreferences.Editor {
        return getSharedPreferences(context).edit()
    }

    private fun fetchFavoritePlaces(context: Context): MutableList<Place> {
        val json = getSharedPreferences(context).getString(CONST.KEY_FAVORITE_PLACES, "[]")
        val type = object : TypeToken<MutableList<Place>>() {}.type
        return Gson().fromJson(json, type)
    }

    fun addPlace(context: Context, place: Place) {
        val favoritePlaces = fetchFavoritePlaces(context)
        if (favoritePlaces.none { it.title == place.title }) {
            favoritePlaces.add(place)
            saveFavoritePlaces(context, favoritePlaces)
        }
    }

    fun removePlace(context: Context, placeTitle: String) {
        val favoritePlaces = fetchFavoritePlaces(context)
        val updatedPlaces = favoritePlaces.filter { it.title != placeTitle }
        saveFavoritePlaces(context, updatedPlaces)
    }

    fun getFavoritePlaces(context: Context): List<Place> {
        return fetchFavoritePlaces(context)
    }

    private fun saveFavoritePlaces(context: Context, places: List<Place>) {
        val json = Gson().toJson(places)
        getEditor(context).putString(CONST.KEY_FAVORITE_PLACES, json).apply()
    }
}