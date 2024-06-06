package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.ContainerMainBinding
import com.example.newsapp.presentation.ui.sheets.ModalBottomSheet
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable

class ContainerMain : Fragment(R.layout.container_main) {

    private val binding by viewBinding(ContainerMainBinding::bind)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.container_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)

// Apply behavior attributes programmatically
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.saveFlags = BottomSheetBehavior.SAVE_ALL

        val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                // Do something for new state.
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Do something for slide offset.
            }
        }

// To add the callback:
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)

// To remove the callback:
        bottomSheetBehavior.removeBottomSheetCallback(bottomSheetCallback)

        // Load the default fragment
        // Set up badges
        val bottomNavigationView = view.findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val exploreBadge = bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_explore)
        exploreBadge.isVisible = true
        exploreBadge.number = 10 // Set the number you want to display

        val editBadge = bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_edit)
        editBadge.isVisible = true
        editBadge.number = 5 // Set the number you want to display

        val mainBadge = bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_main)
        mainBadge.isVisible = true
        mainBadge.number = 3 // Set the number you want to display

        // Set up the bottom navigation menu
        binding.bottomNavigation.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.bottom_nav_item_explore -> {
                    loadFragment(HomeScreen())
                    true
                }

                R.id.bottom_nav_item_edit -> {
                    loadFragment(InfoScreen())
                    true
                }

                R.id.bottom_nav_item_main -> {
                    // Show the bottom sheet when needed
                    // Show the bottom sheet when the button is clicked
                    val modalBottomSheet = ModalBottomSheet()
                    modalBottomSheet.show(childFragmentManager, "ModalBottomSheet")
                    true
                }

                else -> {
                    loadFragment(HomeScreen())
                    true
                }
            }
        }
        if (savedInstanceState == null) {
            binding.bottomNavigation.selectedItemId = R.id.bottom_nav_item_explore
            binding.appBarLayout.statusBarForeground =
                MaterialShapeDrawable.createWithElevationOverlay(context)
            binding.appBarLayout.setStatusBarForegroundColor(
                MaterialColors.getColor(
                    binding.appBarLayout, com.google.android.material.R.attr.colorOnSurface
                )
            )

        }

    }

    private fun loadFragment(fragment: Fragment) {
        // Load the selected fragment into the FrameLayout
        childFragmentManager.beginTransaction().replace(R.id.fragment_main_container1, fragment)
            .commit()
    }
}
