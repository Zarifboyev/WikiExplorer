package com.example.newsapp.presentation.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.data.entity.WikiModel
import com.example.newsapp.databinding.ItemNewsListBinding
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
        val binding = ItemNewsListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WikiArticleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WikiArticleViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }

    override fun getItemCount() = data.size

    inner class WikiArticleViewHolder(binding: ItemNewsListBinding) : RecyclerView.ViewHolder(binding.root) {
        private val image: ImageView = binding.itemImage
        private val textTitle: TextView = binding.itemTitle
        private val textSubtitle: TextView = binding.itemSubtitle

        fun bind(item: WikiModel) {
            textTitle.text = item.title
            textSubtitle.text = item.description
            // Assuming content is a short description or subtitle
            // Set the image resource if there's an image URL or resource in the WikiModel
            // For example, you might use an image loading library like Glide or Picasso here
            //Glide.with(image.context).load("https://upload.wikimedia.org/wikipedia/commons/4/4a/Church_of_St._John_at_Kaneo_6.jpg").into(image)
            loadImageWithPicasso(image, item.image)
        }
        private fun loadImageWithPicasso(imageView: ImageView, imageUrl: String) {
            Picasso.get()
                .load(imageUrl)
                .networkPolicy(NetworkPolicy.OFFLINE)
                .into(imageView, object : Callback {
                    override fun onSuccess() {
                        // Image loaded from cache
                    }

                    override fun onError(e: Exception?) {
                        // Try again online if cache failed
                        Picasso.get()
                            .load(imageUrl)
                            .error(R.drawable.no_internet) // Add a placeholder for error
                            .into(imageView, object : Callback {
                                override fun onSuccess() {
                                    // Image loaded from online
                                }

                                override fun onError(e: Exception?) {
                                    Timber.tag("Picasso").v("Could not fetch image")
                                }
                            })
                    }
                })
        }
    }
}
