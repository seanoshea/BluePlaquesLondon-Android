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

package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity;
import com.upwardsnorthwards.blueplaqueslondon.activities.MapDetailActivity;
import com.upwardsnorthwards.blueplaqueslondon.data.preferences.AppPreferencesDataStore;
import com.upwardsnorthwards.blueplaqueslondon.model.KeyedMarker;
import com.upwardsnorthwards.blueplaqueslondon.model.MapModel;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.LocationViewModel;
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MainViewModel;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesKMLParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main fragment in the application. Shows the plaques on a <code>com.google.android.gms.maps.SupportMapFragment</code>
 * Note: This fragment cannot use @AndroidEntryPoint because it extends SupportMapFragment.
 * The ViewModel is obtained from the parent activity instead.
 */
public class BluePlaquesMapFragment extends SupportMapFragment implements OnCameraChangeListener, OnMarkerClickListener, OnInfoWindowClickListener {

    private static final String TAG = "MapFragment";
    @NonNull
    private final List<KeyedMarker> markers = new ArrayList<>();
    private GoogleMap googleMap;
    private MapModel model;
    @Nullable
    private AsyncTask<Void, Void, Void> task;
    private MainViewModel mainViewModel;
    private LocationViewModel locationViewModel;
    private AppPreferencesDataStore preferencesDataStore;
    private List<Placemark> placemarks = new ArrayList<>();
    // Maps placemark keys to their array positions (to support multiple plaques at same location)
    private Map<String, List<Integer>> keyToArrayPositions = new HashMap<>();

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModels and DataStore from parent activity
        if (getActivity() instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainViewModel = new ViewModelProvider(mainActivity).get(MainViewModel.class);
            locationViewModel = mainActivity.getLocationViewModel();
            preferencesDataStore = mainActivity.getPreferencesDataStore();
            observeViewModel(mainActivity);

            // Check and update location permission state
            if (ContextCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                locationViewModel.setLocationPermissionGranted(true);
            }
        }

