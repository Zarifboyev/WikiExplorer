package com.example.newsapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.adapter.NewsAdapter;
import com.example.newsapp.databinding.FragmentMainPageBinding;
import com.example.newsapp.model.WikiNews;
import com.example.newsapp.viewModel.WikiNewsViewModel;

import java.util.ArrayList;
import java.util.List;

public class FragmentMainPage extends Fragment implements NewsAdapter.OnItemClickListener {

    private FragmentMainPageBinding binding;
    private NewsAdapter mAdapter;

    private ArrayList<WikiNews> wikiNewsList = new ArrayList<>();
    private WikiNewsViewModel viewModel;

    private ProgressBar progressBar;
    private RecyclerView recyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentMainPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBar = binding.loadingIndicator;
        recyclerView = binding.recyclerView;

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        mAdapter = new NewsAdapter(wikiNewsList, listener->
        {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(listener.getLink_article()));
            if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(mAdapter);

        viewModel = new ViewModelProvider(this).get(WikiNewsViewModel.class);
        viewModel.getWikiNewsLiveData().observe(getViewLifecycleOwner(), new Observer<List<WikiNews>>() {
            @Override
            public void onChanged(List<WikiNews> wikiNews) {
                mAdapter.submitList(wikiNewsList);
                progressBar.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
            }
        });

        if (!viewModel.isDataLoaded()) {
            viewModel.loadData();
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else if (!viewModel.isInternetAvailable()) {
            showNoInternetUI();
        }
    }

    private void showNoInternetUI() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onItemClick(WikiNews wikiNews) {
        String url = wikiNews.getLink_article();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
