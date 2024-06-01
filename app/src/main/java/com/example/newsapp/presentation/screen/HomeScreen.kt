package com.example.newsapp.presentation.screen

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import by.kirich1409.viewbindingdelegate.viewBinding
import com.example.newsapp.R
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.utils.createFragment
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
        viewModel.loadData()
        viewModel.fetchWikiNewsData.observe(viewLifecycleOwner, fetchWikiNewsDataObserver)
        viewModel.moveToInfoScreen.observe(viewLifecycleOwner, moveToInfoScreenObserver)
    }

    private fun initAdapter() {

        binding.recyclerView.adapter = adapter

    }

    private val fetchWikiNewsDataObserver = Observer<List<WikiEntity>> { articles ->
        adapter.submitItems(articles)
    }

    private val moveToInfoScreenObserver = Observer<Boolean> { wikiEntity ->
        val bundle = Bundle().apply {
            putSerializable("data", wikiEntity)
        }
        // Assuming you have an InfoScreenActivity to navigate to
//        val intent = Intent(requireContext(), InfoScreen::class.java).apply {
//            putExtras(bundle)
//        }
//        startActivity(intent)

        startFragment(InfoScreen());
    }

}