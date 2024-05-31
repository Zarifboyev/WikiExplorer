package com.example.newsapp.presentation.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.utils.FetchWikiArticleTask

class WikiNewsViewModel(private val application: Application) : ViewModel() {
    var isDataLoaded: Boolean = false;
    val wikiNewsLiveData = MutableLiveData<List<WikiEntity?>?>()

    fun getWikiNewsLiveData(): LiveData<List<WikiEntity?>?> {
        return wikiNewsLiveData
    }

    fun loadData() {
        val isInternetAvailable = isInternetAvailable()

        if (isInternetAvailable) {
            FetchDataTask().execute()
        } else {
            wikiNewsLiveData.value = null
        }
    }

    fun isInternetAvailable(): Boolean {
        val connectivityManager =
            application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    private inner class FetchDataTask : AsyncTask<Void?, Void?, List<WikiEntity?>?>() {


        override fun onPostExecute(wikiNewsList: List<WikiEntity?>?) {
            // Update the LiveData on the main thread
            wikiNewsLiveData.value = wikiNewsList
            isDataLoaded = true
        }

        override fun doInBackground(vararg params: Void?): List<WikiEntity?>? {
            TODO("Not yet implemented")
            return FetchWikiArticleTask.Companion.fetchWikiArticles()

        }
    }
}
