package com.example.newsapp.presentation.screen

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.utils.startFragment
import dagger.hilt.android.AndroidEntryPoint
import uz.mlsoft.noteappnative.presentaion.viewModels.HomeViewModel
import uz.mlsoft.noteappnative.presentaion.viewModels.impl.HomeViewModelImpl

@AndroidEntryPoint
class HomeScreen : Fragment(R.layout.fragment_home) {
    private val binding by viewBinding(FragmentHomeBinding::bind)
    private lateinit var viewModel: HomeViewModel
    private val adapter by lazy { NewsAdapter() }

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
        binding.recyclerView.visibility = View.VISIBLE // Ensure RecyclerView is visible
    }

    private fun observeViewModel() {
        viewModel.fetchWikiNewsData.observe(viewLifecycleOwner, fetchWikiNewsDataObserver)
        viewModel.moveToInfoScreen.observe(viewLifecycleOwner, moveToInfoScreenObserver)
    }

    private val fetchWikiNewsDataObserver = Observer<List<WikiModel>> { articles ->
        if (articles.isNotEmpty()) {
            adapter.submitItems(articles)
            binding.recyclerView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        } else {
            binding.recyclerView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        }
    }

    private val moveToInfoScreenObserver = Observer<Boolean> { moveToInfo ->
        if (moveToInfo) {
            // Navigate to InfoScreen, adjust according to your navigation setup
            startFragment(InfoScreen())
        }
    }
}
