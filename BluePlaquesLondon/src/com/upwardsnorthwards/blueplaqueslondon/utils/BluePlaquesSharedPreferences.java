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

package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class BluePlaquesSharedPreferences {

	private static String PREFERENCES_KEY = "PREFERENCES_KEY";

	private static String LAST_KNOWN_BPL_COORDINATE_LATITUDE = "LAST_KNOWN_BPL_COORDINATE_LATITUDE";
	private static String LAST_KNOWN_BPL_COORDINATE_LONGITUDE = "LAST_KNOWN_BPL_COORDINATE_LONGITUDE";
	private static String LAST_KNOWN_COORDINATE_LATITUDE = "LAST_KNOWN_COORDINATE_LATITUDE";
	private static String LAST_KNOWN_COORDINATE_LONGITUDE = "LAST_KNOWN_COORDINATE_LONGITUDE";
	private static String MAP_ZOOM = "MAP_ZOOM";

	static final float MAP_ZOOM_DEFAULT = 15.0f;

	public static LatLng getLastKnownBPLCoordinate(Context context) {
		return getSavedCoordinate(context, LAST_KNOWN_BPL_COORDINATE_LATITUDE,
				LAST_KNOWN_BPL_COORDINATE_LONGITUDE);
	}

	public static void saveLastKnownBPLCoordinate(Context context, LatLng latLng) {
		saveCoordinate(context, latLng, LAST_KNOWN_BPL_COORDINATE_LATITUDE,
				LAST_KNOWN_BPL_COORDINATE_LONGITUDE);
	}

	public static LatLng getLastKnownCoordinate(Context context) {
		return getSavedCoordinate(context, LAST_KNOWN_COORDINATE_LATITUDE,
				LAST_KNOWN_COORDINATE_LONGITUDE);
	}

	public static void saveLastKnownCoordinate(Context context, LatLng latLng) {
		saveCoordinate(context, latLng, LAST_KNOWN_COORDINATE_LATITUDE,
				LAST_KNOWN_COORDINATE_LONGITUDE);
	}

	public static float getMapZoom(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_KEY, Context.MODE_PRIVATE);
		return preferences.getFloat(MAP_ZOOM, MAP_ZOOM_DEFAULT);
	}

	public static void saveMapZoom(Context context, GoogleMap map, float zoom) {
		if (zoom <= map.getMaxZoomLevel() && zoom >= map.getMinZoomLevel()) {
			SharedPreferences preferences = context.getSharedPreferences(
					PREFERENCES_KEY, Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = preferences.edit();
			editor.putFloat(MAP_ZOOM, zoom);
			editor.commit();
		}
	}

	private static void saveCoordinate(Context context, LatLng latLng,
			String latitudeKey, String longitudeKey) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_KEY, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = preferences.edit();
		editor.putFloat(latitudeKey, (float) latLng.latitude);
		editor.putFloat(longitudeKey, (float) latLng.longitude);
		editor.commit();
	}

	private static LatLng getSavedCoordinate(Context context,
			String latitudeKey, String longitudeKey) {
		SharedPreferences preferences = context.getSharedPreferences(
				PREFERENCES_KEY, Context.MODE_PRIVATE);
		float latitude = preferences.getFloat(latitudeKey,
				(float) BluePlaquesConstants.DEFAULT_LATITUDE);
		float longitude = preferences.getFloat(longitudeKey,
				(float) BluePlaquesConstants.DEFAULT_LONGITUDE);
		return new LatLng(latitude, longitude);
	}
}
