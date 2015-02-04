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

package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
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

public class MapFragment extends com.google.android.gms.maps.MapFragment
        implements OnCameraChangeListener, OnMarkerClickListener,
        OnInfoWindowClickListener {

    private static final String TAG = "MapFragment";

    private GoogleMap googleMap;
    private MapModel model;
    private List<KeyedMarker> markers = new ArrayList<KeyedMarker>();
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

    @Subscribe
    public void onPlacemarkSelected(Placemark placemark) {
        if (placemark.getName() == getString(R.string.closest)) {
            final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity().getApplication();
            final Location currentLocation = app.getCurrentLocation();
            if (currentLocation != null) {
                placemark = model.getPlacemarkClosestToPlacemark(currentLocation);
            }
        }
        if (placemark != null) {
            final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity().getApplication();
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY, BluePlaquesConstants.TABLE_ROW_PRESSED_EVENT, placemark.getName());
            navigateToPlacemark(placemark);
        }
    }

    protected void mapReady(final GoogleMap map) {
        googleMap = map;
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.map)).getMap();
        }
        if (googleMap != null) {
            // a few settings
            googleMap.setIndoorEnabled(false);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.setMyLocationEnabled(true);
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
        final LatLng lastKnownCoordinate = BluePlaquesSharedPreferences
                .getLastKnownBPLCoordinate(getActivity());
        MapsInitializer.initialize(getActivity());
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                lastKnownCoordinate,
                BluePlaquesSharedPreferences.getMapZoom(getActivity())));
        setProgressBarVisibility(View.GONE);
    }

    private void addPlacemarksToMap() {
        // first of all, check to see whether the placemarks have already been added to the map
        // no need to iterate through this twice just because onResume was called on the fragment
        if (model.getMassagedPlacemarks() != null && model.getMassagedPlacemarks().size() > 0 && markers != null && markers.size() > 0) {
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
    public void onCameraChange(final CameraPosition position) {
        BluePlaquesSharedPreferences.saveLastKnownCoordinate(getActivity(),
                position.target);
        BluePlaquesSharedPreferences.saveMapZoom(getActivity(), googleMap,
                position.zoom);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        final LatLng latLng = marker.getPosition();
        final String key = Placemark.keyFromLatLng(latLng.latitude, latLng.longitude);
        final Integer location = model.getParser().getKeyToArrayPositions().get(key).get(0);
        final Placemark placemark = model.getParser().getPlacemarks().get(location);
        marker.setTitle(placemark.getTrimmedName());
        marker.setSnippet(getSnippetForPlacemark(placemark, true));
        BluePlaquesSharedPreferences.saveLastKnownBPLCoordinate(getActivity(),
                latLng);
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity()
                .getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.MARKER_PRESSED_EVENT, marker.getTitle());
        return false;
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {
        final Intent intent = new Intent(getActivity(), MapDetailActivity.class);
        intent.putParcelableArrayListExtra(
                BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                getListOfPlacemarksForMarker(marker));
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity()
                .getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.MARKER_INFO_WINDOW_PRESSED_EVENT,
                marker.getTitle());
        startActivity(intent);
    }

    public void navigateToPlacemark(final Placemark placemark) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        placemark.getLatitude(), placemark.getLongitude()),
                BluePlaquesSharedPreferences.getMapZoom(getActivity())));
        final LatLng latLng = new LatLng(placemark.getLatitude(),
                placemark.getLongitude());
        BluePlaquesSharedPreferences.saveLastKnownBPLCoordinate(getActivity(),
                latLng);
        for (final KeyedMarker keyedMarker : markers) {
            if (placemark.key().equals(keyedMarker.getKey())) {
                keyedMarker.getMarker().showInfoWindow();
                break;
            }
        }
    }

    private String getSnippetForPlacemark(final Placemark placemark, final boolean trimmed) {
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

    private ArrayList<Placemark> getListOfPlacemarksForMarker(final Marker marker) {
        ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
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

    protected void onPlaquesParsed() {
        task = null;
        setupMap();
    }

    public MapModel getModel() {
        return model;
    }

    public void setModel(final MapModel model) {
        this.model = model;
    }

    private class ParsePlaquesTask extends AsyncTask<Void, Void, Void> {

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
