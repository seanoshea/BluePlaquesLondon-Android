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

package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

/**
 * Includes getters and setters for some of the preferences associated with the app. Uses
 * SharedPreferences as a data store.
 */
public class BluePlaquesSharedPreferences {

    private static final String PREFERENCES_KEY = "PREFERENCES_KEY";

    private static final String LAST_KNOWN_BPL_COORDINATE_LATITUDE = "LAST_KNOWN_BPL_COORDINATE_LATITUDE";
    private static final String LAST_KNOWN_BPL_COORDINATE_LONGITUDE = "LAST_KNOWN_BPL_COORDINATE_LONGITUDE";
    private static final String LAST_KNOWN_COORDINATE_LATITUDE = "LAST_KNOWN_COORDINATE_LATITUDE";
    private static final String LAST_KNOWN_COORDINATE_LONGITUDE = "LAST_KNOWN_COORDINATE_LONGITUDE";
    private static final String MAP_ZOOM = "MAP_ZOOM";
    private static final String ANALYTICS_ENABLED = "ANALYTICS_ENABLED";

    static final float MAP_ZOOM_DEFAULT = 15.0f;
    static final boolean ANALYTICS_ENABLED_DEFAULT = true;

    public static LatLng getLastKnownBPLCoordinate(final Context context) {
        return getSavedCoordinate(context, LAST_KNOWN_BPL_COORDINATE_LATITUDE,
                LAST_KNOWN_BPL_COORDINATE_LONGITUDE);
    }

    public static void saveLastKnownBPLCoordinate(final Context context, final LatLng latLng) {
        saveCoordinate(context, latLng, LAST_KNOWN_BPL_COORDINATE_LATITUDE,
                LAST_KNOWN_BPL_COORDINATE_LONGITUDE);
    }

    public static LatLng getLastKnownCoordinate(final Context context) {
        return getSavedCoordinate(context, LAST_KNOWN_COORDINATE_LATITUDE,
                LAST_KNOWN_COORDINATE_LONGITUDE);
    }

    public static void saveLastKnownCoordinate(final Context context, final LatLng latLng) {
        saveCoordinate(context, latLng, LAST_KNOWN_COORDINATE_LATITUDE,
                LAST_KNOWN_COORDINATE_LONGITUDE);
    }

    public static float getMapZoom(final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        return preferences.getFloat(MAP_ZOOM, MAP_ZOOM_DEFAULT);
    }

    public static void saveMapZoom(final Context context, final GoogleMap map, final float zoom) {
        if (zoom <= map.getMaxZoomLevel() && zoom >= map.getMinZoomLevel()) {
            final SharedPreferences preferences = context.getSharedPreferences(
                    PREFERENCES_KEY, Context.MODE_PRIVATE);
            final SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(MAP_ZOOM, zoom);
            editor.commit();
        }
    }

    public static boolean getAnalyticsEnabled(final Context context) {
        final SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        return preferences.getBoolean(ANALYTICS_ENABLED,
                ANALYTICS_ENABLED_DEFAULT);
    }

    public static void saveAnalyticsEnabled(final Context context, final boolean enabled) {
        final SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ANALYTICS_ENABLED, enabled);
        editor.commit();
    }

    private static void saveCoordinate(final Context context, final LatLng latLng, final String latitudeKey, final String longitudeKey) {
        final SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        final SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(latitudeKey, (float) latLng.latitude);
        editor.putFloat(longitudeKey, (float) latLng.longitude);
        editor.commit();
    }

    private static LatLng getSavedCoordinate(final Context context, final String latitudeKey, final String longitudeKey) {
        final SharedPreferences preferences = context.getSharedPreferences(
                PREFERENCES_KEY, Context.MODE_PRIVATE);
        final float latitude = preferences.getFloat(latitudeKey,
                (float) BluePlaquesConstants.DEFAULT_LATITUDE);
        final float longitude = preferences.getFloat(longitudeKey,
                (float) BluePlaquesConstants.DEFAULT_LONGITUDE);
        return new LatLng(latitude, longitude);
    }
}
