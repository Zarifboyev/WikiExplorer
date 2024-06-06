package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.databinding.FragmentExploreBinding
import com.example.newsapp.presentation.adapters.WikiArticlesAdapter
import com.example.newsapp.utils.startFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.mlsoft.noteappnative.presentaion.viewModels.HomeViewModel
import uz.mlsoft.noteappnative.presentaion.viewModels.impl.HomeViewModelImpl

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.fragment_explore) {
    private val binding by viewBinding(FragmentExploreBinding::bind)
    private lateinit var viewModel: HomeViewModel
    private val adapter by lazy { WikiArticlesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModelImpl::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
        observeViewModel()
        viewModel.loadData()
    }

    private fun initAdapter() {
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewModel.fetchWikiNewsData.observe(viewLifecycleOwner, fetchWikiNewsDataObserver)
        viewModel.moveToInfoScreen.observe(viewLifecycleOwner, moveToInfoScreenObserver)
    }

    private val fetchWikiNewsDataObserver = Observer<List<WikiModel>> { articles ->
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

    private val moveToInfoScreenObserver = Observer<Boolean> { moveToInfo ->
        if (moveToInfo) {
            // Navigate to InfoScreen, adjust according to your navigation setup
            startFragment(ReadArticleScreen())
        }
    }
}
