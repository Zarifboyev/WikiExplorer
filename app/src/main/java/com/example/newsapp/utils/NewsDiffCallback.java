package com.example.newsapp.utils;
import androidx.recyclerview.widget.DiffUtil;

import com.example.newsapp.model.WikiNews;

import java.util.List;

public class NewsDiffCallback extends DiffUtil.Callback {
    private final List<WikiNews> mOldList;
    private final List<WikiNews> mNewList;

    public NewsDiffCallback(List<WikiNews> oldList, List<WikiNews> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).getLink_article().equals(mNewList.get(newItemPosition).getLink_article());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        // Assuming equals() is properly implemented in WikiNews class
        return mOldList.get(oldItemPosition).equals(mNewList.get(newItemPosition));
    }
}
