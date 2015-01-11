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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter.ViewHolder;
import com.upwardsnorthwards.blueplaqueslondon.fragments.AboutFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.MapFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.SettingsFragment;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends FragmentActivity implements
        OnFocusChangeListener, OnQueryTextListener, OnItemClickListener {

    private ListView searchListView;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseAppRating();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search)
                .getActionView();
        searchView.setOnQueryTextFocusChangeListener(this);
        searchView.setOnQueryTextListener(this);
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        FragmentManager fm = getSupportFragmentManager();
        switch (item.getItemId()) {
            case R.id.action_about:
                AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.show(fm, "fragment_about");
                break;
            case R.id.action_settings:
                SettingsFragment settingsFragment = new SettingsFragment();
                settingsFragment.show(fm, "fragment_settings");
                break;
            default:
                break;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForGooglePlayServicesAvailability();
        setupSearchListView();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            toggleSearchListVisibility(false, null);
        }
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        toggleSearchListVisibility(newText.length() > 0, newText);
        return false;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        toggleSearchListVisibility(false, null);
        MapFragment mapFragment = getMapFragment();
        if (mapFragment != null) {
            ViewHolder holder = (ViewHolder) view.getTag();
            // if the holder doesnt have a placemark associated with it,
            // it must be the 'find the closest plaque' item
            Placemark placemark = holder.placemark;
            if (placemark == null) {
                placemark = getClosestPlacemark();
            }
            BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                    BluePlaquesConstants.TABLE_ROW_PRESSED_EVENT,
                    placemark.getName());
            mapFragment.navigateToPlacemark(placemark);
        }
    }

    private void toggleSearchListVisibility(boolean show, String newText) {
        View searchList = findViewById(R.id.search_list);
        View map = findViewById(R.id.map);
        if (map != null && searchList != null) {
            searchList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            map.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            if (show) {
                searchAdapter.setPlacemarks(filterPlacemarksWithText(newText));
                searchAdapter.notifyDataSetChanged();
            }
        }
    }

    private List<Placemark> filterPlacemarksWithText(String newText) {
        List<Placemark> placemarks = new ArrayList<Placemark>();
        MapFragment mapFragment = getMapFragment();
        if (mapFragment != null) {
            for (Placemark placemark : mapFragment.getModel()
                    .getMassagedPlacemarks()) {
                if (placemark.getName().toLowerCase()
                        .contains(newText.toLowerCase())) {
                    placemarks.add(placemark);
                }
            }
            // alphabetically sort 'em
            Collections.sort(placemarks, new Comparator<Placemark>() {
                @Override
                public int compare(Placemark lhs, Placemark rhs) {
                    return lhs.getName().compareTo(rhs.getName());
                }
            });
        }
        return placemarks;
    }

    private void setupSearchListView() {
        searchListView = (ListView) findViewById(R.id.search_list);
        searchAdapter = new SearchAdapter(this, R.layout.search_item,
                new ArrayList<Placemark>());
        searchListView.setOnItemClickListener(this);
        searchListView.setAdapter(searchAdapter);
    }

    private MapFragment getMapFragment() {
        MapFragment mapFragment = null;
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null && fragments.size() > 0) {
            mapFragment = (MapFragment) fragments.get(0);
        }
        return mapFragment;
    }

    private Placemark getClosestPlacemark() {
        Placemark closestPlacemark = null;
        BluePlaquesLondonApplication application = (BluePlaquesLondonApplication) getApplication();
        Location currentLocation = application.getCurrentLocation();
        MapFragment mapFragment = getMapFragment();
        if (mapFragment != null) {
            closestPlacemark = mapFragment.getModel()
                    .getPlacemarkClosestToPlacemark(currentLocation);
        }
        return closestPlacemark;
    }

    private void checkForGooglePlayServicesAvailability() {
        int playServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (playServicesAvailable) {
            case ConnectionResult.SUCCESS: {

            }
            break;
            default: {
                GooglePlayServicesUtil.showErrorDialogFragment(playServicesAvailable, this, 123);
            }
        }
    }

    private void initialiseAppRating() {
        AppRate.with(this)
                .setInstallDays(10)
                .setLaunchTimes(10)
                .setRemindInterval(1)
                .setOnClickButtonListener(new OnClickButtonListener() {
                    @Override
                    public void onClickButton(int which) {
                        String event = analyticsStringForButtonPress(which);
                        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
                        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                                BluePlaquesConstants.RATE_APP_BUTTON_PRESSED_EVENT,
                                event);
                    }
                })
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private String analyticsStringForButtonPress(int which) {
        String event = "";
        switch (which) {
            case 0: {
                event = BluePlaquesConstants.DECLINE_RATE_APP_BUTTON_PRESSED_EVENT;
            }
            break;
            case 1: {
                event = BluePlaquesConstants.REMIND_RATE_APP_BUTTON_PRESSED_EVENT;
            }
            break;
            case 2: {
                event = BluePlaquesConstants.RATE_APP_STORE_OPENED_EVENT;
            }
            break;
        }
        return event;
    }
}