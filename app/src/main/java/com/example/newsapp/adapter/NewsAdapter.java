package com.example.newsapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.model.News;

import java.util.List;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.NewsViewHolder> {
    private final List<News> mNewsList;
    private final OnItemClickListener mListener;

    public NewsAdapter(List<News> newsList, OnItemClickListener listener) {
        mNewsList = newsList;
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(News news);
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.news_list_item, parent, false);
        return new NewsViewHolder(v);
    }

    @Override
    public void onBindViewHolder(NewsViewHolder holder, int position) {
        News news = mNewsList.get(position);
        holder.mTitle.setText(news.getTitle());
        holder.mType.setText(news.getType());
        holder.mDate.setText(news.getDate());
        holder.mSection.setText(news.getSection());
        holder.mAuthor.setText(news.getAuthor());
        holder.bind(news, mListener);
    }

    @Override
    public int getItemCount() {
        return mNewsList.size();
    }

    public static class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView mTitle;
        TextView mType;
        TextView mDate;
        TextView mSection;
        TextView mAuthor;

        public NewsViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.news_title);
            mType = itemView.findViewById(R.id.news_type);
            mDate = itemView.findViewById(R.id.news_date);
            mSection = itemView.findViewById(R.id.section);
            mAuthor = itemView.findViewById(R.id.author);
        }

        public void bind(final News news, final OnItemClickListener listener) {
            itemView.setOnClickListener(v -> listener.onItemClick(news));
        }
    }

    public void clear() {
        mNewsList.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<News> newsList) {
        mNewsList.addAll(newsList);
        notifyDataSetChanged();
    }
}
