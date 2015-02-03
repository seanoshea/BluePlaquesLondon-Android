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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class WikipediaActivity extends BaseActivity {

    private WebView webView;
    private Placemark placemark;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
        new WikipediaModel().execute(placemark.getName(),
                getString(R.string.wikipedia_url));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCustomTitleBarText(placemark.getTitle());
    }

    protected void onRetriveWikipediaUrlSuccess(String url) {
        final Activity activity = this;
        webView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                activity.setProgress(progress * 1000);
            }
        });
        webView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                onRetriveWikipediaUrlFailure();
            }
        });
        webView.loadUrl(url);
    }

    protected void onRetriveWikipediaUrlFailure() {
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
        app.trackEvent(BluePlaquesConstants.ERROR_CATEGORY,
                BluePlaquesConstants.WIKIPEDIA_PAGE_LOAD_ERROR_EVENT,
                placemark.getName());
    }

    public class WikipediaModel extends AsyncTask<String, String, String> {

        private static final String WIKIPEDIA_SEARCH_URL_FORMAT = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=%s&srprop=timestamp&format=json";
        private String responseUrl;

        @Override
        protected String doInBackground(String... params) {
            final String name = params[0];
            responseUrl = params[1];
            final HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                final String url = String.format(WIKIPEDIA_SEARCH_URL_FORMAT,
                        URLEncoder.encode(name, "UTF-8"));
                final HttpGet get = new HttpGet(url);
                response = httpclient.execute(get);
                final StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);
                    out.close();
                    responseString = out.toString();
                } else {
                    // Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                onRetriveWikipediaUrlFailure();
            } catch (IOException e) {
                onRetriveWikipediaUrlFailure();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                if (result.length() > 0) {
                    final JSONObject jObject = new JSONObject(result);
                    final JSONObject query = jObject.getJSONObject("query");
                    final JSONArray search = query.getJSONArray("search");
                    if (search.length() > 0) {
                        // take the first result ...
                        final JSONObject wikipediaArticle = (JSONObject) search
                                .get(0);
                        final String title = wikipediaArticle.getString("title");
                        onRetriveWikipediaUrlSuccess(String.format(responseUrl,
                                title.replace(" ", "_")));
                    } else {
                        onRetriveWikipediaUrlFailure();
                    }
                } else {
                    onRetriveWikipediaUrlFailure();
                }
            } catch (JSONException e) {
                onRetriveWikipediaUrlFailure();
            }
        }
    }
}
