package com.example.newsapp.presentation.screen

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.ContainerMainBinding
import com.example.newsapp.presentation.ui.sheets.ModalBottomSheet
import com.example.newsapp.presentation.viewModels.HomeViewModel
import com.example.newsapp.presentation.viewModels.impl.HomeViewModelImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerMain : Fragment(R.layout.container_main) {

    private val containerMainBinding by viewBinding(ContainerMainBinding::bind)
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModelImpl::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBottomSheetBehavior()
        setupTopAppBar()
        setupBottomNavigation()
        loadDefaultFragment(savedInstanceState)
    }

    private fun setupBottomSheetBehavior() {
        //TODO: Set Up Bottom Sheet Behavior
    }

    private fun setupTopAppBar() {
        containerMainBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.search -> {
                    showSearchDialog()
                    true
                }
                R.id.filter -> true
                else -> false
            }
        }
    }

    private fun setupBottomNavigation() {
        containerMainBinding.bottomNavigation.apply {
            setupBadges(this)
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.bottom_nav_item_home -> {
                        loadFragment(HomeScreen())
                        true
                    }
                    R.id.bottom_nav_item_youtube -> {
                        loadFragment(YouTubeScreen())
                        true
                    }
                    R.id.bottom_nav_item_notes -> {
                        loadFragment(NotesScreen())
                        true
                    }
//                    R.id.bottom_nav_item_account -> {
//                        ModalBottomSheet().show(childFragmentManager, "ModalBottomSheet")
//                        true
//                    }
                    else -> {
                        loadFragment(HomeScreen())
                        true
                    }
                }
            }
        }
    }

    private fun setupBadges(bottomNavigationView: BottomNavigationView) {
        bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_home).apply {
            isVisible = true
            number = 10
        }
        bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_youtube).apply {
            isVisible = true
            number = 5
        }
        bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_notes).apply {
            isVisible = true
            number = 3
        }
    }

    private fun loadDefaultFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            containerMainBinding.bottomNavigation.selectedItemId = R.id.bottom_nav_item_home
            containerMainBinding.appBarLayout.statusBarForeground =
                MaterialShapeDrawable.createWithElevationOverlay(context)
            containerMainBinding.appBarLayout.setStatusBarForegroundColor(
                MaterialColors.getColor(
                    containerMainBinding.appBarLayout,
                    com.google.android.material.R.attr.colorOnSurface
                )
            )
        }
    }

    private fun loadFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().replace(R.id.container, fragment).commit()
    }

    private fun showSearchDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)
        val searchEditText = dialogView.findViewById<EditText>(R.id.searchEditText)
        val searchButton = dialogView.findViewById<Button>(R.id.searchButton)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        searchButton.setOnClickListener {
            val searchQuery = searchEditText.text.toString()
            // TODO: Perform search based on the query
            dialog.dismiss()
        }

        dialog.show()
    }



    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
