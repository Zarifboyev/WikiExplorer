package com.example.newsapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.example.newsapp.data.entity.WikiEntity

class NewsDiffCallback(
    private val oldList: List<WikiEntity>,
    private val newList: List<WikiEntity>
) : DiffUtil.Callback() {

    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare unique identifiers
        return oldList[oldItemPosition].title == newList[newItemPosition].title
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        // Compare entire contents of the items
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}
