package com.example.newsapp.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.data.model.WikiModel
import com.example.newsapp.databinding.NewsListItemBinding
import timber.log.Timber

class NewsAdapter : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {
    private var isSortedAscending = true
    private val data = ArrayList<WikiModel>()
    private var onClickListener: ((WikiModel) -> Unit)? = null

    fun onClickItem(block: (WikiModel) -> Unit) {
        onClickListener = block
    }

    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(newsItems: List<WikiModel>) {
        data.clear()
        data.addAll(newsItems)
        notifyDataSetChanged()
        Timber.tag("NewsAdapter").d(data.toString())
    }

    inner class NewsViewHolder(private val binding: NewsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val image: ImageView = binding.itemImage
        private val textTitle: TextView = binding.itemTitle
        private val textSubtitle: TextView = binding.itemSubtitle

        fun bind(item: WikiModel) {
            textTitle.text = item.title
            textSubtitle.text = item.description
            Glide.with(image.context).load(item.image).into(image) // Load the image using Glide
            itemView.setOnClickListener {
                onClickListener?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val binding = NewsListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NewsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount() = data.size

    @SuppressLint("NotifyDataSetChanged")
    fun sortItemsAscending() {
        data.sortBy { it.title }
        isSortedAscending = true
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun sortItemsDescending() {
        data.sortByDescending { it.title }
        isSortedAscending = false
        notifyDataSetChanged()
    }

    fun toggleSortOrder() {
        if (isSortedAscending) {
            sortItemsDescending()
        } else {
            sortItemsAscending()
        }
    }
}
