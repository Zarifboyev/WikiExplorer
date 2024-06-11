package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.databinding.ScreenHomeBinding
import com.example.newsapp.presentation.adapters.WikiArticlesAdapter
import com.example.newsapp.presentation.viewModels.HomeViewModel
import com.example.newsapp.presentation.viewModels.impl.HomeViewModelImpl
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.screen_home) {
    private val binding by viewBinding(ScreenHomeBinding::bind)
    private val adapter by lazy { WikiArticlesAdapter() }
    private lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModelImpl::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        observeViewModel()
        viewModel.loadData()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    private fun observeViewModel() {
        with(viewModel) {
            fetchWikiNewsData.observe(viewLifecycleOwner) { articles ->
                handleWikiNewsData(articles)
            }
            moveToInfoScreen.observe(viewLifecycleOwner) { moveToInfo ->
               // if (moveToInfo) startFragment(ReadArticleScreen())
            }
        }
//       TODO: viewModel.searchResults.observe(viewLifecycleOwner, Observer { results ->
//            handleSearchResults(results)
//        })
    }

    private fun handleWikiNewsData(articles: List<WikiModel>) {
        if (articles.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
            binding.emptyView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.recyclerView.visibility = View.VISIBLE
            binding.emptyView.visibility = View.GONE
            adapter.submitItems(articles)
            binding.progressBar.visibility = View.GONE
            Timber.tag("WikiArticles").d(articles.toString())
        }
    }

    private fun handleSearchResults(results: List<WikiModel>) {
        binding.progressBar.visibility = View.GONE
        val previousItemCount = adapter.itemCount
        adapter.submitItems(results)

        // Calculate the difference between the new and old item lists
        val diffResult = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int {
                return previousItemCount
            }

            override fun getNewListSize(): Int {
                return results.size
            }

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // Implement logic to check if items are the same
                // return oldList[oldItemPosition].id == newList[newItemPosition].id
                return TODO("Provide the return value")
            }

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                // Implement logic to check if item contents are the same
                // Example: return oldList[oldItemPosition] == newList[newItemPosition]
                return TODO("Provide the return value")
            }
        })

        // Dispatch the specific change events to the adapter
        diffResult.dispatchUpdatesTo(adapter)
    }

}
