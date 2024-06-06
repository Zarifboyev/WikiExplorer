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
import com.example.newsapp.databinding.ScreenReadArticleBinding
import com.example.newsapp.presentation.adapters.WikiArticlesAdapter
import com.example.newsapp.utils.startFragment
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import uz.mlsoft.noteappnative.presentaion.viewModels.HomeViewModel
import uz.mlsoft.noteappnative.presentaion.viewModels.impl.HomeViewModelImpl

@AndroidEntryPoint
class ReadArticleScreen : Fragment(R.layout.screen_read_article) {
    private val binding by viewBinding(ScreenReadArticleBinding::bind)
    private lateinit var viewModel: HomeViewModel
    private val adapter by lazy { WikiArticlesAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[HomeViewModelImpl::class.java]
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAdapter()
    }

    private fun initAdapter() {
    }

//    private fun observeViewModel() {
//    }


}
