// Copyright (c) 2014 - 2025 Upwards Northwards Software Limited
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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.fragments.AboutFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.SettingsFragment;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;
import com.upwardsnorthwards.blueplaqueslondon.utils.InternetConnectivityHelper;
import com.upwardsnorthwards.blueplaqueslondon.utils.InternetConnectivityHelperDelegate;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;

public class MainActivity extends AppCompatActivity implements InternetConnectivityHelperDelegate {

    private static final String TAG = "MainActivity";
    private static final int GOOGLE_PLAY_SERVICES_REQUEST = 9002;

    private ProgressBar progressBar;
    private InternetConnectivityHelper internetConnectivityHelper;
    private NavController navController;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        initialiseAppRating();
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        updateProgressBarVisibility(View.GONE);
        int itemId = item.getItemId();
        if (itemId == R.id.action_about) {
            navController.navigate(R.id.about_fragment);
        } else if (itemId == R.id.action_settings) {
            navController.navigate(R.id.settings_fragment);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForInternetConnectivity();
        progressBar = findViewById(R.id.map_progress_bar);
        BluePlaquesLondonApplication.bus.register(this);
        checkForGooglePlayServicesAvailability();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (internetConnectivityHelper != null) {
            internetConnectivityHelper.onPause();
        }
        updateProgressBarVisibility(View.GONE);
        BluePlaquesLondonApplication.bus.unregister(this);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        updateProgressBarVisibility(View.GONE);
        if (requestCode == GOOGLE_PLAY_SERVICES_REQUEST) {
            if (resultCode != RESULT_OK) {
                Log.e(TAG, "Tried to request the user to download the correct version of Google Play Services but it failed");
            }
        }
    }

    @SuppressWarnings({"unused", "UnusedParameters"})
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlacemarkSelected(final Placemark placemark) {
        // Handle placemark selection
    }

    public void updateProgressBarVisibility(final int visibility) {
        if (progressBar != null) {
            progressBar.setVisibility(visibility);
        }
    }

    private void checkForGooglePlayServicesAvailability() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int playServicesAvailable = googleAPI.isGooglePlayServicesAvailable(this);
        if (playServicesAvailable != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(playServicesAvailable)) {
                googleAPI.getErrorDialog(this, playServicesAvailable, GOOGLE_PLAY_SERVICES_REQUEST).show();
            } else {
                Log.e(TAG, "Unrecoverable Google Play Services error");
            }
        }
    }

    private void initialiseAppRating() {
        AppRate.with(this)
                .setInstallDays(10)
                .setLaunchTimes(10)
                .setRemindInterval(1)
                .setOnClickButtonListener(which -> {
                    // Handle button clicks
                })
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private void registerForInternetConnectivity() {
        internetConnectivityHelper = new InternetConnectivityHelper(this);
        internetConnectivityHelper.setDelegate(this);
        internetConnectivityHelper.onResume();
    }

    @Override
    public void internetConnectivityUpdated(boolean hasInternetConnectivity) {
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            BluePlaquesMapFragment mapFragment = (BluePlaquesMapFragment) navHostFragment.getChildFragmentManager().getPrimaryNavigationFragment();
            if (mapFragment != null) {
                mapFragment.internetConnectivityUpdated(hasInternetConnectivity);
            }
        }
        if (!hasInternetConnectivity) {
            internetConnectivityHelper.showConnectivityToast();
        }
    }

    @Override
    public void lostInternetConnectivity() {
        Log.v(TAG, "Lost Internet Connectivity");
    }

    @Override
    public void regainedInternetConnectivity() {
        Log.v(TAG, "Regained Internet Connectivity");
    }
}
