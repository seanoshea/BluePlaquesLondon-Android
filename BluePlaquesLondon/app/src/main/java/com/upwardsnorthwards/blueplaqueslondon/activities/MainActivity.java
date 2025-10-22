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

package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.gms.tasks.Task;
import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.data.preferences.AppPreferencesDataStore;
import com.upwardsnorthwards.blueplaqueslondon.fragments.AboutFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.LocationViewModel;
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MainViewModel;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;
import com.upwardsnorthwards.blueplaqueslondon.utils.InternetConnectivityHelper;
import com.upwardsnorthwards.blueplaqueslondon.utils.InternetConnectivityHelperDelegate;
import com.upwardsnorthwards.blueplaqueslondon.views.ArrayAdapterSearchView;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

/**
 * Landing activity for the application. Includes a reference to the <code>BluePlaquesMapFragment</code>
 */
@AndroidEntryPoint
public class MainActivity extends AppCompatActivity implements InternetConnectivityHelperDelegate {

    private static final String TAG = "MainActivity";
    private static final int GOOGLE_PLAY_SERVICES_REQUEST = 9002;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private static final int CONNECTION_FAILURE_NO_RESOLUTION_REQUEST = 9001;
    private ArrayAdapterSearchView searchView;
    private ProgressBar progressBar;
    private InternetConnectivityHelper internetConnectivityHelper;
    private MainViewModel mainViewModel;
    private LocationViewModel locationViewModel;
    private NavController navController;

    @Inject
    AppPreferencesDataStore preferencesDataStore;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize ViewModels
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        // Observe ViewModel LiveData
        observeViewModel();
        observeLocationViewModel();

        // Load plaques
        mainViewModel.loadPlaques();

        // Initialize NavController for Navigation Component (deferred to ensure View is ready)
        // NavController will be initialized lazily when first needed

