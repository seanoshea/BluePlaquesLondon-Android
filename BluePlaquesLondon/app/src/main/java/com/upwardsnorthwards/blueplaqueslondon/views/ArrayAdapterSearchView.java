package com.upwardsnorthwards.blueplaqueslondon.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.adapters.recyclerview.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.ArrayList;
import java.util.List;

public class ArrayAdapterSearchView extends ConstraintLayout implements SearchView.OnQueryTextListener {

    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchAdapter searchAdapter;
    private List<Placemark> placemarks;

    public ArrayAdapterSearchView(Context context) {
        super(context);
        init(context);
    }

    public ArrayAdapterSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.search_view_layout, this, true);
        searchView = findViewById(R.id.search_view);
        recyclerView = findViewById(R.id.search_recycler_view);
        placemarks = new ArrayList<>();
        searchAdapter = new SearchAdapter(placemarks);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(searchAdapter);
        searchView.setOnQueryTextListener(this);
    }

    public void setPlacemarks(List<Placemark> placemarks) {
        this.placemarks.clear();
        this.placemarks.addAll(placemarks);
        searchAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        searchAdapter.getFilter().filter(newText);
        return false;
    }
}
