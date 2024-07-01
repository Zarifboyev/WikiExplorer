package com.example.newsapp.presentation.screen

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.databinding.ScreenContainerMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.color.MaterialColors
import com.google.android.material.shape.MaterialShapeDrawable
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContainerMain : Fragment(R.layout.screen_container_main) {

    private val containerMainBinding by viewBinding(ScreenContainerMainBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupTopAppBar()
        setupBottomNavigation()
        loadDefaultFragment(savedInstanceState)
    }


    private fun setupTopAppBar() {
        containerMainBinding.topAppBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_rate_app -> {
                    onRateIconClicked()
                    true
                }
                else -> false
            }
        }
    }

    private fun onRateIconClicked() {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822" // Set the MIME type to indicate it's an email
            putExtra(Intent.EXTRA_EMAIL, arrayOf("developers@example.com"))
            putExtra(Intent.EXTRA_SUBJECT, "Feedback on the News App")
            putExtra(Intent.EXTRA_TEXT, "Please write your feedback here...")
        }

        // Ensure the user can choose between email apps like Gmail or Outlook
        val packageManager = requireActivity().packageManager
        val emailClients = arrayListOf<Intent>()

        packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).forEach { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            if (packageName.contains("com.google.android.gm") || packageName.contains("com.microsoft.office.outlook")) {
                val emailIntent = Intent(Intent.ACTION_SEND).apply {
                    setPackage(packageName)
                    type = "message/rfc822"
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("zarifboyevjavohir27@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Feedback on the News App")
                    putExtra(Intent.EXTRA_TEXT, "Please write your feedback here...")
                }
                emailClients.add(emailIntent)
            }
        }

        if (emailClients.isNotEmpty()) {
            val chooserIntent = Intent.createChooser(emailClients.removeAt(0), "Send Email")
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, emailClients.toTypedArray())
            startActivity(chooserIntent)
        } else {
            // Fallback to default email client selection
            startActivity(Intent.createChooser(intent, "Send Email"))
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
                        loadFragment(YouTubePlaylistsScreen())
                        true
                    }
                    R.id.bottom_nav_item_notes -> {
                        loadFragment(WikiTaskScreen())
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
//        bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_home).apply {
//            isVisible = true
//            number = 10
//        }
//        bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_youtube).apply {
//            isVisible = true
//            number = 5
//        }
//        bottomNavigationView.getOrCreateBadge(R.id.bottom_nav_item_notes).apply {
//            isVisible = true
//            number = 3
//        }
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
