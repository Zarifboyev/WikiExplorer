package com.example.newsapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.model.WikiNews;
import com.example.newsapp.utils.NewsDiffCallback;

import java.util.ArrayList;
import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<WikiNews> mWikiNewsList;
    private final OnItemClickListener mListener;

    public NewsAdapter(List<WikiNews> wikiNewsList, OnItemClickListener listener) {
        mWikiNewsList = wikiNewsList;
        mListener = listener;
    }

    public void submitList(ArrayList<WikiNews> wikiNews) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new NewsDiffCallback(mWikiNewsList, wikiNews));

        // Update the current list
        mWikiNewsList.clear();
        mWikiNewsList.addAll(wikiNews);

        // Notify adapter with the diff result
        diffResult.dispatchUpdatesTo(this);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(WikiNews wikiNews);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        WikiNews wikiNews = mWikiNewsList.get(position);
        holder.mTitle.setText(wikiNews.getTitle());
        holder.mArticleText.setText(wikiNews.getArticle_text());
        holder.bind(wikiNews, mListener);
    }

    @Override
    public int getItemCount() {
        return mWikiNewsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mArticleText;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.itemTitle);
            mArticleText = itemView.findViewById(R.id.itemSubtitle);


        }

        public void bind(final WikiNews wikiNews, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(wikiNews));
        }
    }

    public void clear() {
        mWikiNewsList.clear();
        notifyDataSetChanged();
    }

}
