// Copyright (c) 2014 - 2015 Upwards Northwards Software Limited
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// 3. All advertising materials mentioning features or use of this software
// must display the following acknowledgement:
// This product includes software developed by Upwards Northwards Software Limited.
// 4. Neither the name of Upwards Northwards Software Limited nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY UPWARDS NORTHWARDS SOFTWARE LIMITED ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE UPWARDS NORTHWARDS SOFTWARE LIMITED BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.upwardsnorthwards.blueplaqueslondon;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.ArrayList;
import java.util.List;

public class ArrayAdapterSearchView extends SearchView implements SearchView.OnQueryTextListener, SearchView.OnSuggestionListener, android.view.View.OnFocusChangeListener, OnItemClickListener {

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

    public void onItemClick(AdapterView<?> p, View v, int pos, long id) {
        if (pos == 0) {
            navigateToPlacemarkAtIndex(pos);
        }
    }

    public boolean onSuggestionSelect(int index) {
        navigateToPlacemarkAtIndex(index);
        return true;
    }

    public boolean onSuggestionClick(int index) {
        navigateToPlacemarkAtIndex(index);
        return true;
    }

    private void navigateToPlacemarkAtIndex(int index) {
        Placemark placemark = searchAdapter.getFilteredPlacemarkAtPosition(index);
        Log.v(TAG, "Navigating to placemark " + placemark.getName());
        BluePlaquesLondonApplication.bus.post(placemark);
    }

    private void initialize(Context context) {
        searchAutoComplete = (SearchAutoComplete) findViewById(R.id.search_src_text);
        searchAdapter = new SearchAdapter(context, R.layout.search_item,
                new ArrayList<Placemark>());
        setAdapter(searchAdapter);
        setOnQueryTextListener(this);
        setOnQueryTextFocusChangeListener(this);
        setOnSuggestionListener(this);
        setOnItemClickListener(this);
    }

    public List<Placemark> getPlacemarks() {
        return placemarks;
    }

    public void setPlacemarks(List<Placemark> placemarks) {
        this.placemarks = placemarks;
    }
}
