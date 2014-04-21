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

package com.upwardsnorthwards.blueplaqueslondon;

import android.app.Application;
import android.content.IntentSender;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

public class BluePlaquesLondonApplication extends Application implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

	private LocationClient locationClient;
	private Location currentLocation;

	@Override
	public void onCreate() {
		super.onCreate();
		locationClient = new LocationClient(this, this, this);
		if (!Build.HARDWARE.contains("vbox")) {
			locationClient.connect();
		} else {
			// dummy the current location
			currentLocation = new Location("");
			currentLocation.setLatitude(BluePlaquesConstants.DEFAULT_LATITUDE);
			currentLocation
					.setLongitude(BluePlaquesConstants.DEFAULT_LONGITUDE);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		try {
			if (connectionResult.hasResolution()) {
				connectionResult.startResolutionForResult(null,
						CONNECTION_FAILURE_RESOLUTION_REQUEST);
			}
		} catch (IntentSender.SendIntentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {

	}

	@Override
	public void onDisconnected() {

	}

	@Override
	public void onLocationChanged(Location location) {
		currentLocation = location;
	}

	public Location getCurrentLocation() {
		return currentLocation;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
}