        initialiseAppRating();
    }

    /**
     * Observe LocationViewModel LiveData.
     */
    private void observeLocationViewModel() {
        locationViewModel.getClosestPlaque().observe(this, closestPlaque -> {
            if (closestPlaque != null) {
                // Select the closest plaque in the main view model
                mainViewModel.selectPlaque(closestPlaque);
                // Notify the map fragment
                BluePlaquesMapFragment mapFragment = getMapFragment();
                if (mapFragment != null) {
                    mapFragment.onPlacemarkSelected(closestPlaque);
                }
            }
        });

        locationViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Location error: " + error);
            }
        });
    }

    /**
     * Observe MainViewModel LiveData.
     */
    private void observeViewModel() {
        mainViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading != null && isLoading) {
                updateProgressBarVisibility(View.VISIBLE);
            } else {
                updateProgressBarVisibility(View.GONE);
            }
        });

        mainViewModel.getError().observe(this, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
                // You can show a Toast or Snackbar here if needed
            }
        });

        mainViewModel.getPlaques().observe(this, plaques -> {
            if (plaques != null && searchView != null) {
                searchView.notifyAdapterOfPlacemarks(plaques);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull final Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        final SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (ArrayAdapterSearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Populate search view with plaques from ViewModel
        if (mainViewModel.getPlaques().getValue() != null) {
            searchView.notifyAdapterOfPlacemarks(mainViewModel.getPlaques().getValue());
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull final MenuItem item) {
        updateProgressBarVisibility(View.GONE);
        int id = item.getItemId();
        NavController controller = getNavController();
        if (controller == null) {
            Log.w(TAG, "NavController not available");
            return false;
        }
        if (id == R.id.action_about) {
            controller.navigate(R.id.action_mapFragment_to_aboutFragment);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerForInternetConnectivity();
        progressBar = (ProgressBar) findViewById(R.id.map_progress_bar);
        checkForGooglePlayServicesAvailability();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (internetConnectivityHelper != null) {
            internetConnectivityHelper.onPause();
        }
        updateProgressBarVisibility(View.GONE);
    }

    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        updateProgressBarVisibility(View.GONE);
        switch (requestCode) {
            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            case CONNECTION_FAILURE_NO_RESOLUTION_REQUEST: {
                switch (resultCode) {
                    case Activity.RESULT_OK: {
                        Log.d(TAG, "User downloaded the correct version of Google Play Service after being prompted");
                    }
                    break;
                    default: {
                        Log.e(TAG, "Tried to request the user to download the correct version of Google Play Services but it failed");
                        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
                        final Dialog dialog = googleAPI.getErrorDialog(this, GOOGLE_PLAY_SERVICES_REQUEST, resultCode);
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

    // Called by BluePlaquesMapFragment when a placemark is selected
    public void onPlacemarkSelected(final Placemark placemark) {
        if (searchView != null) {
            searchView.setQuery("", false);
            searchView.setIconified(true);
            searchView.clearFocus();
        }
    }

    /**
     * Toggles the visibility of the progress bar which is shown while we wait for the map to load
     * and for the application to fully parse all the blue plaques.
     *
     * @param visibility either View.GONE or View.VISIBLE.
     */
    public void updateProgressBarVisibility(final int visibility) {
        if (progressBar != null) {
            progressBar.setVisibility(visibility);
        }
    }

    @NonNull
    private BluePlaquesMapFragment getMapFragment() {
        // Get the NavHostFragment and retrieve the map fragment from it
        try {
            androidx.fragment.app.Fragment navHostFragment = getSupportFragmentManager()
                    .findFragmentById(R.id.nav_host_fragment);
            if (navHostFragment != null) {
                androidx.fragment.app.Fragment mapFragment = navHostFragment.getChildFragmentManager()
                        .getPrimaryNavigationFragment();
                if (mapFragment != null && mapFragment.getClass().getSimpleName().equals("BluePlaquesMapFragment")) {
                    return (BluePlaquesMapFragment) (Object) mapFragment;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error getting map fragment", e);
        }
        return new BluePlaquesMapFragment();
    }

    /**
     * Get the LocationViewModel for use by fragments.
     */
    @NonNull
    public LocationViewModel getLocationViewModel() {
        return locationViewModel;
    }

    /**
     * Get the AppPreferencesDataStore for use by fragments.
     */
    @NonNull
    public AppPreferencesDataStore getPreferencesDataStore() {
        return preferencesDataStore;
    }

    /**
     * Before showing the map, we need to make sure that the user has the correct version of Google Play Services installed.
     * If they do, the user is shown the map and they can continue to use the application. Otherwise, they are prompted to
     * update their version of Google Play Services on the Play Store.
     */
    private void checkForGooglePlayServicesAvailability() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        final int playServicesAvailable = googleAPI.isGooglePlayServicesAvailable(this);
        switch (playServicesAvailable) {
            case ConnectionResult.SUCCESS: {
                Log.d(TAG, "Successfully connected to Google Play Services");
            }
            break;
            default: {
                boolean isRecoverable = true;
                updateProgressBarVisibility(View.GONE);
                if (googleAPI.isUserResolvableError(playServicesAvailable)) {
                    googleAPI.showErrorDialogFragment(this, playServicesAvailable, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } else {
                    isRecoverable = false;
                    googleAPI.showErrorDialogFragment(this, playServicesAvailable, CONNECTION_FAILURE_NO_RESOLUTION_REQUEST);
                }
                final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
                app.trackEvent(BluePlaquesConstants.ERROR_CATEGORY, BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT, isRecoverable ? BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT_RECOVERABLE : BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT_UNRECOVERABLE);
            }
        }
    }

    /**
     * Users are prompted to rate the application using Google Play In-App Review API.
     * This is triggered based on launch count stored in DataStore.
     */
    private void initialiseAppRating() {
        // Increment launch count and check if we should show review
        preferencesDataStore.incrementLaunchCount()
                .flatMap(prefs -> preferencesDataStore.getLaunchCountSingle())
                .flatMap(launchCount -> {
                    if (launchCount >= 10) {
                        return preferencesDataStore.hasCompletedReviewSingle()
                                .map(hasCompleted -> !hasCompleted);
                    }
                    return io.reactivex.rxjava3.core.Single.just(false);
                })
                .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                .subscribe(
                        shouldShowReview -> {
                            if (shouldShowReview) {
                                showInAppReview();
                            }
                        },
                        error -> Log.e(TAG, "Error initializing app rating: " + error.getMessage())
                );
    }

    /**
     * Shows the Google Play In-App Review dialog.
     */
    private void showInAppReview() {
        ReviewManager reviewManager = ReviewManagerFactory.create(this);
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ReviewInfo reviewInfo = task.getResult();
                Task<Void> flow = reviewManager.launchReviewFlow(this, reviewInfo);
                flow.addOnCompleteListener(reviewTask -> {
                    // Mark as completed regardless of whether user reviewed
                    preferencesDataStore.setCompletedReview(true)
                            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                            .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(
                                    prefs -> {
                                        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
                                        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                                                BluePlaquesConstants.RATE_APP_BUTTON_PRESSED_EVENT,
                                                "In-App Review Shown");
                                    },
                                    error -> Log.e(TAG, "Error saving review completion: " + error.getMessage())
                            );
                });
            }
        });
    }

    private void registerForInternetConnectivity() {
        internetConnectivityHelper = new InternetConnectivityHelper(this);
        internetConnectivityHelper.setDelegate(this);
        internetConnectivityHelper.onResume();
    }

    @Override
    public void internetConnectivityUpdated(boolean hasInternetConnectivity) {
        BluePlaquesMapFragment mapFragment = getMapFragment();
        if (mapFragment != null) {
            mapFragment.internetConnectivityUpdated(hasInternetConnectivity);
        } else {
            Log.v(TAG, "Tried to communicate the current internet connectivity state to the map fragment, but it was null");
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

    /**
     * Get the NavController, initializing it lazily if needed.
     */
    private NavController getNavController() {
        if (navController == null) {
            try {
                // Ensure the view hierarchy is ready before finding NavController
                androidx.fragment.app.Fragment navHostFragment = getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);
                if (navHostFragment != null) {
                    navController = Navigation.findNavController(this, R.id.nav_host_fragment);
                } else {
                    Log.w(TAG, "NavHostFragment not found, deferring NavController initialization");
                    return null;
                }
            } catch (IllegalStateException e) {
                Log.w(TAG, "Unable to find NavController, view hierarchy may not be ready", e);
                return null;
            }
        }
        return navController;
    }
}