package com.example.newsapp.fragments
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.databinding.FragmentHomeBinding
import com.example.newsapp.presentation.adapters.NewsAdapter
import com.example.newsapp.presentation.viewModels.WikiNewsViewModel
import com.example.newsapp.utils.FetchWikiArticleTask
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint

class FragmentWiki : Fragment(), FetchWikiArticleTask.FetchWikiArticleListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private  val mAdapter: NewsAdapter by lazy { NewsAdapter() }
    private lateinit var viewModel: WikiNewsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Execute FetchWikiArticleTask
        val fetchWikiArticleTask = FetchWikiArticleTask(this)
        fetchWikiArticleTask.execute()

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = mAdapter

        viewModel = ViewModelProvider(this).get(WikiNewsViewModel::class.java)

        viewModel.wikiNewsLiveData.observe(viewLifecycleOwner) { wikiNews ->
            // Update UI or perform any necessary operations with the fetched data
            wikiNews?.let { ArrayList(it) }?.let { mAdapter.submitItems(it)


            }
            binding.progressBar.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }

        // Ensure the data is loaded initially
        if (!viewModel.isDataLoaded) {
            binding.progressBar.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
            viewModel.loadData()
        } else if (!viewModel.isInternetAvailable()) {
            showNoInternetUI()
        }
    }

    private fun showNoInternetUI() {
        binding.progressBar.visibility = View.GONE
        binding.recyclerView.visibility = View.GONE
        binding.noInternet.visibility = View.VISIBLE
        binding.emptyView.visibility = View.VISIBLE
        binding.emptyView.text = getString(R.string.no_internet_connection)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Destroy the binding to avoid memory leaks
        _binding = null
    }

    private fun openNewsArticle(news: WikiEntity) {
        //val url: String = news.linkArticle
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://en.wikipedia.org/wiki/"))
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    override fun onWikiArticleFetched(htmlContent: String?) {
        // Handle the fetched HTML content of an individual wiki article if needed
        // For example, you can display it in a WebView or process it in any other way
        // Here, you can implement the specific behavior based on your requirements
    }



    override fun onWikiArticlesFetched(wikiNewsList: List<WikiEntity>?) {
        // Handle the fetched wiki articles here
        if (wikiNewsList != null) {
            // Update UI or perform any necessary operations with the fetched data
            viewModel.wikiNewsLiveData.value = wikiNewsList.filterNotNull()
        } else {
            // Handle null result or error
            showNoInternetUI()
        }
    }
}
