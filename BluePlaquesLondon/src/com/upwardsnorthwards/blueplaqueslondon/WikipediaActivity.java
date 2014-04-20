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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

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

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.webkit.WebView;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

public class WikipediaActivity extends Activity {

	private WebView webView;
	private Placemark placemark;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wikipedia);
		webView = (WebView) findViewById(R.id.activity_wikipedia_web_view);
		Intent intent = getIntent();
		if (intent != null) {
			placemark = (Placemark) intent
					.getParcelableExtra(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY);
		}
		new WikipediaModel().execute(placemark.getName(),
				getString(R.string.wikipedia_url));
	}

	protected void onRetriveWikipediaUrlSuccess(String url) {
		webView.loadUrl(url);
	}

	protected void onRetriveWikipediaUrlFailure() {

	}

	public class WikipediaModel extends AsyncTask<String, String, String> {

		private static final String WIKIPEDIA_SEARCH_URL_FORMAT = "http://en.wikipedia.org/w/api.php?action=query&list=search&srsearch=%s&srprop=timestamp&format=json";
		private String responseUrl;

		@Override
		protected String doInBackground(String... params) {
			String name = params[0];
			responseUrl = params[1];

			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response;
			String responseString = null;
			try {
				String url = String.format(WIKIPEDIA_SEARCH_URL_FORMAT,
						URLEncoder.encode(name, "UTF-8"));
				HttpGet get = new HttpGet(url);
				response = httpclient.execute(get);
				StatusLine statusLine = response.getStatusLine();
				if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					response.getEntity().writeTo(out);
					out.close();
					responseString = out.toString();
				} else {
					// Closes the connection.
					response.getEntity().getContent().close();
					throw new IOException(statusLine.getReasonPhrase());
				}
			} catch (ClientProtocolException e) {
				// TODO Handle problems..
			} catch (IOException e) {
				// TODO Handle problems..
			}
			return responseString;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				if (result.length() > 0) {
					JSONObject jObject = new JSONObject(result);
					JSONObject query = jObject.getJSONObject("query");
					JSONArray search = query.getJSONArray("search");
					if (search.length() > 0) {
						// take the first result ...
						JSONObject wikipediaArticle = (JSONObject) search
								.get(0);
						String title = wikipediaArticle.getString("title");
						onRetriveWikipediaUrlSuccess(String.format(responseUrl,
								title.replace(" ", "_")));
					} else {
						onRetriveWikipediaUrlFailure();
					}
				} else {
					onRetriveWikipediaUrlFailure();
				}
			} catch (JSONException e) {
				e.printStackTrace();
				onRetriveWikipediaUrlFailure();
			}
		}
	}
}
