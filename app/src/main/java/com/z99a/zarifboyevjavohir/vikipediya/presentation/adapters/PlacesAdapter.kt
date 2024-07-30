package com.z99a.zarifboyevjavohir.vikipediya.presentation.adapters

import android.content.Context
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import com.z99a.zarifboyevjavohir.R
import com.z99a.zarifboyevjavohir.databinding.ItemPlaceBinding
import com.z99a.zarifboyevjavohir.vikipediya.data.SharedPreferencesManager
import com.z99a.zarifboyevjavohir.vikipediya.data.model.Place
import com.z99a.zarifboyevjavohir.vikipediya.presentation.viewModels.GlobalViewModel
import okhttp3.Cache
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File

class PlacesAdapter(
    private val context: Context,
    private val listener: OnPlaceClickListener,
    private val sharedPreferencesManager: SharedPreferencesManager,
) : ListAdapter<Place, PlacesAdapter.PlaceViewHolder>(PlaceDiffCallback()) {


    private val picasso: Picasso by lazy {
        val cacheDir = File(context.cacheDir, "picasso_cache")
        val cache = Cache(cacheDir, 50L * 1024 * 1024) // 50 MB
        val okHttpClient = OkHttpClient.Builder().cache(cache).build()
        Picasso.Builder(context)
            .downloader(OkHttp3Downloader(okHttpClient))
            .memoryCache(com.squareup.picasso.LruCache(50 * 1024 * 1024)) // 50 MB memory cache
            .build()
    }

    companion object {
        private const val MISSING_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/75/Gnome-image-missing.svg/200px-Gnome-image-missing.svg.png"
    }

    inner class PlaceViewHolder(private val binding: ItemPlaceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(place: Place) {
            Timber.d("Binding place: %s", place.title)
            with(binding) {
                setupTitle(place)
                setupDescription(place)
                setupImage(place)
                setupFavoriteIcon(place)
                setupBackgroundColor(place)
                setupClickListeners(place)
                setupDistance(place)
            }
        }

        private fun ItemPlaceBinding.setupTitle(place: Place) {
            placeTitle.text = place.title
        }

        private fun ItemPlaceBinding.setupDescription(place: Place) {
            val description = place.terms?.description?.firstOrNull()

//            articleStatus.visibility = if (place.isExisted == false) {
//                articleStatus.text = "O'zbekchada mavjud emas"
//                articleStatus.setTextColor(ContextCompat.getColor(context, R.color.md_theme_error))
//                View.VISIBLE
//            } else {
//                View.GONE
//            }

            placeDescription.visibility = View.VISIBLE
            placeDescription.text = when {
                description.isNullOrBlank() -> "Description is not available"
                else -> description
            }
            placeDescription.maxLines = 2
            placeDescription.setTextColor(
                ContextCompat.getColor(
                    context,
                    if (place.isExisted == true) R.color.semi_transparent_black else R.color.md_theme_tertiaryContainer_highContrast
                )
            )
        }

        private fun ItemPlaceBinding.setupImage(place: Place) {
            val imageUrl = place.thumbnail?.source?.replace("50px", "452px") ?: MISSING_IMAGE_URL
            picasso.load(imageUrl)
                .memoryPolicy(MemoryPolicy.NO_STORE)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(placeThumbnail, object : com.squareup.picasso.Callback {
                    override fun onSuccess() {
                        Timber.d("Image loaded successfully from cache for: %s", place.title)
                    }

                    override fun onError(e: Exception?) {
                        Timber.e(e, "Failed to load image from cache for: %s, trying online", place.title)
                        picasso.load(imageUrl)
                            .error(R.drawable.video_placeholder2)
                            .into(placeThumbnail)
                    }
                })
        }



        private fun ItemPlaceBinding.setupFavoriteIcon(place: Place) {
            favoriteIcon.setImageResource(
                if (place.isFavorite) R.drawable.ic_favourite_borderless else R.drawable.ic_favorite_border
            )
        }

        private fun ItemPlaceBinding.setupBackgroundColor(place: Place) {

        }

        private fun ItemPlaceBinding.setupClickListeners(place: Place) {
            placeCard.setOnClickListener {
                Timber.d("Place clicked: %s", place.title)
                listener.onPlaceClick(place)
            }
            placeDistance.setOnClickListener {
                Timber.d("Distance clicked for place: %s", place.title)
                listener.onDistanceClick(place)
            }
            favoriteIcon.setOnClickListener {
                handleFavoriteClick(place)
            }
        }

        private fun ItemPlaceBinding.setupDistance(place: Place) {
            placeDistance.text = "${place.distance} km"
        }

        private fun handleFavoriteClick(place: Place) {
            Timber.d("Favorite checkbox clicked for place: %s", place.title)
            val newFavoriteStatus = !place.isFavorite
            val updatedPlace = place.copy(isFavorite = newFavoriteStatus)
            if (newFavoriteStatus) {
                sharedPreferencesManager.addPlace(updatedPlace)
                Timber.d("Added place to favorites: %s", updatedPlace.title)
            } else {
                sharedPreferencesManager.removePlace(place.title)
                Timber.d("Removed place from favorites: %s", place.title)
            }
            listener.onFavoriteClick(adapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        Timber.d("Binding view holder at position: %d", position)
        holder.bind(getItem(position))
    }

    class PlaceDiffCallback : DiffUtil.ItemCallback<Place>() {
        override fun areItemsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem.title == newItem.title
        override fun areContentsTheSame(oldItem: Place, newItem: Place): Boolean = oldItem == newItem
    }

    interface OnPlaceClickListener {
        fun onPlaceClick(place: Place)
        fun onDistanceClick(place: Place)
        fun onLocationIconClick(place: Place)
        fun onFavoriteClick(position: Int)
    }
}
