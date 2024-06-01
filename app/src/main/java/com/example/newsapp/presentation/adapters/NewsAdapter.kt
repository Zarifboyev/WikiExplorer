package com.example.newsapp.presentation.adapters
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.entity.WikiEntity
import com.example.newsapp.databinding.NewsListItemBinding
import com.example.newsapp.presentation.adapters.NewsAdapter.NewsViewHolder
import timber.log.Timber
import kotlin.collections.ArrayList

class NewsAdapter : RecyclerView.Adapter<NewsViewHolder>() {
    private var isSortedAscending = true

    private val data = ArrayList<WikiEntity?>()

    private var onClickListener:((WikiEntity)->Unit)?=null

    fun onClickItem(block:(WikiEntity)->Unit){
        onClickListener=block
    }


    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(newsItems: List<WikiEntity?>) {
        data.clear()
        data.addAll(newsItems)
        Timber.tag("WikiNewsViewModel").d(data.toString())
        notifyDataSetChanged()
    }
    inner class NewsViewHolder(view: NewsListItemBinding): RecyclerView.ViewHolder(view.root){



        private val image: ImageView =view.itemImage
        private val textTitle: TextView = view.itemTitle
        private val textSubtitle: TextView = view.itemSubtitle




        fun bind(item: WikiEntity?) {
            // Set other views from the WikiEntity properties as needed
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) = holder.bind(data[position])

    override fun getItemCount()=data.size




}