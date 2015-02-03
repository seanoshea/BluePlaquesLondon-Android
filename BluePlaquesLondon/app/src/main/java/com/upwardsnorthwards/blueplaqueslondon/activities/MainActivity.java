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

package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.squareup.otto.Subscribe;
import com.upwardsnorthwards.blueplaqueslondon.views.ArrayAdapterSearchView;
import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.fragments.AboutFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.MapFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.SettingsFragment;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";
    private static final int GOOGLE_PLAY_SERVICES_REQUEST = 9002;

    private ArrayAdapterSearchView searchView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialiseAppRating();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.notifyAdapterOfPlacemarks(getMapFragment().getModel().getMassagedPlacemarks());
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        updateProgressBarVisibility(View.GONE);
        BluePlaquesLondonApplication.bus.unregister(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final FragmentManager fm = getFragmentManager();
        updateProgressBarVisibility(View.GONE);
        switch (item.getItemId()) {
            case R.id.action_about:
                final AboutFragment aboutFragment = new AboutFragment();
                aboutFragment.show(fm, "fragment_about");
                break;
            case R.id.action_settings:
                final SettingsFragment settingsFragment = new SettingsFragment();
                settingsFragment.show(fm, "fragment_settings");
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar = (ProgressBar) findViewById(R.id.map_progress_bar);
        BluePlaquesLondonApplication.bus.register(this);
        checkForGooglePlayServicesAvailability();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateProgressBarVisibility(View.GONE);
        switch (requestCode) {
            case BluePlaquesLondonApplication.CONNECTION_FAILURE_RESOLUTION_REQUEST:
            case BluePlaquesLondonApplication.CONNECTION_FAILURE_NO_RESOLUTION_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.d(TAG, "User downloaded the correct version of Google Play Service after being prompted");
                    }
                    break;
                    default: {
                        Log.e(TAG, "Tried to request the user to download the correct version of Google Play Services but it failed");
                        final Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, GOOGLE_PLAY_SERVICES_REQUEST);
                        dialog.setCancelable(false);
                        dialog.show();
                    }
                    break;
                }
            }
            case GOOGLE_PLAY_SERVICES_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.d(TAG, "User downloaded the correct version of Google Play Service after being prompted the second time");
                    }
                    break;
                    default: {
                        Log.e(TAG, "Tried to request the user to download the correct version of Google Play Services but it failed");
                    }
                    break;
                }
            }
            default: {
                Log.w(TAG, "onActivityResult invoked with an unexpected requestCode");
            }
            break;
        }
    }

    @Subscribe
    public void onPlacemarkSelected(Placemark placemark) {
        searchView.setQuery("", false);
        searchView.setIconified(true);
        searchView.clearFocus();
    }

    public void updateProgressBarVisibility(int visibility) {
        if (progressBar != null) {
            progressBar.setVisibility(visibility);
        }
    }

    private MapFragment getMapFragment() {
        return (MapFragment) getFragmentManager().findFragmentById(R.id.map);
    }

    private void checkForGooglePlayServicesAvailability() {
        final int playServicesAvailable = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        switch (playServicesAvailable) {
            case ConnectionResult.SUCCESS: {
                Log.d(TAG, "Successfully connected to Google Play Services");
            }
            break;
            default: {
                boolean isRecoverable = true;
                updateProgressBarVisibility(View.GONE);
                if (GooglePlayServicesUtil.isUserRecoverableError(playServicesAvailable)) {
                    GooglePlayServicesUtil.showErrorDialogFragment(playServicesAvailable, this, BluePlaquesLondonApplication.CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } else {
                    isRecoverable = false;
                    GooglePlayServicesUtil.showErrorDialogFragment(playServicesAvailable, this, BluePlaquesLondonApplication.CONNECTION_FAILURE_NO_RESOLUTION_REQUEST);
                }
                final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
                app.trackEvent(BluePlaquesConstants.ERROR_CATEGORY, BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT, isRecoverable ? BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT_RECOVERABLE : BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT_UNRECOVERABLE);
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
                        final String event = analyticsStringForButtonPress(which);
                        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
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