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
import android.os.Bundle;
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
import com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity;
import com.upwardsnorthwards.blueplaqueslondon.activities.MapDetailActivity;
import com.upwardsnorthwards.blueplaqueslondon.R;
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

    private GoogleMap googleMap;
    private MapModel model;
    private List<KeyedMarker> markers = new ArrayList<KeyedMarker>();

    @Override
    public void onResume() {
        super.onResume();
        setProgressBarVisibility(View.VISIBLE);
        setupMap();
    }

    @Override
    public void onPause() {
        super.onPause();
        BluePlaquesLondonApplication.bus.unregister(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        model = new MapModel();
        loadMapData();
    }

    @Subscribe
    public void onPlacemarkSelected(Placemark placemark) {
        if (placemark.getName() == getString(R.string.closest)) {
            BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity().getApplication();
            Location currentLocation = app.getCurrentLocation();
            if (currentLocation != null) {
                placemark = model.getPlacemarkClosestToPlacemark(currentLocation);
            }
        }
        if (placemark != null) {
            BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity().getApplication();
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY, BluePlaquesConstants.TABLE_ROW_PRESSED_EVENT, placemark.getName());
            navigateToPlacemark(placemark);
        }
    }

    protected void mapReady(GoogleMap map) {
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
            addPlacemarksToMap();
            LatLng lastKnownCoordinate = BluePlaquesSharedPreferences
                    .getLastKnownBPLCoordinate(getActivity());
            MapsInitializer.initialize(getActivity());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    lastKnownCoordinate,
                    BluePlaquesSharedPreferences.getMapZoom(getActivity())));
            BluePlaquesLondonApplication.bus.register(this);
            setProgressBarVisibility(View.GONE);
        }
    }

    public void loadMapData() {
        model.loadMapData(getActivity());
    }

    private void setupMap() {
        getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mapReady(googleMap);
            }
        });
    }

    private void addPlacemarksToMap() {
        for (Placemark placemark : model.getMassagedPlacemarks()) {
            int iconResource = R.drawable.blue;
            if (!placemark.getStyleUrl().equalsIgnoreCase("#myDefaultStyles")) {
                iconResource = R.drawable.green;
            }
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(
                            new LatLng(placemark.getLatitude(), placemark
                                    .getLongitude()))
                    .title(placemark.getName())
                    .snippet(getSnippetForPlacemark(placemark))
                    .icon(BitmapDescriptorFactory.fromResource(iconResource)));
            KeyedMarker keyedMarker = new KeyedMarker();
            keyedMarker.setKey(placemark.key());
            keyedMarker.setMarker(marker);
            markers.add(keyedMarker);
        }
    }

    @Override
    public void onCameraChange(CameraPosition position) {
        BluePlaquesSharedPreferences.saveLastKnownCoordinate(getActivity(),
                position.target);
        BluePlaquesSharedPreferences.saveMapZoom(getActivity(), googleMap,
                position.zoom);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latLng = marker.getPosition();
        BluePlaquesSharedPreferences.saveLastKnownBPLCoordinate(getActivity(),
                latLng);
        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity()
                .getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.MARKER_PRESSED_EVENT, marker.getTitle());
        return false;
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(getActivity(), MapDetailActivity.class);
        intent.putParcelableArrayListExtra(
                BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                getListOfPlacemarksForMarker(marker));
        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getActivity()
                .getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.MARKER_INFO_WINDOW_PRESSED_EVENT,
                marker.getTitle());
        startActivity(intent);
    }

    public void navigateToPlacemark(Placemark placemark) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        placemark.getLatitude(), placemark.getLongitude()),
                BluePlaquesSharedPreferences.getMapZoom(getActivity())));
        LatLng latLng = new LatLng(placemark.getLatitude(),
                placemark.getLongitude());
        BluePlaquesSharedPreferences.saveLastKnownBPLCoordinate(getActivity(),
                latLng);
        for (KeyedMarker keyedMarker : markers) {
            if (placemark.key().equals(keyedMarker.getKey())) {
                keyedMarker.getMarker().showInfoWindow();
                break;
            }
        }
    }

    private String getSnippetForPlacemark(Placemark placemark) {
        String snippet;
        List<Integer> numberOfPlacemarksAssociatedWithPlacemark = model
                .getParser().getKeyToArrayPositions().get(placemark.key());
        if (numberOfPlacemarksAssociatedWithPlacemark.size() == 1) {
            snippet = placemark.getOccupation();
        } else {
            snippet = getString(R.string.multiple_placemarks);
        }
        return snippet;
    }

    private ArrayList<Placemark> getListOfPlacemarksForMarker(Marker marker) {
        ArrayList<Placemark> placemarks = new ArrayList<Placemark>();
        for (KeyedMarker keyedMarker : markers) {
            if (keyedMarker.getMarker().equals(marker)) {
                List<Integer> numberOfPlacemarksAssociatedWithPlacemark = model
                        .getParser().getKeyToArrayPositions()
                        .get(keyedMarker.getKey());
                placemarks = model
                        .getPlacemarksAtIndices(numberOfPlacemarksAssociatedWithPlacemark);
                break;
            }
        }
        return placemarks;
    }

    private void setProgressBarVisibility(int visibility) {
        MainActivity activity = (MainActivity) getActivity();
        if (activity != null) {
            activity.updateProgressBarVisibility(visibility);
        }
    }

    public MapModel getModel() {
        return model;
    }

    public void setModel(MapModel model) {
        this.model = model;
    }
}
