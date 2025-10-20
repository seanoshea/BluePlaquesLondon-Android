// Copyright (c) 2014 - 2025 Upwards Northwards Software Limited
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

import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.core.content.ContextCompat;
import android.util.Log;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
// Migrated to Firebase Analytics
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import org.greenrobot.eventbus.EventBus;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import java.util.HashMap;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Application class. Initialises Google Play Services and location services.
 */
public class BluePlaquesLondonApplication extends MultiDexApplication implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    public final static int CONNECTION_FAILURE_NO_RESOLUTION_REQUEST = 9001;
    public static final EventBus bus = EventBus.getDefault();
    private final static String TAG = "BluePlaquesLondonApp";
    private final static String TRACKER_ID = "UA-46153093-3";
    @NonNull
    private FirebaseAnalytics firebaseAnalytics;
    private GoogleApiClient locationClient;
    private Location currentLocation;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    @Override
    public void onConnectionFailed(@NonNull final ConnectionResult connectionResult) {
        Log.e(TAG, "onConnectionFailed " + connectionResult);
        try {
            if (connectionResult.hasResolution()) {
                connectionResult.startResolutionForResult(null,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } else {
                Log.e(TAG, "The connection result did not have a resolution");
            }
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "An error occurred when trying to resolve the Google Maps issue", e);
        }
    }

    @Override
    public void onConnected(final Bundle connectionHint) {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            currentLocation = LocationServices.FusedLocationApi.getLastLocation(
                    locationClient);
            final LocationRequest locationRequest = new LocationRequest();
            locationRequest.setInterval(10000);
            locationRequest.setFastestInterval(5000);
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    locationClient, locationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(final int i) {
        Log.w(TAG, "onConnectionSuspended " + i);
    }

    @Override
    public void onLocationChanged(final Location location) {
        currentLocation = location;
    }

    public void trackEvent(final String category, final String action, final String label) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_CATEGORY, category);
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, action);
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, label);
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    // Migrated to Firebase Analytics

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

    public enum TrackerName {
        APP_TRACKER,
    }
}