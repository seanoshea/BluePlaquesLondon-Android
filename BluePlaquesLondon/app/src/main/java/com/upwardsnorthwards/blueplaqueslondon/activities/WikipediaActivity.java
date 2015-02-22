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

package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.IWikipediaModelDelegate;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModel;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

public class WikipediaActivity extends BaseActivity implements IWikipediaModelDelegate {

    private WebView webView;
    private Placemark placemark;
    private WikipediaModel wikipediaModel;

    /**
     * Used to see whether the web view has loaded ok or not
     */
    private enum WikipediaActivityWebViewLoadedState {
        WikipediaActivityWebViewLoadedStateOK,
        WikipediaActivityWebViewLoadedStateError,
    }

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
            placemark = (Placemark) intent
                    .getParcelableExtra(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCustomTitleBarText(placemark.getTrimmedName());
        initiateWebViewRequest();
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

    public void onRetriveWikipediaUrlSuccess(final String url) {
        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(final WebView view, final int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(final WebView view, final int errorCode,
                                        final String description, final String failingUrl) {
                onRetriveWikipediaUrlFailure();
            }
        });
        webView.loadUrl(url);
    }

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

    private void configureWebView() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Uri.parse(url).getHost().equals("www.wikipedia.org")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }

    private void initiateWebViewRequest() {
        wikipediaModel = new WikipediaModel();
        state = WikipediaActivityWebViewLoadedState.WikipediaActivityWebViewLoadedStateOK;
        wikipediaModel.setDelegate(this);
        wikipediaModel.execute(placemark.getName(), getString(R.string.wikipedia_url));
    }
}
