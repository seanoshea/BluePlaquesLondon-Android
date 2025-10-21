// Copyright (c) 2014 - 2016 Upwards Northwards Software Limited
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

package com.upwardsnorthwards.blueplaqueslondon.views;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.ArrayList;
import java.util.List;

/**
 * Used in conjunction with the search adapter to show the user a list of placemarks to search for.
 */
public class ArrayAdapterSearchView extends SearchView implements SearchView.OnQueryTextListener, View.OnFocusChangeListener, OnItemClickListener {

    private SearchView.SearchAutoComplete searchAutoComplete;
    private SearchAdapter searchAdapter;

    public ArrayAdapterSearchView(@NonNull final Context context) {
        super(context);
        initialize(context);
    }

    public ArrayAdapterSearchView(@NonNull final Context context, final AttributeSet attrs) {
        super(context, attrs);
        initialize(context);
    }

    public void notifyAdapterOfPlacemarks(final List<Placemark> placemarks) {
        searchAdapter.setPlacemarks(placemarks);
    }

    private void setOnItemClickListener(final OnItemClickListener listener) {
        searchAutoComplete.setOnItemClickListener(listener);
    }

    private void setAdapter(final ArrayAdapter<?> adapter) {
        searchAutoComplete.setAdapter(adapter);
    }

    @Override
    public boolean onQueryTextSubmit(final String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(final String s) {
        searchAdapter.getFilter().filter(s);
        return false;
    }

    @Override
    public void onFocusChange(final View v, final boolean hasFocus) {
        if (hasFocus) {
            searchAdapter.notifyDataSetChanged();
        }
    }

    public void onItemClick(final AdapterView<?> p, final View v, final int pos, final long id) {
        navigateToPlacemarkAtIndex(pos);
    }

    private void navigateToPlacemarkAtIndex(final int index) {
        final Placemark placemark = searchAdapter.getFilteredPlacemarkAtPosition(index);
        // Notify the MapFragment directly instead of using Otto bus
        try {
            if (getContext() instanceof androidx.appcompat.app.AppCompatActivity) {
                androidx.appcompat.app.AppCompatActivity activity = (androidx.appcompat.app.AppCompatActivity) getContext();
                androidx.fragment.app.Fragment navHostFragment = activity.getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    androidx.fragment.app.Fragment mapFragment = navHostFragment.getChildFragmentManager()
                            .getPrimaryNavigationFragment();
                    if (mapFragment != null && mapFragment.getClass().getSimpleName().equals("BluePlaquesMapFragment")) {
                        ((com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment) (Object) mapFragment)
                                .onPlacemarkSelected(placemark);
                    }
                }
                // Also notify MainActivity to clear the search view
                if (activity instanceof com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity) {
                    ((com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity) activity).onPlacemarkSelected(placemark);
                }
            }
        } catch (Exception e) {
            android.util.Log.e("ArrayAdapterSearchView", "Error navigating to placemark", e);
        }
    }

    private void initialize(final Context context) {
        // Get SearchAutoComplete using AndroidX internal ID
        int searchSrcTextId = getResources().getIdentifier("search_src_text", "id", context.getPackageName());
        if (searchSrcTextId != 0) {
            searchAutoComplete = (SearchAutoComplete) findViewById(searchSrcTextId);
        } else {
            // Fallback: try androidx package
            searchSrcTextId = getResources().getIdentifier("search_src_text", "id", "androidx.appcompat");
            if (searchSrcTextId != 0) {
                searchAutoComplete = (SearchAutoComplete) findViewById(searchSrcTextId);
            }
        }

        searchAdapter = new SearchAdapter(context, new ArrayList<Placemark>());

        if (searchAutoComplete != null) {
            setAdapter(searchAdapter);
            setOnItemClickListener(this);
        }
        setOnQueryTextListener(this);
        setOnQueryTextFocusChangeListener(this);
    }

}
