package com.example.newsapp.presentation.screen

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.SharedPreferencesManager
import com.example.newsapp.data.model.Place
import com.example.newsapp.databinding.ScreenWikiTasksBinding
import com.example.newsapp.presentation.adapters.OnPlaceClickListener
import com.example.newsapp.presentation.adapters.PlacesAdapter
import com.example.newsapp.utils.isNetworkAvailable
import com.example.newsapp.utils.showNetworkUnavailableToast
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SavedPagesScreen : Fragment(), OnPlaceClickListener {

    private lateinit var binding: ScreenWikiTasksBinding
    private lateinit var savedPlacesAdapter: PlacesAdapter
    private var deleteIcon: Drawable? = null
    private lateinit var background: ColorDrawable

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenWikiTasksBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        if (requireContext().isNetworkAvailable()) {
            observeViewModel()
        } else {
            requireContext().showNetworkUnavailableToast()
        }
    }

    private fun setupRecyclerView() {
        savedPlacesAdapter = PlacesAdapter(requireContext(), this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = savedPlacesAdapter
            setHasFixedSize(true)
            deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
            background = ColorDrawable(Color.WHITE)

            val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                    val position = viewHolder.adapterPosition
                    val place = savedPlacesAdapter.currentList[position]
                    SharedPreferencesManager.removePlace(requireContext(), place.title)
                    observeViewModel()
                    savedPlacesAdapter.notifyItemRemoved(position)

                    Toast.makeText(requireContext(), "Place removed from favorites", Toast.LENGTH_SHORT).show()
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                    val itemView = viewHolder.itemView
                    val iconMargin = (itemView.height - (deleteIcon?.intrinsicHeight ?: 0)) / 2

                    if (dX < 0) { // Swipe left to delete
                        background.setBounds(
                            itemView.right + dX.toInt(),
                            itemView.top,
                            itemView.right,
                            itemView.bottom
                        )
                        deleteIcon?.setBounds(
                            itemView.right - iconMargin - (deleteIcon?.intrinsicWidth ?: 0),
                            itemView.top + iconMargin,
                            itemView.right - iconMargin,
                            itemView.bottom - iconMargin
                        )
                    } else {
                        background.setBounds(0, 0, 0, 0)
                        deleteIcon?.setBounds(0, 0, 0, 0)
                    }

                    background.draw(c)
                    deleteIcon?.draw(c)
                }
            })
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun observeViewModel() {
        // Observe the favorite places
        lifecycleScope.launch {
            val favoritePlaces = SharedPreferencesManager.getFavoritePlaces(requireContext())
            savedPlacesAdapter.submitList(favoritePlaces)
        }
    }

    override fun onPlaceClick(place: Place?) {
        place?.articleUrl?.let {
            openUrlInCustomTab(requireContext(), it)
        }
    }

    override fun onDistanceClick(place: Place) {
        openLocationInMaps(place.title)
    }

    override fun onLocationIconClick(place: Place) {
        openLocationInMaps(place.title)
    }

    override fun onFavoriteClick(position: Int) {
        val place = savedPlacesAdapter.currentList[position]
        place.isFavorite = !place.isFavorite
        savedPlacesAdapter.notifyItemChanged(position)
        toggleFavoriteClick(place)
    }

    private fun toggleFavoriteClick(place: Place) {
        if (place.isFavorite) {
            SharedPreferencesManager.addPlace(requireContext(), place)
        } else {
            SharedPreferencesManager.removePlace(requireContext(), place.title)
        }
        // Update the list after adding or removing
        lifecycleScope.launch {
            val favoritePlaces = SharedPreferencesManager.getFavoritePlaces(requireContext())
            savedPlacesAdapter.submitList(favoritePlaces)
        }
    }

    private fun openLocationInMaps(placeName: String?) {
        val gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(placeName))
        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply {
            setPackage("com.google.android.apps.maps")
        }
        startActivity(mapIntent)
    }

    private fun openUrlInCustomTab(context: Context, url: String) {
        val customTabsIntent = CustomTabsIntent.Builder().build()
        customTabsIntent.launchUrl(context, Uri.parse(url))
    }
}