        checkForModel();
    }

    /**
     * Observe MainViewModel LiveData.
     */
    private void observeViewModel(@NonNull MainActivity mainActivity) {
        mainViewModel.getPlaques().observe(mainActivity, plaquesFromDb -> {
            if (plaquesFromDb != null) {
                placemarks = plaquesFromDb;
                buildKeyToArrayPositionsMap();
                setupMap();
            }
        });

        mainViewModel.getLoading().observe(mainActivity, isLoading -> {
            if (isLoading != null && isLoading) {
                setProgressBarVisibility(View.VISIBLE);
            } else {
                setProgressBarVisibility(View.GONE);
            }
        });

        mainViewModel.getError().observe(mainActivity, error -> {
            if (error != null && !error.isEmpty()) {
                Log.e(TAG, "Error: " + error);
            }
        });
    }

    /**
     * Build the key-to-array-positions map to support multiple plaques at the same location.
     */
    private void buildKeyToArrayPositionsMap() {
        keyToArrayPositions.clear();
        for (int i = 0; i < placemarks.size(); i++) {
            Placemark placemark = placemarks.get(i);
            String key = placemark.key();
            if (!keyToArrayPositions.containsKey(key)) {
                keyToArrayPositions.put(key, new ArrayList<>());
            }
            keyToArrayPositions.get(key).add(i);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForModel();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(true);
        }
    }

    // Called from ArrayAdapterSearchView when a placemark is selected
    public void onPlacemarkSelected(@NonNull Placemark placemark) {
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity().getApplication();
        if (placemark.getName().equals(getString(R.string.closest))) {
            // Find closest plaque using LocationViewModel
            if (locationViewModel != null) {
                // Request current location first
                locationViewModel.requestCurrentLocation();
                // Then find closest plaque once location is available
                locationViewModel.getCurrentLocation().observe((MainActivity) getActivity(), location -> {
                    if (location != null && placemarks != null && !placemarks.isEmpty()) {
                        locationViewModel.findClosestPlaque(placemarks);
                    }
                });
            } else {
                Log.w(TAG, "LocationViewModel not available");
            }
        } else {
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY, BluePlaquesConstants.TABLE_ROW_PRESSED_EVENT, placemark.getName());
            navigateToPlacemark(placemark);
        }
    }

    @SuppressWarnings("UnusedParameters")
    public void internetConnectivityUpdated(boolean hasInternetConnectivity) {
        setupMap();
    }

    private void mapReady(final GoogleMap map) {
        googleMap = map;
        if (googleMap != null) {
            // a few settings
            googleMap.setIndoorEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                googleMap.setMyLocationEnabled(true);
            }
            // listen for events
            googleMap.setOnCameraChangeListener(this);
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnInfoWindowClickListener(this);
            mapConfigured();
        }
    }

    private void checkForModel() {
        if (model == null) {
            setProgressBarVisibility(View.VISIBLE);
            model = new MapModel();
            task = new ParsePlaquesTask().execute();
        } else if (googleMap == null || model.getMassagedPlacemarks() == null || model.getMassagedPlacemarks().size() <= 0) {
            setProgressBarVisibility(View.VISIBLE);
        }
    }

    private void setupMap() {
        if (googleMap == null) {
            Log.v(TAG, "Retrieving the map from the fragment before setup can complete");
            getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(final GoogleMap googleMap) {
                    mapReady(googleMap);
                }
            });
        } else {
            Log.v(TAG, "Map previously retrieved from the fragment. Proceeding to configuring the map");
            mapConfigured();
        }
    }

    private void mapConfigured() {
        addPlacemarksToMap();
        final Activity activity = getActivity();
        if (activity != null && preferencesDataStore != null) {
            MapsInitializer.initialize(activity);
            // Get last known coordinate and zoom from DataStore
            io.reactivex.rxjava3.core.Single.zip(
                    preferencesDataStore.getLastKnownBPLCoordinateSingle(),
                    preferencesDataStore.getMapZoomSingle(),
                    (coordinate, zoom) -> new Object[]{coordinate, zoom}
            ).subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
            .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
            .subscribe(
                    result -> {
                        LatLng coordinate = (LatLng) result[0];
                        Float zoom = (Float) result[1];
                        if (googleMap != null) {
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate, zoom));
                        }
                        setProgressBarVisibility(View.GONE);
                    },
                    error -> {
                        Log.e(TAG, "Error loading map preferences: " + error.getMessage());
                        setProgressBarVisibility(View.GONE);
                    }
            );
        }
    }

    private void addPlacemarksToMap() {
        // first of all, check to see whether the placemarks have already been added to the map
        // no need to iterate through this twice just because onResume was called on the fragment
        if (placemarks != null && placemarks.size() > 0 && markers.size() > 0) {
            Log.v(TAG, "No point in recreating the placemarks as they are already set on the map");
        } else if (placemarks != null && placemarks.size() > 0) {
            Log.v(TAG, "Creating the placemarks for the map");
            for (final Placemark placemark : placemarks) {
                int iconResource = R.drawable.blue;
                if (!placemark.getStyleUrl().equalsIgnoreCase("#myDefaultStyles")) {
                    iconResource = R.drawable.green;
                }
                final Marker marker = googleMap.addMarker(new MarkerOptions()
                        .position(
                                new LatLng(placemark.getLatitude(), placemark
                                        .getLongitude()))
                        .title(placemark.getName())
                        .snippet(getSnippetForPlacemark(placemark, false))
                        .icon(BitmapDescriptorFactory.fromResource(iconResource)));
                final KeyedMarker keyedMarker = new KeyedMarker();
                keyedMarker.setKey(placemark.key());
                keyedMarker.setMarker(marker);
                markers.add(keyedMarker);
            }
        }
    }

    @Override
    public void onCameraChange(@NonNull final CameraPosition position) {
        final Activity activity = getActivity();
        if (activity != null && preferencesDataStore != null && googleMap != null) {
            // Save last known coordinate
            preferencesDataStore.saveLastKnownCoordinate(position.target)
                    .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(
                            prefs -> Log.v(TAG, "Saved last known coordinate"),
                            error -> Log.e(TAG, "Error saving coordinate: " + error.getMessage())
                    );

            // Save map zoom
            preferencesDataStore.saveMapZoom(position.zoom, googleMap.getMinZoomLevel(), googleMap.getMaxZoomLevel())
                    .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(
                            prefs -> Log.v(TAG, "Saved map zoom"),
                            error -> Log.e(TAG, "Error saving zoom: " + error.getMessage())
                    );
        } else {
            Log.v(TAG, "Tried saving the coordinates after a camera change, but dependencies are not available");
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull final Marker marker) {
        final LatLng latLng = marker.getPosition();
        final String key = Placemark.keyFromLatLng(latLng.latitude, latLng.longitude);
        final List<Integer> locations = keyToArrayPositions.get(key);
        if (locations != null && locations.size() > 0) {
            final Integer location = locations.get(0);
            final Placemark placemark = placemarks.get(location);
            final Activity activity = getActivity();
            if (activity != null) {
                final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) activity
                        .getApplication();
                marker.setTitle(placemark.getTrimmedName());
                marker.setSnippet(getSnippetForPlacemark(placemark, true));

                // Save last known BPL coordinate using DataStore
                if (preferencesDataStore != null) {
                    preferencesDataStore.saveLastKnownBPLCoordinate(latLng)
                            .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                            .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                            .subscribe(
                                    prefs -> Log.v(TAG, "Saved BPL coordinate"),
                                    error -> Log.e(TAG, "Error saving BPL coordinate: " + error.getMessage())
                            );
                }

                app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                        BluePlaquesConstants.MARKER_PRESSED_EVENT, marker.getTitle());
            }
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull final Marker marker) {
        final Activity activity = getActivity();
        if (activity != null) {
            final Bundle args = new Bundle();
            args.putParcelableArrayList(
                    BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                    getListOfPlacemarksForMarker(marker));

            final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) activity
                    .getApplication();
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                    BluePlaquesConstants.MARKER_INFO_WINDOW_PRESSED_EVENT,
                    marker.getTitle());

            View view = getView();
            if (view != null) {
                androidx.navigation.NavController navController = androidx.navigation.Navigation.findNavController(view);
                navController.navigate(R.id.action_mapFragment_to_mapDetailFragment, args);
            }
        }
    }

    private void navigateToPlacemark(@NonNull final Placemark placemark) {
        final Activity activity = getActivity();
        if (activity != null && preferencesDataStore != null) {
            final LatLng latLng = new LatLng(placemark.getLatitude(), placemark.getLongitude());

            // Get map zoom and navigate
            preferencesDataStore.getMapZoomSingle()
                    .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                    .subscribe(
                            zoom -> {
                                if (googleMap != null) {
                                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
                                }

                                // Save last known BPL coordinate
                                preferencesDataStore.saveLastKnownBPLCoordinate(latLng)
                                        .subscribeOn(io.reactivex.rxjava3.schedulers.Schedulers.io())
                                        .observeOn(io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread())
                                        .subscribe(
                                                prefs -> Log.v(TAG, "Saved BPL coordinate"),
                                                error -> Log.e(TAG, "Error saving BPL coordinate: " + error.getMessage())
                                        );

                                // Show marker info window
                                for (final KeyedMarker keyedMarker : markers) {
                                    if (placemark.key().equals(keyedMarker.getKey())) {
                                        Marker marker = keyedMarker.getMarker();
                                        marker.setTitle(placemark.getTrimmedName());
                                        marker.setSnippet(getSnippetForPlacemark(placemark, true));
                                        marker.showInfoWindow();
                                        break;
                                    }
                                }
                            },
                            error -> Log.e(TAG, "Error loading zoom: " + error.getMessage())
                    );
        }
    }

    private String getSnippetForPlacemark(@NonNull final Placemark placemark, final boolean trimmed) {
        final String snippet;
        final List<Integer> numberOfPlacemarksAssociatedWithPlacemark = keyToArrayPositions.get(placemark.key());
        if (numberOfPlacemarksAssociatedWithPlacemark != null && numberOfPlacemarksAssociatedWithPlacemark.size() == 1) {
            if (trimmed) {
                snippet = placemark.getTrimmedOccupation();
            } else {
                snippet = placemark.getOccupation();
            }
        } else {
            snippet = getString(R.string.multiple_placemarks);
        }
        return snippet;
    }

    @NonNull
    private ArrayList<Placemark> getListOfPlacemarksForMarker(final Marker marker) {
        ArrayList<Placemark> result = new ArrayList<>();
        for (final KeyedMarker keyedMarker : markers) {
            if (keyedMarker.getMarker().equals(marker)) {
                final List<Integer> numberOfPlacemarksAssociatedWithPlacemark = keyToArrayPositions.get(keyedMarker.getKey());
                if (numberOfPlacemarksAssociatedWithPlacemark != null) {
                    for (Integer index : numberOfPlacemarksAssociatedWithPlacemark) {
                        if (index < placemarks.size()) {
                            result.add(placemarks.get(index));
                        }
                    }
                }
                break;
            }
        }
        return result;
    }

    private void setProgressBarVisibility(final int visibility) {
        final MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.updateProgressBarVisibility(visibility);
        }
    }

    private void onPlaquesParsed() {
        task = null;
        setupMap();
    }

    public MapModel getModel() {
        return model;
    }

    /**
     * Get placemarks for backward compatibility with search functionality.
     */
    public List<Placemark> getPlacemarks() {
        return placemarks;
    }

    /**
     * Asynchronously loads the plaques from the .xml file into an array of consumable Placemark objects.
     */
    private class ParsePlaquesTask extends AsyncTask<Void, Void, Void> {

        @Nullable
        @Override
        protected Void doInBackground(final Void... params) {
            model.loadMapData(getActivity());
            return null;
        }

        @Override
        protected void onPostExecute(final Void result) {
            super.onPostExecute(result);
            onPlaquesParsed();
        }
    }
}
