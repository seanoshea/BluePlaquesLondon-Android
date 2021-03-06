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

package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.utils.InternetConnectivityHelper;
import com.upwardsnorthwards.blueplaqueslondon.utils.InternetConnectivityHelperDelegate;

/**
 * Simple 'base'-style class which exposes the ability to set the text on the custom title bar.
 */
@SuppressLint("Registered")
public class BaseActivity extends Activity implements InternetConnectivityHelperDelegate {

    private static final String TAG = "BaseActivity";

    /**
     * Offers callbacks to client code to announce whether the device has internet connectivity or not.
     */
    private InternetConnectivityHelper internetConnectivityHelper;

    @Override
    protected void onResume() {
        super.onResume();
        registerForInternetConnectivity();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (internetConnectivityHelper != null) {
            internetConnectivityHelper.onPause();
        }
    }

    /**
     * The application uses a custom title bar. This method allows client code to set the title
     * easily. Will do nothing if it cannot find the title_bar
     *
     * @param text the text to set on the title bar.
     */
    void setCustomTitleBarText(final String text) {
        final TextView titleBar = (TextView) findViewById(R.id.title_bar);
        if (titleBar != null) {
            titleBar.setText(text);
        } else {
            Log.v(TAG, "Tried to set the title bar text to " + text + " but could not find title_bar in the view hierarchy");
        }
    }

    private void registerForInternetConnectivity() {
        internetConnectivityHelper = new InternetConnectivityHelper(this);
        internetConnectivityHelper.setDelegate(this);
        internetConnectivityHelper.onResume();
    }

    @Override
    public void internetConnectivityUpdated(boolean hasInternetConnectivity) {
        if (!hasInternetConnectivity) {
            internetConnectivityHelper.showConnectivityToast();
        }
    }

    @Override
    public void lostInternetConnectivity() {

    }

    @Override
    public void regainedInternetConnectivity() {

    }
}
