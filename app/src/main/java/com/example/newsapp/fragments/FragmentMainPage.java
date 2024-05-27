package com.example.newsapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.newsapp.R;
import com.example.newsapp.adapter.NewsAdapter;
import com.example.newsapp.databinding.FragmentMainPageBinding;
import com.example.newsapp.model.WikiNews;
import com.example.newsapp.viewModel.WikiNewsViewModel;

import java.util.ArrayList;

public class FragmentMainPage extends Fragment {

    private FragmentMainPageBinding binding;
    private NewsAdapter mAdapter;
    private WikiNewsViewModel viewModel;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentMainPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mAdapter = new NewsAdapter(new ArrayList<>(), this::openNewsArticle);
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.recyclerView.setAdapter(mAdapter);

        viewModel = new ViewModelProvider(this).get(WikiNewsViewModel.class);

        viewModel.getWikiNewsLiveData().observe(getViewLifecycleOwner(), wikiNews -> {
            mAdapter.submitList((ArrayList<WikiNews>) wikiNews);
            binding.progressBar.setVisibility(View.GONE);
            binding.recyclerView.setVisibility(View.VISIBLE);
        });

        // Ensure the data is loaded initially
        if (!viewModel.isDataLoaded()) {
            binding.progressBar.setVisibility(View.VISIBLE);
            binding.recyclerView.setVisibility(View.GONE);
            viewModel.loadData();
        } else if (!viewModel.isInternetAvailable()) {
            showNoInternetUI();
        }
    }

    private void showNoInternetUI() {
        binding.progressBar.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.GONE);
        binding.noInternet.setVisibility(View.VISIBLE);
        binding.emptyView.setVisibility(View.VISIBLE);
        binding.emptyView.setText(getString(R.string.no_internet_connection));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void openNewsArticle(WikiNews news) {
        String url = news.getLink_article();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        if (intent.resolveActivity(requireActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
