package com.example.newsapp.presentation.adapters

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.R
import com.example.newsapp.domain.service.CategoryMember

class ArticlesAdapter : ListAdapter<CategoryMember, ArticlesAdapter.ArticleViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<CategoryMember>() {
            override fun areItemsTheSame(oldItem: CategoryMember, newItem: CategoryMember): Boolean {
                return oldItem.pageid == newItem.pageid
            }

            override fun areContentsTheSame(oldItem: CategoryMember, newItem: CategoryMember): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.article_title)

        fun bind(article: CategoryMember) {
            titleTextView.text = article.title.replace("Vikipediya:","")
            itemView.setOnClickListener {
                // Handle item click
                // You can navigate to the article details screen or perform any other action
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://uz.wikipedia.org/wiki/${article.title}"))
                itemView.context.startActivity(intent)
            }
        }
    }
}
