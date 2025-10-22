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


package com.upwardsnorthwards.blueplaqueslondon;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.upwardsnorthwards.blueplaqueslondon.workers.WorkManagerInitializer;

import javax.inject.Inject;

import dagger.hilt.android.HiltAndroidApp;

/**
 * Application class with Hilt dependency injection.
 * Initializes Firebase Analytics and Crashlytics.
 */
@HiltAndroidApp
public class BluePlaquesLondonApplication extends Application {

    private static final String TAG = "BluePlaquesLondonApp";
    private static final String APPLICATION_LOADED = "ApplicationLoaded";

    private FirebaseAnalytics firebaseAnalytics;

    @Inject
    WorkManagerInitializer workManagerInitializer;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize Firebase
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        // Schedule periodic background work
        // TODO: Fix WorkManager Hilt integration - currently disabled due to worker instantiation issues
        // workManagerInitializer.schedulePeriodicPlaquesSync();

        trackApplicationLoadedEvent();
    }

    public void trackEvent(@NonNull final String category, @NonNull final String action, @NonNull final String label) {
        // Log event to Firebase Analytics
        android.os.Bundle bundle = new android.os.Bundle();
        bundle.putString("category", category);
        bundle.putString("action", action);
        bundle.putString("label", label);
        firebaseAnalytics.logEvent("app_event", bundle);
    }

    private void trackApplicationLoadedEvent() {
        try {
            final PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            trackEvent(
                    APPLICATION_LOADED,
                    String.format("Application Version: %s", pInfo.versionName),
                    String.format("Android Version %s", Build.VERSION.RELEASE));
        } catch (NameNotFoundException e) {
            Log.e(TAG, "An error occurred when requesting the package information from the app", e);
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
