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

import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.MapModel;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesSharedPreferences;

public class MapFragment extends com.google.android.gms.maps.SupportMapFragment
		implements OnCameraChangeListener {

	private GoogleMap googleMap;
	private MapModel model;

	@Override
	public void onResume() {
		super.onResume();
		setupMap();

		LatLng lastKnownCoordinate = BluePlaquesSharedPreferences
				.getLastKnownBPLCoordinate(getActivity());
		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

		googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
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
			// Check if we were successful in obtaining the map.
			if (googleMap != null) {
				// a few settings
				googleMap.setIndoorEnabled(false);
				googleMap.getUiSettings().setMyLocationButtonEnabled(true);
				// listen for events
				googleMap.setOnCameraChangeListener(this);
			}
		}
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		BluePlaquesSharedPreferences.saveLastKnownCoordinate(getActivity(),
				position.target);
		BluePlaquesSharedPreferences.saveMapZoom(getActivity(), googleMap,
				position.zoom);
	}
}
