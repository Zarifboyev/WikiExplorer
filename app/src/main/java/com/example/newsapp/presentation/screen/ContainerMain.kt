package com.example.newsapp.presentation.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.ScreenContainerMainBinding
import com.example.newsapp.presentation.viewModels.GlobalViewModel
import com.example.newsapp.presentation.viewModels.impl.HomeViewModelImpl
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class ContainerMain : Fragment(R.layout.screen_container_main) {

    private val containerMainBinding by viewBinding(ScreenContainerMainBinding::bind)
    private val globalViewModel: GlobalViewModel by activityViewModels()
    private val homeViewModel: HomeViewModelImpl by activityViewModels()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupTopAppBar()
        setupBottomNavigation()
        loadDefaultFragment(savedInstanceState)
    }


    private fun setupTopAppBar() {
        containerMainBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_select_languages -> {
                    showLanguagesDialog()
                    hideKeyboard()

                    true
                }
                else -> false
            }

        }
    }
    private fun showLanguagesDialog() {
        Timber.d("Showing language selection dialog")
        val languages = arrayOf(
            getString(R.string.russian_wikipedia),
            getString(R.string.uzbek_wikipedia),
            getString(R.string.english_wikipedia)
        )
        val checkedItem = -1 // No item selected by default
        var languageCodeSelected = "ru"
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                when (which) {
                    0 -> languageCodeSelected = "ru"
                    1 -> languageCodeSelected = "uz"
                    2 -> languageCodeSelected = "en"
                }
            }
            .setPositiveButton(R.string.OK) { dialog, which ->
                val currentLanguageCode = globalViewModel.languageCode.value
                if (currentLanguageCode != languageCodeSelected) {
                    val languageToFetch = currentLanguageCode?.takeIf { it.isNotBlank() } ?: "uz"

                    // Update the language code in ViewModel
                    globalViewModel.changeLanguage(languageCodeSelected)
                    globalViewModel.changeLanguage(languageCodeSelected)
                }
            }


            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun onSupportIconClicked() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_donate, null)
        val textViewCardNumber: TextView = dialogView.findViewById(R.id.textViewCardNumber)
        val imageViewCopyCardNumber: ImageView = dialogView.findViewById(R.id.imageViewCopyCardNumber)

        imageViewCopyCardNumber.setOnClickListener {
            val context = it.context  // Use the context from the view
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(getString(R.string.card_number), textViewCardNumber.text.toString())
            clipboard.setPrimaryClip(clip)
        }
        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.support_our_app))
            .setMessage(getString(R.string.donate_message))
            .setView(dialogView)
            .setPositiveButton(getString(R.string.donate)) { dialogInterface, _ ->
                // Process donation with card details
                // Validate and process the card details (you would implement this)
                dialogInterface.dismiss()
            }
            .setNegativeButton(getString(R.string.cancel)) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
        dialog.show()
    }

    private val homeScreen = HomeScreen()
    private val savedPagesScreen = SavedPagesScreen()

    private fun setupBottomNavigation() {
        containerMainBinding.bottomNavigation.apply {
            setupBadges(this)
            setOnItemSelectedListener { item ->
                when (item.itemId) {
                    R.id.bottom_nav_item_home -> {
                        loadFragment(homeScreen)
                        true
                    }
                    R.id.bottom_nav_item_youtube -> {
                        loadFragment(YouTubePlaylistsScreen())
                        true
                    }
                    R.id.bottom_nav_item_notes -> {
                        loadFragment(savedPagesScreen)
                        true
                    }
                    else -> {
                        loadFragment(homeScreen)
                        true
                    }
                }
            }
        }
    }

    private fun setupBadges(bottomNavigationView: BottomNavigationView) {
        // Optional badge setup
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

    private fun hideKeyboard() {
        val imm = context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
