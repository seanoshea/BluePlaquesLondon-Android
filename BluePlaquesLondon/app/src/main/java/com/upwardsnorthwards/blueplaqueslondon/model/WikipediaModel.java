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

package com.upwardsnorthwards.blueplaqueslondon.model;

import android.os.AsyncTask;

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

public class WikipediaModel extends AsyncTask<String, String, String> {

    private static final String WIKIPEDIA_SEARCH_URL_FORMAT = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=%s&srprop=timestamp&format=json";
    private static final String WIKIPEDIA_MODEL_ENCODING = "UTF-8";
    private String responseUrl;

    private IWikipediaModelDelegate delegate;

    public void onPause() {
        this.cancel(true);
    }

    @Override
    protected String doInBackground(final String... params) {
        final String name = params[0];
        responseUrl = params[1];
        final HttpClient httpclient = new DefaultHttpClient();
        String responseString = null;
        try {
            final String url = String.format(WIKIPEDIA_SEARCH_URL_FORMAT,
                    URLEncoder.encode(name, WIKIPEDIA_MODEL_ENCODING));
            final HttpGet get = new HttpGet(url);
            final HttpResponse response = httpclient.execute(get);
            final StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString(WIKIPEDIA_MODEL_ENCODING);
            } else {
                // Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            delegate.onRetriveWikipediaUrlFailure();
        } catch (IOException e) {
            delegate.onRetriveWikipediaUrlFailure();
        }
        return responseString;
    }

    @Override
    protected void onPostExecute(final String result) {
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
                    delegate.onRetriveWikipediaUrlSuccess(String.format(responseUrl, title.replace(" ", "_")));
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

    public void setDelegate(IWikipediaModelDelegate delegate) {
        this.delegate = delegate;
    }
}