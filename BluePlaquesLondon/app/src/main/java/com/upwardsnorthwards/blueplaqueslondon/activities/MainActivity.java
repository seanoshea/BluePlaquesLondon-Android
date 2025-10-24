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
 * Main activity for the Blue Plaques London application.
 * 
 * <p>This activity serves as the primary entry point and navigation hub for the application.
 * It manages the map display, search functionality, location services, and user interactions
 * with blue plaques across London.</p>
 * 
 * <h3>Key Responsibilities:</h3>
 * <ul>
 *   <li>Hosts the {@link BluePlaquesMapFragment} for displaying blue plaques on Google Maps</li>
 *   <li>Manages search functionality through {@link ArrayAdapterSearchView}</li>
 *   <li>Coordinates location services via {@link LocationViewModel}</li>
 *   <li>Handles navigation between fragments using Navigation Component</li>
 *   <li>Monitors internet connectivity and provides user feedback</li>
 *   <li>Manages Google Play Services availability</li>
 *   <li>Implements in-app review prompts based on usage patterns</li>
 * </ul>
 * 
 * <h3>Architecture:</h3>
 * <p>Follows MVVM pattern with:</p>
 * <ul>
 *   <li>{@link MainViewModel} - Manages plaque data and UI state</li>
 *   <li>{@link LocationViewModel} - Handles location services and closest plaque detection</li>
 *   <li>{@link AppPreferencesDataStore} - Persists user preferences and app state</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Activity is launched automatically as the main launcher activity
 * // No direct instantiation required
 * }</pre>
 * 
 * @see BluePlaquesMapFragment
 * @see MainViewModel
 * @see LocationViewModel
 * @see InternetConnectivityHelperDelegate
 * 
 * @author Blue Plaques London Team
 * @since 1.0
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
     * Observes LocationViewModel LiveData for location updates and closest plaque detection.
     * 
     * <p>Sets up observers for:</p>
     * <ul>
     *   <li>Closest plaque updates - automatically selects and displays the nearest plaque</li>
     *   <li>Location errors - logs errors for debugging purposes</li>
     * </ul>
     * 
     * <p>When a closest plaque is found, it updates both the MainViewModel selection
     * and notifies the map fragment to highlight the plaque.</p>
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
     * Observes MainViewModel LiveData for UI state and data updates.
     * 
     * <p>Sets up observers for:</p>
     * <ul>
     *   <li>Loading state - shows/hides progress bar during data operations</li>
     *   <li>Error messages - logs errors for debugging and user feedback</li>
     *   <li>Plaques data - updates search view with available plaques</li>
     * </ul>
     * 
     * <p>This method ensures the UI stays synchronized with the ViewModel state
     * and provides appropriate user feedback during data loading operations.</p>
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

    /**
     * Handles placemark selection events from the map fragment.
     * 
     * <p>Called by {@link BluePlaquesMapFragment} when a user selects a blue plaque
     * on the map. This method clears and collapses the search view to provide
     * a clean user experience when viewing plaque details.</p>
     * 
     * @param placemark The selected {@link Placemark} object containing plaque information
     */
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
     * Provides access to the LocationViewModel for fragments and other components.
     * 
     * <p>The LocationViewModel manages location services, GPS permissions,
     * and closest plaque detection functionality. Fragments can use this
     * to access location-related data and operations.</p>
     * 
     * @return The activity-scoped {@link LocationViewModel} instance
     * @see LocationViewModel
     */
    @NonNull
    public LocationViewModel getLocationViewModel() {
        return locationViewModel;
    }

    /**
     * Provides access to the application preferences DataStore.
     * 
     * <p>The AppPreferencesDataStore handles persistent storage of user preferences,
     * app launch counts, review completion status, and other application state.
     * Fragments can use this to read and write user preferences.</p>
     * 
     * @return The injected {@link AppPreferencesDataStore} instance
     * @see AppPreferencesDataStore
     */
    @NonNull
    public AppPreferencesDataStore getPreferencesDataStore() {
        return preferencesDataStore;
    }

    /**
     * Verifies Google Play Services availability and prompts user for updates if needed.
     * 
     * <p>Google Maps functionality requires Google Play Services to be installed and up-to-date.
     * This method checks the current installation status and handles various scenarios:</p>
     * 
     * <ul>
     *   <li><strong>SUCCESS:</strong> Google Play Services is available and up-to-date</li>
     *   <li><strong>RECOVERABLE ERROR:</strong> User can update/install Google Play Services</li>
     *   <li><strong>UNRECOVERABLE ERROR:</strong> Device doesn't support Google Play Services</li>
     * </ul>
     * 
     * <p>Analytics events are tracked for error scenarios to monitor compatibility issues.</p>
     * 
     * @see GoogleApiAvailability
     * @see ConnectionResult
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
     * Initializes the in-app review system based on user engagement metrics.
     * 
     * <p>Uses Google Play In-App Review API to prompt users for app ratings at appropriate times.
     * The review prompt is triggered when:</p>
     * 
     * <ul>
     *   <li>User has launched the app 10 or more times</li>
     *   <li>User hasn't already completed a review</li>
     * </ul>
     * 
     * <p>This approach follows Google's best practices for in-app reviews by ensuring
     * users are engaged before requesting feedback. The system uses RxJava for
     * asynchronous preference checking and UI updates.</p>
     * 
     * @see ReviewManager
     * @see AppPreferencesDataStore
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
     * Displays the Google Play In-App Review dialog to the user.
     * 
     * <p>Initiates the Google Play In-App Review flow which allows users to rate
     * the app without leaving the application. The process involves:</p>
     * 
     * <ol>
     *   <li>Requesting a ReviewInfo object from Google Play</li>
     *   <li>Launching the review flow if the request succeeds</li>
     *   <li>Marking the review as completed regardless of user action</li>
     *   <li>Tracking analytics events for monitoring purposes</li>
     * </ol>
     * 
     * <p>The review completion is marked immediately to prevent repeated prompts,
     * following Google's recommendation to respect user choice.</p>
     * 
     * @see ReviewManager#requestReviewFlow()
     * @see ReviewManager#launchReviewFlow(Activity, ReviewInfo)
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
     * Retrieves the Navigation Component NavController with lazy initialization.
     * 
     * <p>The NavController is initialized lazily to avoid IllegalStateException
     * that can occur if accessed before the view hierarchy is fully established.
     * This method safely handles the initialization process and provides appropriate
     * error handling.</p>
     * 
     * <p><strong>Error Handling:</strong></p>
     * <ul>
     *   <li>Returns null if NavHostFragment is not found</li>
     *   <li>Returns null if view hierarchy is not ready</li>
     *   <li>Logs warnings for debugging purposes</li>
     * </ul>
     * 
     * @return The {@link NavController} instance, or null if not available
     * @see Navigation#findNavController(Activity, int)
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