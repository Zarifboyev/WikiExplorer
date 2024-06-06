package com.example.newsapp.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.databinding.NewsListItemBinding
import timber.log.Timber

class WikiArticlesAdapter : RecyclerView.Adapter<WikiArticlesAdapter.WikiArticleViewHolder>() {
    private var data = mutableListOf<WikiModel>()

    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(newsItems: List<WikiModel>) {
        data.clear()
        data.addAll(newsItems)
        notifyDataSetChanged()
        Timber.tag("WikiArticlesAdapter").d(data.toString())
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WikiArticleViewHolder {
        val binding = NewsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WikiArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WikiArticleViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    inner class WikiArticleViewHolder(private val binding: NewsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val image: ImageView = binding.itemImage
        private val textTitle: TextView = binding.itemTitle
        private val textSubtitle: TextView = binding.itemSubtitle

        fun bind(item: WikiModel) {
            textTitle.text = item.title
            textSubtitle.text = item.description
            // Assuming content is a short description or subtitle
            // Set the image resource if there's an image URL or resource in the WikiModel
            // For example, you might use an image loading library like Glide or Picasso here
            Glide.with(image.context).load("https://upload.wikimedia.org/wikipedia/commons/4/4a/Church_of_St._John_at_Kaneo_6.jpg").into(image)
        }
    }
}
