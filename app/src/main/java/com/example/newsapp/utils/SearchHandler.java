package com.example.newsapp.utils;

import android.content.Context;

import com.example.newsapp.activity.MainActivity2;
import com.google.android.material.search.SearchBar;
import com.google.android.material.search.SearchView;

public class SearchHandler {
    private SearchBar searchBar;
    private SearchView searchView;
    private Context context;

    public SearchHandler(SearchBar searchBar, SearchView searchView, MainActivity2 context) {
        this.searchBar = searchBar;
        this.searchView = searchView;
        this.context = context;
        setupSearchViewListeners();
    }

    private void setupSearchViewListeners() {
        searchView.getEditText().setOnEditorActionListener((v, actionId, event) -> {
            searchBar.setText(searchView.getText());
            searchView.hide();
            return false;
        });

        searchView.addTransitionListener((searchView1, previousState, newState) -> {
            if (newState == SearchView.TransitionState.SHOWING) {
                // Handle search view opened.
            }
        });
    }
}
