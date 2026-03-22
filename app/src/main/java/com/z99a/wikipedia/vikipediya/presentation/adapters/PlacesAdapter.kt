package com.z99a.wikipedia.vikipediya.presentation.adapters

import android.content.Context
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
import com.z99a.wikipedia.R
import com.z99a.wikipedia.databinding.ItemPlaceBinding
import com.z99a.wikipedia.vikipediya.data.SharedPreferencesManager
import com.z99a.wikipedia.vikipediya.data.model.Place
import okhttp3.Cache
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

class PlacesAdapter(
    private val context: Context,
    private val listener: OnPlaceClickListener,
    private val sharedPreferencesManager: SharedPreferencesManager,
) : ListAdapter<Place, PlacesAdapter.PlaceViewHolder>(PlaceDiffCallback()) {


    //TODO: Apply DI for Picasso and OkHTTP
    //TODO: GIT: Push updated and working app to another branch like: BETA
    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .cache(Cache(File(context.cacheDir, "http_cache"), 50L * 1024 * 1024))
            .connectTimeout(15, TimeUnit.SECONDS) // Give more time to connect
            .readTimeout(20, TimeUnit.SECONDS)    // Give more time to download
            .retryOnConnectionFailure(true)
            .build()
    }

    private val picasso: Picasso by lazy {
        Picasso.Builder(context)
            .downloader(OkHttp3Downloader(okHttpClient))
            .loggingEnabled(true) // Helpful for debugging
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
            // Wikipedia thumb URLs often use /thumb/ and /XXpx- at the end.
            // Ensure the replacement logic is robust.
            // TODO: Check if the imageURl is loading correctly
            val imageUrl = place.thumbnail?.source?.let { url ->
                if (url.contains("50px")) url.replace("50px", "400px") else url
            } ?: MISSING_IMAGE_URL
            picasso.load(imageUrl)
                .placeholder(R.drawable.video_placeholder2) // Show while loading
                .error(R.drawable.video_placeholder2)       // Show if network fails
                .into(placeThumbnail)
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
