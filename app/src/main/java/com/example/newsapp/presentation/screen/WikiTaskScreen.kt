package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.databinding.ScreenWikiTasksBinding
import com.example.newsapp.presentation.adapters.ArticlesAdapter
import com.example.newsapp.presentation.viewModels.CategoryViewModel
import com.example.newsapp.utils.isNetworkAvailable
import com.example.newsapp.utils.showNetworkUnavailableToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WikiTaskScreen : Fragment() {

    private val categoryViewModel: CategoryViewModel by viewModels()
    private lateinit var binding: ScreenWikiTasksBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ScreenWikiTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val adapter = ArticlesAdapter()
        recyclerView.adapter = adapter

        categoryViewModel.articles.observe(viewLifecycleOwner) { articles ->
            adapter.submitList(articles)
            Log.d("WikiTaskScreen", "Articles: $articles")
        }

        if (requireContext().isNetworkAvailable()) {
            categoryViewModel.fetchCategoryMembers("Turkum:Vikipediya:Koʻrsatmalar")
        } else {
            requireContext().showNetworkUnavailableToast()
        }
    }
}
