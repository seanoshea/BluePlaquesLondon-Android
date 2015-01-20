package com.upwardsnorthwards.blueplaqueslondon;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Sean on 1/12/15.
 */
public class ArrayAdapterSearchView extends SearchView implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, android.view.View.OnFocusChangeListener {

    private static final String TAG = "ArrayAdapterSearchView";

    private SearchView.SearchAutoComplete searchAutoComplete;
    private SearchAdapter searchAdapter;
    private List<Placemark> placemarks;

    public ArrayAdapterSearchView(Context context) {
        super(context);
        initialize(context);
    }

    public ArrayAdapterSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public void notifyAdapterOfPlacemarks(List<Placemark> placemarks) {
        this.placemarks = placemarks;
        searchAdapter.setPlacemarks(placemarks);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        searchAutoComplete.setOnItemClickListener(listener);
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        searchAutoComplete.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        searchAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    public boolean onSuggestionSelect(int index) {
        Log.v(TAG, "onSuggestionSelect " + index);
        return true;
    }

    public boolean onSuggestionClick(int index) {
        Log.v(TAG, "onSuggestionClick " + index);
        return true;
    }

    private void initialize(Context context) {
        searchAutoComplete = (SearchAutoComplete) findViewById(R.id.search_src_text);
        searchAdapter = new SearchAdapter(context, R.layout.search_item,
                new ArrayList<Placemark>());
        setAdapter(searchAdapter);
        setOnQueryTextListener(this);
        setOnQueryTextFocusChangeListener(this);
        setOnSuggestionListener(this);
    }

    public List<Placemark> getPlacemarks() {
        return placemarks;
    }

    public void setPlacemarks(List<Placemark> placemarks) {
        this.placemarks = placemarks;
    }
}
