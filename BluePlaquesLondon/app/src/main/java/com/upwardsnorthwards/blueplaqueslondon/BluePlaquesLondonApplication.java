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

package com.upwardsnorthwards.blueplaqueslondon;

import android.app.Application;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import java.util.HashMap;

public class BluePlaquesLondonApplication extends Application implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static String TAG = "BluePlaquesLondonApplication";

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public final static int CONNECTION_FAILURE_NO_RESOLUTION_REQUEST = 9001;

    public enum TrackerName {
        APP_TRACKER,
    }

    private HashMap<TrackerName, Tracker> trackers = new HashMap<TrackerName, Tracker>();
    private GoogleApiClient locationClient;
    private Location currentLocation;
    public static Bus bus = new Bus(ThreadEnforcer.MAIN);

    @Override
    public void onCreate() {
        super.onCreate();
        locationClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        if (!Build.HARDWARE.contains("vbox")) {
            locationClient.connect();
        } else {
            // dummy the current location
            currentLocation = new Location("");
            currentLocation.setLatitude(BluePlaquesConstants.DEFAULT_LATITUDE);
            currentLocation
                    .setLongitude(BluePlaquesConstants.DEFAULT_LONGITUDE);
        }
        trackApplicationLoadedEvent();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult);
        try {
            if (connectionResult.hasResolution()) {
                connectionResult.startResolutionForResult(null,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } else {
                GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(),
                        null, CONNECTION_FAILURE_NO_RESOLUTION_REQUEST);
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "An error occurred when loading the map", e);
            GooglePlayServicesUtil.showErrorDialogFragment(connectionResult.getErrorCode(),
                    null, CONNECTION_FAILURE_NO_RESOLUTION_REQUEST);
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                locationClient);
        final LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationServices.FusedLocationApi.requestLocationUpdates(
                locationClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.w(TAG, "onConnectionSuspended " + i);
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    public void trackEvent(String category, String action, String label) {
        final Tracker tracker = getTracker(TrackerName.APP_TRACKER);
        tracker.send(new HitBuilders.EventBuilder().setCategory(category)
                .setAction(action).setLabel(label).build());
    }

    private synchronized Tracker getTracker(TrackerName trackerId) {
        if (!trackers.containsKey(trackerId)) {
            final GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
            final Tracker t = analytics.newTracker(R.xml.app_tracker);
            trackers.put(trackerId, t);
        }
        return trackers.get(trackerId);
    }

    private void trackApplicationLoadedEvent() {
        final PackageInfo pInfo;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            trackEvent(
                    BluePlaquesConstants.APPLICATION_LOADED,
                    String.format("Application Version: %s", pInfo.versionName),
                    String.format("Android Version %s", Build.VERSION.RELEASE));
        } catch (NameNotFoundException e) {
            Log.e(TAG, "An error occurred when requesting the package information from the app", e);
        }
    }

    public Location getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(Location currentLocation) {
        this.currentLocation = currentLocation;
    }
}