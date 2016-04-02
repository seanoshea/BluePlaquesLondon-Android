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

package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.upwardsnorthwards.blueplaqueslondon.R;

/**
 * Helper class which listens for internet connectivity events.
 */
public class InternetConnectivityHelper {

    private static final String TAG = "ConnectivityHelper";

    /**
     * Weak reference to the context in which this helper was instantiated.
     */
    private Context context;

    /**
     * Used for communicating connectivity events back to client code.
     */
    private InternetConnectivityHelperDelegate delegate;
    /**
     * The current state of internet connectivity.
     */
    private InternetConnectivity currentInternetConnectivity;
    /**
     * The receiver responsible for indicating whether or not the device has regained or lost internet connectivity.
     */
    private BroadcastReceiver networkConnectivityReceiver;

    /**
     * Simple constructor.
     *
     * @param context
     */
    public InternetConnectivityHelper(Context context) {
        this.context = context;
    }

    public void onResume() {
        networkConnectivityReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, @NonNull Intent intent) {
                String action = intent.getAction();
                if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                    Log.v(TAG, "Received an intent related to internet connectivity");
                    boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
                    if (noConnectivity && currentInternetConnectivity == InternetConnectivity.InternetConnectivityConnected) {
                        lostInternetConnectivity();
                    } else if (!noConnectivity && currentInternetConnectivity == InternetConnectivity.InternetConnectivityNoConnection) {
                        regainedInternetConnectivity();
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkConnectivityReceiver, filter);
        updateCurrentInternetConnectivity();
    }

    public void onPause() {
        if (networkConnectivityReceiver != null) {
            context.unregisterReceiver(networkConnectivityReceiver);
        }
    }

    public void showConnectivityToast() {
        if (context != null) {
            Toast toast = Toast.makeText(context, context.getString(R.string.internet_connectivity_message), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Executed callback when the device loses internet connectivity
     */
    protected void lostInternetConnectivity() {
        currentInternetConnectivity = InternetConnectivity.InternetConnectivityNoConnection;
        if (delegate != null) {
            delegate.lostInternetConnectivity();
        }
    }

    /**
     * Executed callback when the device regains internet connectivity
     */
    protected void regainedInternetConnectivity() {
        currentInternetConnectivity = InternetConnectivity.InternetConnectivityConnected;
        if (delegate != null) {
            delegate.regainedInternetConnectivity();
        }
    }

    private void updateCurrentInternetConnectivity() {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Log.v(TAG, "The application has an internet connection");
            currentInternetConnectivity = InternetConnectivity.InternetConnectivityConnected;
        } else {
            Log.v(TAG, "The application does not have an internet connection");
            currentInternetConnectivity = InternetConnectivity.InternetConnectivityNoConnection;
        }
        if (delegate != null) {
            delegate.internetConnectivityUpdated(currentInternetConnectivity == InternetConnectivity.InternetConnectivityConnected);
        }
    }

    public void setDelegate(InternetConnectivityHelperDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Gives an understanding of whether or not the application has an internet connection.
     */
    private enum InternetConnectivity {
        InternetConnectivityConnected,
        InternetConnectivityNoConnection,
    }
}
