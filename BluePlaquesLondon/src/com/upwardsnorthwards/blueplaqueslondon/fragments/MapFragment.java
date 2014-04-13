/*
 Copyright 2014 Sean O' Shea
 
 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.apache.org/licenses/LICENSE-2.0
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.upwardsnorthwards.blueplaqueslondon.fragments;

import java.util.List;

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.MapModel;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesSharedPreferences;

public class MapFragment extends com.google.android.gms.maps.SupportMapFragment
		implements OnCameraChangeListener, OnMarkerClickListener {

	private GoogleMap googleMap;
	private MapModel model;

	@Override
	public void onResume() {
		super.onResume();
		setupMap();

		LatLng lastKnownCoordinate = BluePlaquesSharedPreferences
				.getLastKnownBPLCoordinate(getActivity());

		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
				lastKnownCoordinate,
				BluePlaquesSharedPreferences.getMapZoom(getActivity())));
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		model = new MapModel();
		loadMapData();
	}

	public void loadMapData() {
		model.loadMapData(getActivity());
	}

	private void setupMap() {
		googleMap = getMap();
		if (googleMap == null) {
			googleMap = ((MapFragment) getFragmentManager().findFragmentById(
					R.id.map)).getMap();
		}
		if (googleMap != null) {
			// a few settings
			googleMap.setIndoorEnabled(false);
			googleMap.getUiSettings().setMyLocationButtonEnabled(true);
			// listen for events
			googleMap.setOnCameraChangeListener(this);
			googleMap.setOnMarkerClickListener(this);
			addPlacemarksToMap();
		}
	}

	private void addPlacemarksToMap() {

		for (Placemark placemark : model.getMassagedPlacemarks()) {

			int iconResource = R.drawable.blue;
			if (!placemark.getStyleUrl().equalsIgnoreCase("#myDefaultStyles")) {
				iconResource = R.drawable.green;
			}

			googleMap.addMarker(new MarkerOptions()
					.position(
							new LatLng(placemark.getLatitude(), placemark
									.getLongitude()))
					.title(placemark.getTitle())
					.snippet(getSnippetForPlacemark(placemark))
					.icon(BitmapDescriptorFactory.fromResource(iconResource)));
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
		return false;
	}

	private String getSnippetForPlacemark(Placemark placemark) {
		String snippet;
		List<Integer> numberOfPlacemarksAssociatedWithPlacemark = model
				.getKeyToArrayPositions().get(placemark.key());
		if (numberOfPlacemarksAssociatedWithPlacemark.size() == 1) {
			snippet = placemark.getOccupation();
		} else {
			// generic message should suffice
			snippet = getString(R.string.multiple_placemarks);
		}
		return snippet;
	}
}
