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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.otto.Subscribe;
import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity;
import com.upwardsnorthwards.blueplaqueslondon.activities.MapDetailActivity;
import com.upwardsnorthwards.blueplaqueslondon.model.KeyedMarker;
import com.upwardsnorthwards.blueplaqueslondon.model.MapModel;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesSharedPreferences;

import java.util.ArrayList;
import java.util.List;

/**
 * Main fragment in the application. Shows the plaques on a <code>com.google.android.gms.maps.MapFragment</code>
 */
public class BluePlaquesMapFragment extends MapFragment implements OnCameraChangeListener, OnMarkerClickListener, OnInfoWindowClickListener {

    private static final String TAG = "MapFragment";
    @NonNull
    private final List<KeyedMarker> markers = new ArrayList<>();
    private GoogleMap googleMap;
    private MapModel model;
    @Nullable
    private AsyncTask<Void, Void, Void> task;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkForModel();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkForModel();
        BluePlaquesLondonApplication.bus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (task != null) {
            task.cancel(true);
        }
        BluePlaquesLondonApplication.bus.unregister(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = BluePlaquesLondonApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onPlacemarkSelected(@NonNull Placemark placemark) {
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity().getApplication();
        if (placemark.getName().equals(getString(R.string.closest))) {
            final Location currentLocation = app.getCurrentLocation();
            if (currentLocation != null) {
                placemark = model.getPlacemarkClosestToPlacemark(currentLocation);
            }
        }
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY, BluePlaquesConstants.TABLE_ROW_PRESSED_EVENT, placemark.getName());
        navigateToPlacemark(placemark);
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
        if (activity != null) {
            final LatLng lastKnownCoordinate = BluePlaquesSharedPreferences
                    .getLastKnownBPLCoordinate(activity);
            MapsInitializer.initialize(activity);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    lastKnownCoordinate,
                    BluePlaquesSharedPreferences.getMapZoom(activity)));
            setProgressBarVisibility(View.GONE);
        }
    }

    private void addPlacemarksToMap() {
        // first of all, check to see whether the placemarks have already been added to the map
        // no need to iterate through this twice just because onResume was called on the fragment
        if (model.getMassagedPlacemarks() != null && model.getMassagedPlacemarks().size() > 0 && markers.size() > 0) {
            Log.v(TAG, "No point in recreating the placemarks as they are already set on the map");
        } else {
            Log.v(TAG, "Creating the placemarks for the map");
            for (final Placemark placemark : model.getMassagedPlacemarks()) {
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
        if (activity != null) {
            BluePlaquesSharedPreferences.saveLastKnownCoordinate(activity,
                    position.target);
            BluePlaquesSharedPreferences.saveMapZoom(activity, googleMap,
                    position.zoom);
        } else {
            Log.v(TAG, "Tried saving the coordinates after a camera change, but the fragment returned null for getActivity");
        }
    }

    @Override
    public boolean onMarkerClick(@NonNull final Marker marker) {
        final LatLng latLng = marker.getPosition();
        final String key = Placemark.keyFromLatLng(latLng.latitude, latLng.longitude);
        final Integer location = model.getParser().getKeyToArrayPositions().get(key).get(0);
        final Placemark placemark = model.getParser().getPlacemarks().get(location);
        final Activity activity = getActivity();
        if (activity != null) {
            final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) activity
                    .getApplication();
            marker.setTitle(placemark.getTrimmedName());
            marker.setSnippet(getSnippetForPlacemark(placemark, true));
            BluePlaquesSharedPreferences.saveLastKnownBPLCoordinate(activity,
                    latLng);
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                    BluePlaquesConstants.MARKER_PRESSED_EVENT, marker.getTitle());
        }
        return false;
    }

    @Override
    public void onInfoWindowClick(@NonNull final Marker marker) {
        final Activity activity = getActivity();
        if (activity != null) {
            final Intent intent = new Intent(activity, MapDetailActivity.class);
            intent.putParcelableArrayListExtra(
                    BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                    getListOfPlacemarksForMarker(marker));
            final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) activity
                    .getApplication();
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                    BluePlaquesConstants.MARKER_INFO_WINDOW_PRESSED_EVENT,
                    marker.getTitle());
            startActivity(intent);
        }
    }

    private void navigateToPlacemark(@NonNull final Placemark placemark) {
        final Activity activity = getActivity();
        if (activity != null) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                            placemark.getLatitude(), placemark.getLongitude()),
                    BluePlaquesSharedPreferences.getMapZoom(activity)));
            final LatLng latLng = new LatLng(placemark.getLatitude(),
                    placemark.getLongitude());
            BluePlaquesSharedPreferences.saveLastKnownBPLCoordinate(activity,
                    latLng);
            for (final KeyedMarker keyedMarker : markers) {
                if (placemark.key().equals(keyedMarker.getKey())) {
                    Marker marker = keyedMarker.getMarker();
                    marker.setTitle(placemark.getTrimmedName());
                    marker.setSnippet(getSnippetForPlacemark(placemark, true));
                    marker.showInfoWindow();
                    break;
                }
            }
        }
    }

    private String getSnippetForPlacemark(@NonNull final Placemark placemark, final boolean trimmed) {
        final String snippet;
        final List<Integer> numberOfPlacemarksAssociatedWithPlacemark = model
                .getParser().getKeyToArrayPositions().get(placemark.key());
        if (numberOfPlacemarksAssociatedWithPlacemark.size() == 1) {
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
        ArrayList<Placemark> placemarks = new ArrayList<>();
        for (final KeyedMarker keyedMarker : markers) {
            if (keyedMarker.getMarker().equals(marker)) {
                final List<Integer> numberOfPlacemarksAssociatedWithPlacemark = model
                        .getParser().getKeyToArrayPositions()
                        .get(keyedMarker.getKey());
                placemarks = model
                        .getPlacemarksAtIndices(numberOfPlacemarksAssociatedWithPlacemark);
                break;
            }
        }
        return placemarks;
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
