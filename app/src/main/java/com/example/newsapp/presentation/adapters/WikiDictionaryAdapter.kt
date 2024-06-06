package com.example.newsapp.presentation.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.newsapp.data.model.WikiEntityData
import com.example.newsapp.databinding.WikiDictionaryItemBinding
import timber.log.Timber

class WikiDictionaryAdapter : RecyclerView.Adapter<WikiDictionaryAdapter.WikiDictionaryViewHolder>() {
    private var isSortedAscending = true
    private val data = ArrayList<WikiEntityData>()
    private var onClickListener: ((WikiEntityData) -> Unit)? = null


    @SuppressLint("NotifyDataSetChanged")
    fun submitItems(newsItems: List<WikiEntityData>) {
        data.clear()
        data.addAll(newsItems)
        notifyDataSetChanged()
        Timber.tag("NewsAdapter").d(data.toString())
    }

    inner class WikiDictionaryViewHolder(private var binding: WikiDictionaryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        private val textTitle: TextView = binding.pageidTextView
        private val textSubtitle: TextView = binding.titleTextView

        fun bind(item: WikiEntityData) {
            textTitle.text = item.title
            textSubtitle.text = item.pageid.toString()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WikiDictionaryViewHolder {
        val binding = WikiDictionaryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WikiDictionaryViewHolder(binding)
    }



    override fun onBindViewHolder(holder: WikiDictionaryViewHolder, position: Int) {
        val item = data[position]

        Log.d("NewsAdapter", "Binding item at position $position: $item")

        holder.bind(data[position])
    }

    override fun getItemCount() = data.size




}
