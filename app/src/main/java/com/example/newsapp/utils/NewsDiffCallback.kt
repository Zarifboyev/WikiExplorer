package com.example.newsapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.newsapp.model.WikiNews

class NewsDiffCallback(
    private val oldList: List<WikiNews>,
    private val newList: List<WikiNews>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare unique identifiers
        return oldList[oldItemPosition].linkArticle == newList[newItemPosition].linkArticle
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare entire contents of the items
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
