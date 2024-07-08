package com.example.newsapp.presentation.adapters

import com.example.newsapp.data.model.Place



interface OnPlaceClickListener {
    fun onPlaceClick(place: Place?)
    fun onDistanceClick(place: Place)
    fun onLocationIconClick(place: Place)
    fun onFavoriteClick(position: Int)
}