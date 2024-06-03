package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.newsapp.R
import com.example.newsapp.databinding.FragmentProfileBinding

class ProfileScreen : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Uncomment and use if needed
        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        //     binding.text.text = Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY)
        // } else {
        //     binding.text.text = Html.fromHtml(content)
        // }

        // Uncomment and use if needed
        // binding.backImage.setOnClickListener {
        //     activity?.onBackPressed()
        // }
    }
}
