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
import androidx.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.upwardsnorthwards.blueplaqueslondon.R;

/**
 * Network connectivity monitor and helper for the Blue Plaques London application.
 * 
 * <p>This class provides real-time monitoring of internet connectivity status,
 * automatically registering broadcast receivers to detect network state changes
 * and notifying delegates of connectivity events.</p>
 * 
 * <p><strong>Key Features:</strong></p>
 * <ul>
 *   <li>Real-time connectivity monitoring via BroadcastReceiver</li>
 *   <li>Delegate pattern for connectivity event notifications</li>
 *   <li>Automatic lifecycle management (onResume/onPause)</li>
 *   <li>User-friendly connectivity status toasts</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * public class MainActivity extends AppCompatActivity 
 *         implements InternetConnectivityHelperDelegate {
 *     
 *     private InternetConnectivityHelper connectivityHelper;
 *     
 *     @Override
 *     protected void onCreate(Bundle savedInstanceState) {
 *         super.onCreate(savedInstanceState);
 *         connectivityHelper = new InternetConnectivityHelper(this);
 *         connectivityHelper.setDelegate(this);
 *     }
 *     
 *     @Override
 *     protected void onResume() {
 *         super.onResume();
 *         connectivityHelper.onResume(); // Start monitoring
 *     }
 *     
 *     @Override
 *     protected void onPause() {
 *         super.onPause();
 *         connectivityHelper.onPause(); // Stop monitoring
 *     }
 *     
 *     @Override
 *     public void lostInternetConnectivity() {
 *         // Handle offline state
 *         showOfflineMessage();
 *     }
 *     
 *     @Override
 *     public void regainedInternetConnectivity() {
 *         // Handle online state
 *         refreshData();
 *     }
 * }
 * }</pre>
 * 
 * <p><strong>Architecture Integration:</strong></p>
 * <ul>
 *   <li>Used by Activities to monitor network state for Wikipedia and Maps functionality</li>
 *   <li>Integrates with {@link InternetConnectivityHelperDelegate} for event callbacks</li>
 *   <li>Supports graceful degradation when offline</li>
 * </ul>
 * 
 * <p><strong>Lifecycle Management:</strong></p>
 * <p>This class requires proper lifecycle management to avoid memory leaks.
 * Always call {@link #onResume()} and {@link #onPause()} from the corresponding
 * Activity lifecycle methods.</p>
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 * @see InternetConnectivityHelperDelegate
 * @see android.content.BroadcastReceiver
 * @see android.net.ConnectivityManager
 */
public class InternetConnectivityHelper {

    private static final String TAG = "ConnectivityHelper";

    /**
     * Application context for accessing system services and registering receivers.
     * Maintained as a reference to enable connectivity monitoring and toast display.
     */
    private final Context context;

    /**
     * Delegate for receiving connectivity change notifications.
     * Implements the observer pattern for loose coupling between this helper
     * and client code that needs to respond to network state changes.
     */
    private InternetConnectivityHelperDelegate delegate;
    /**
     * Current network connectivity state.
     * Tracks whether the device currently has an active internet connection,
     * used to detect state transitions and avoid duplicate notifications.
     */
    private InternetConnectivity currentInternetConnectivity;
    /**
     * Broadcast receiver for network connectivity change events.
     * Automatically registered/unregistered during onResume/onPause lifecycle
     * to monitor ConnectivityManager.CONNECTIVITY_ACTION broadcasts.
     */
    private BroadcastReceiver networkConnectivityReceiver;

    /**
     * Creates a new internet connectivity helper.
     * 
     * <p>The provided context will be used for accessing system services
     * and displaying connectivity-related toasts to the user.</p>
     * 
     * @param context the application or activity context, must not be null
     */
    public InternetConnectivityHelper(Context context) {
        this.context = context;
    }

    /**
     * Starts monitoring network connectivity changes.
     * 
     * <p>This method should be called from the Activity's onResume() method
     * to begin listening for connectivity changes. It registers a broadcast
     * receiver and performs an initial connectivity check.</p>
     * 
     * <p><strong>Important:</strong> Must be paired with {@link #onPause()}
     * to avoid memory leaks.</p>
     */
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

    /**
     * Stops monitoring network connectivity changes.
     * 
     * <p>This method should be called from the Activity's onPause() method
     * to unregister the broadcast receiver and prevent memory leaks.</p>
     */
    public void onPause() {
        if (networkConnectivityReceiver != null) {
            context.unregisterReceiver(networkConnectivityReceiver);
        }
    }

    /**
     * Displays a user-friendly connectivity status message.
     * 
     * <p>Shows a short toast message informing the user about the current
     * internet connectivity status. Useful for providing immediate feedback
     * when network-dependent features are accessed.</p>
     */
    public void showConnectivityToast() {
        if (context != null) {
            Toast toast = Toast.makeText(context, context.getString(R.string.internet_connectivity_message), Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    /**
     * Handles loss of internet connectivity.
     * 
     * <p>Updates internal state and notifies the delegate if one is set.
     * This method is called automatically when the system broadcasts
     * a connectivity change indicating network loss.</p>
     */
    private void lostInternetConnectivity() {
        currentInternetConnectivity = InternetConnectivity.InternetConnectivityNoConnection;
        if (delegate != null) {
            delegate.lostInternetConnectivity();
        }
    }

    /**
     * Handles restoration of internet connectivity.
     * 
     * <p>Updates internal state and notifies the delegate if one is set.
     * This method is called automatically when the system broadcasts
     * a connectivity change indicating network restoration.</p>
     */
    private void regainedInternetConnectivity() {
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

    /**
     * Sets the delegate to receive connectivity change notifications.
     * 
     * @param delegate the delegate to notify of connectivity changes, may be null
     * @see InternetConnectivityHelperDelegate
     */
    public void setDelegate(InternetConnectivityHelperDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * Internal enumeration representing internet connectivity states.
     * 
     * <p>Used internally to track the current connectivity status and
     * detect state transitions for delegate notifications.</p>
     */
    private enum InternetConnectivity {
        /** Device has an active internet connection */
        InternetConnectivityConnected,
        
        /** Device has no internet connection */
        InternetConnectivityNoConnection,
    }
}
