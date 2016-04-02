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

package com.upwardsnorthwards.blueplaqueslondon.model;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Used by the <code>WikipediaActivity</code> to reteieve the URL associated with a placemark.
 */
public class WikipediaModel extends AsyncTask<String, String, WikipediaModelSearchResult> {

    private static final String WIKIPEDIA_SEARCH_URL_FORMAT = "https://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=%s&srprop=timestamp&format=json";
    private static final String WIKIPEDIA_MODEL_ENCODING = "UTF-8";
    private static final String WIKIPEDIA_MODEL_LOCATION_STRING = "Location";
    private String responseUrl;
    private IWikipediaModelDelegate delegate;

    public void onPause() {
        this.cancel(true);
    }

    @Nullable
    @Override
    protected WikipediaModelSearchResult doInBackground(final String... params) {
        final String name = params[0];
        responseUrl = params[1];
        StringBuilder result = new StringBuilder();
        String responseString = null;
        HttpURLConnection urlConnection = null;
        URL url;
        try {
            url = new URL(String.format(WIKIPEDIA_SEARCH_URL_FORMAT,
                    URLEncoder.encode(name, WIKIPEDIA_MODEL_ENCODING)));
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setInstanceFollowRedirects(true);
            int status = urlConnection.getResponseCode();
            if (status != HttpURLConnection.HTTP_OK && (status == HttpURLConnection.HTTP_MOVED_TEMP
                    || status == HttpURLConnection.HTTP_MOVED_PERM
                    || status == HttpURLConnection.HTTP_SEE_OTHER)) {
                String newUrl = urlConnection.getHeaderField(WIKIPEDIA_MODEL_LOCATION_STRING);
                urlConnection = (HttpURLConnection) new URL(newUrl).openConnection();
            }
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, WIKIPEDIA_MODEL_ENCODING));
            String line;
            while ((line = reader.readLine()) != null) {
                result.append(line);
            }
            responseString = result.toString();
        } catch (IOException e) {
            delegate.onRetriveWikipediaUrlFailure();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new WikipediaModelSearchResult(responseString, name);
    }

    @Override
    protected void onPostExecute(@NonNull final WikipediaModelSearchResult searchResult) {
        super.onPostExecute(searchResult);
        try {
            if (searchResult.hasResult()) {
                final JSONObject jObject = new JSONObject(searchResult.getResult());
                final JSONObject query = jObject.getJSONObject("query");
                final JSONArray search = query.getJSONArray("search");
                if (search.length() > 0) {
                    final String title = this.findTitleInSearchResults(search, searchResult.getName());
                    delegate.onRetriveWikipediaUrlSuccess(String.format(responseUrl, title != null ? title.replace(" ", "_") : null));
                } else {
                    delegate.onRetriveWikipediaUrlFailure();
                }
            } else {
                delegate.onRetriveWikipediaUrlFailure();
            }
        } catch (JSONException e) {
            delegate.onRetriveWikipediaUrlFailure();
        }
    }

    @Nullable
    private String findTitleInSearchResults(@NonNull JSONArray searchResults, @NonNull String name) throws JSONException {
        String title = null;
        // see if we can find the title in the searchResults first
        for (int i = 0; i < searchResults.length(); i++) {
            final JSONObject wikipediaArticle = searchResults.getJSONObject(i);
            final String searchResultTitle = wikipediaArticle.getString("title");
            final String[] titleComponents = searchResultTitle.split(" ");
            if (titleComponents.length > 0 && name.contains(titleComponents[0])) {
                title = searchResultTitle;
                break;
            }
        }
        if (title == null) {
            // take the first result ...
            final JSONObject wikipediaArticle = (JSONObject) searchResults
                    .get(0);
            title = wikipediaArticle.getString("title");
        }
        return title;
    }

    public void setDelegate(IWikipediaModelDelegate delegate) {
        this.delegate = delegate;
    }

    @SuppressWarnings("SameParameterValue")
    public void setResponseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
    }
}