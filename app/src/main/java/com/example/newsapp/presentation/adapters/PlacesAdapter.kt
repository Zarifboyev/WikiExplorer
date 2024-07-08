package com.example.newsapp.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.SharedPreferencesManager
import com.example.newsapp.data.model.Place
import com.example.newsapp.databinding.ItemPlaceBinding
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class PlacesAdapter(
    private val context: Context,
    private val listener: OnPlaceClickListener
) : ListAdapter<Place, PlacesAdapter.PlaceViewHolder>(PlaceDiffCallback()) {

    private val picasso: Picasso

    init {
        // Set up disk cache for Picasso
        val cacheDir = File(context.cacheDir, "picasso_cache")
        val cacheSize = 50L * 1024 * 1024 // 50 MB
        val cache = Cache(cacheDir, cacheSize)

        val okHttpClient = OkHttpClient.Builder()
            .cache(cache)
            .build()

        picasso = Picasso.Builder(context)
            .downloader(OkHttp3Downloader(okHttpClient))
            .build()
    }

    inner class PlaceViewHolder(private val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place?) {
            if (place == null) return

            binding.apply {
                placeTitle.text = place.title
                placeDescription.text = place.description
                placeDescription.visibility = if (place.description.isNullOrBlank()) View.GONE else View.VISIBLE
                placeDistance.text = "${place.distance} km"

                val imageUrl = place.thumbnail ?: "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Gnome-image-missing.svg/200px-Gnome-image-missing.svg.png"
                picasso.load(imageUrl).into(placeThumbnail)

                val isFavorite = place.isFavorite
                favoriteCheckBox.setImageResource(if (isFavorite) R.drawable.ic_star_filled else R.drawable.ic_star_outline)

                val isExistedInUzWiki = place.isArticleExistedInUzWiki
                backgroundItem.setBackgroundColor(context.resources.getColor(
                    if (isExistedInUzWiki) R.color.md_theme_background else R.color.background_is_not_existed))

                placeCard.setOnClickListener { listener.onPlaceClick(place) }
                placeDistance.setOnClickListener { listener.onDistanceClick(place) }
                locationIcon.setOnClickListener { listener.onLocationIconClick(place) }
                iconFavourites.setOnClickListener {
                    val newFavoriteStatus = !place.isFavorite
                    val updatedPlace = place.copy(isFavorite = newFavoriteStatus)
                    if (newFavoriteStatus) {
                        SharedPreferencesManager.addPlace(context, updatedPlace)
                    } else {
                        SharedPreferencesManager.removePlace(context, place.title)
                    }
                    listener.onFavoriteClick(adapterPosition)
                    notifyItemChanged(adapterPosition)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean {
            return oldItem == newItem
        }
    }
}