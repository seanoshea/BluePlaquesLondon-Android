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

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.IWikipediaModelDelegate;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModel;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

/**
 * Legacy Wikipedia article display activity using WebView.
 * 
 * <p><strong>Note:</strong> This activity is part of the legacy architecture and is being
 * replaced by {@link com.upwardsnorthwards.blueplaqueslondon.fragments.WikipediaFragment}
 * in the Navigation Component architecture. It remains for backward compatibility.</p>
 * 
 * <p>This activity displays Wikipedia articles about blue plaque subjects using a WebView.
 * It implements intelligent Wikipedia URL resolution through the Wikipedia Search API
 * to find the most relevant article for each plaque subject.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li>Wikipedia Search API integration via {@link WikipediaModel}</li>
 *   <li>Fallback to direct Wikipedia URLs if search fails</li>
 *   <li>WebView navigation with back button support</li>
 *   <li>Custom title bar showing plaque subject name</li>
 *   <li>Internet connectivity monitoring and retry logic</li>
 *   <li>Analytics tracking for page load success/failure</li>
 * </ul>
 * 
 * <h3>Architecture:</h3>
 * <p>Uses the delegate pattern with {@link IWikipediaModelDelegate} to handle
 * asynchronous Wikipedia URL resolution. The {@link WikipediaModel} performs
 * API calls on a background thread and reports results via delegate callbacks.</p>
 * 
 * <h3>Usage:</h3>
 * <pre>{@code
 * Intent intent = new Intent(context, WikipediaActivity.class);
 * intent.putExtra(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, placemark);
 * startActivity(intent);
 * }</pre>
 * 
 * @see WikipediaModel
 * @see IWikipediaModelDelegate
 * @see com.upwardsnorthwards.blueplaqueslondon.fragments.WikipediaFragment
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 * @deprecated Use {@link com.upwardsnorthwards.blueplaqueslondon.fragments.WikipediaFragment} with Navigation Component
 */
public class WikipediaActivity extends BaseActivity implements IWikipediaModelDelegate {

    private static final String TAG = "WikipediaActivity";

    private WebView webView;
    private Placemark placemark;
    private WikipediaModel wikipediaModel;
    private WikipediaActivityWebViewLoadedState state;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_wikipedia);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
        webView = (WebView) findViewById(R.id.activity_wikipedia_web_view);
        final Intent intent = getIntent();
        if (intent != null) {
            placemark = intent.getParcelableExtra(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (placemark != null) {
            setCustomTitleBarText(placemark.getTrimmedName());
            initiateWebViewRequest();
        } else {
            Log.v(TAG, "Placemark was null when resuming the WikipediaActivity");
            onRetriveWikipediaUrlFailure();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        wikipediaModel.onPause();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * Handles successful Wikipedia URL resolution from the search API.
     * 
     * <p>Called by {@link WikipediaModel} when a Wikipedia article URL is successfully
     * resolved for the plaque subject. This method configures the WebView with
     * appropriate clients and loads the resolved URL.</p>
     * 
     * <p>WebView configuration includes:</p>
     * <ul>
     *   <li>Progress tracking via {@link WebChromeClient}</li>
     *   <li>Error handling via {@link WebViewClient}</li>
     *   <li>Support for both legacy and modern error handling APIs</li>
     * </ul>
     * 
     * @param url The resolved Wikipedia article URL to display
     * @see IWikipediaModelDelegate#onRetriveWikipediaUrlSuccess(String)
     */
    public void onRetriveWikipediaUrlSuccess(final String url) {
        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(final WebView view, final int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            @SuppressWarnings("deprecation")
            public void onReceivedError(final WebView view, final int errorCode,
                                        final String description, final String failingUrl) {
                onRetriveWikipediaUrlFailure();
            }
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req, WebResourceError rerr) {
                onReceivedError(view, rerr.getErrorCode(), rerr.getDescription().toString(), req.getUrl().toString());
                onRetriveWikipediaUrlFailure();
            }
        });
        webView.loadUrl(url);
    }

    /**
     * Handles Wikipedia URL resolution failures.
     * 
     * <p>Called by {@link WikipediaModel} when the Wikipedia search API fails
     * to resolve a URL for the plaque subject. This method updates the internal
     * state and tracks the failure event for analytics purposes.</p>
     * 
     * <p>The failure is tracked with the specific plaque name to help identify
     * subjects that consistently fail to resolve Wikipedia articles.</p>
     * 
     * @see IWikipediaModelDelegate#onRetriveWikipediaUrlFailure()
     */
    public void onRetriveWikipediaUrlFailure() {
        state = WikipediaActivityWebViewLoadedState.WikipediaActivityWebViewLoadedStateError;
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
        app.trackEvent(BluePlaquesConstants.ERROR_CATEGORY,
                BluePlaquesConstants.WIKIPEDIA_PAGE_LOAD_ERROR_EVENT,
                placemark.getName());
    }

    @Override
    public void regainedInternetConnectivity() {
        super.regainedInternetConnectivity();
        if (state == WikipediaActivityWebViewLoadedState.WikipediaActivityWebViewLoadedStateError) {
            initiateWebViewRequest();
        }
    }

    /**
     * Initiates the Wikipedia article search and loading process.
     * 
     * <p>This method creates a new {@link WikipediaModel} instance and starts
     * the asynchronous Wikipedia search process. The search uses the plaque
     * subject's name to find the most relevant Wikipedia article.</p>
     * 
     * <p>The process involves:</p>
     * <ol>
     *   <li>Creating a new WikipediaModel instance</li>
     *   <li>Setting this activity as the delegate for callbacks</li>
     *   <li>Executing the search with the plaque name and URL template</li>
     *   <li>Updating the internal state to indicate loading</li>
     * </ol>
     */
    private void initiateWebViewRequest() {
        wikipediaModel = new WikipediaModel();
        state = WikipediaActivityWebViewLoadedState.WikipediaActivityWebViewLoadedStateOK;
        wikipediaModel.setDelegate(this);
        wikipediaModel.execute(placemark.getName(), getString(R.string.wikipedia_url));
    }

    /**
     * Represents the current state of the WebView loading process.
     * 
     * <p>This enum tracks whether the Wikipedia article has loaded successfully
     * or encountered an error. It's used to determine whether to retry loading
     * when internet connectivity is restored.</p>
     * 
     * @see #regainedInternetConnectivity()
     */
    private enum WikipediaActivityWebViewLoadedState {
        WikipediaActivityWebViewLoadedStateOK,
        WikipediaActivityWebViewLoadedStateError,
    }
}