package com.example.newsapp.domain.impl

import android.content.Context
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.domain.repository.WikiRepository
import io.github.fastily.jwiki.core.Wiki
import javax.inject.Inject

class WikiRepositoryImpl @Inject constructor(private val wikiModel: List<WikiModel>,
                                             private val wikiService: WikiService
) : WikiRepository {


    override fun getAllWiki(): List<WikiModel> {
        // Fetch data from Room DB


        return wikiModel
    }

    override fun saveWiki(wikiEntity: WikiEntity) {
        val size:Int = wikiModel.size
        for(i in 0 until size){
            val WikiEntity = wikiModel[i]
            //TODO: wikiDao.saveWiki(wikiEntity)
        }
    }

    override suspend fun fetchArticles(context: Context, builder: Wiki.Builder): List<WikiModel> {
        val wiki = wikiService.fetchArticles(context, builder)
        return wiki
    }



    override suspend fun getWikiBuilder(): Wiki.Builder {

        return Wiki.Builder()
    }

}