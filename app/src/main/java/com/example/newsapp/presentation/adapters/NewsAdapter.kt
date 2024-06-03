package com.example.newsapp.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
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


    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(newsItems: List<WikiModel>) {
        data.clear()
        data.addAll(newsItems)
        notifyDataSetChanged()
        Timber.tag("NewsAdapter").d(data.toString())
    }

    inner class NewsViewHolder(private var binding: NewsListItemBinding) : RecyclerView.ViewHolder(binding.root) {
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
        val item = data[position]

        Log.d("NewsAdapter", "Binding item at position $position: $item")

        holder.bind(data[position])
    }

    override fun getItemCount() = data.size




}
