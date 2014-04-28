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

import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.adapters.MultiplePlacemarksAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

public class MapDetailActivity extends FragmentActivity implements
		OnClickListener {

	private List<Placemark> placemarks;
	private Placemark currentPlacemark;

	private TextView occupationTextView;
	private TextView addressTextView;
	private TextView councilAndYearTextView;
	private TextView noteTextView;
	private Button streetViewButton;
	private Button wikipediaArticleButton;
	private Button directionsButton;
	private Button moreButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map_detail);
	}

	@Override
	protected void onResume() {
		super.onResume();
		getPlacemarksFromIntent();
		setupViews();
	}

	@Override
	public void onClick(View v) {
		if (v.equals(streetViewButton)) {
			streetViewButtonClicked();
		} else if (v.equals(wikipediaArticleButton)) {
			wikipediaArticleButtonClicked();
		} else if (v.equals(directionsButton)) {
			directionsButtonClicked();
		} else if (v.equals(moreButton)) {
			moreButtonClicked();
		}
	}

	protected void switchToPlacemark(Placemark placemark) {
		placemarks.remove(placemark);
		placemarks.add(0, placemark);
		addTextToTextViews();
	}

	private void getPlacemarksFromIntent() {
		Intent intent = getIntent();
		if (intent != null) {
			placemarks = intent
					.getParcelableArrayListExtra(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY);
		}
	}

	private void setupViews() {
		occupationTextView = (TextView) findViewById(R.id.activity_map_details_occupation);
		addressTextView = (TextView) findViewById(R.id.activity_map_details_address);
		councilAndYearTextView = (TextView) findViewById(R.id.activity_map_details_council_and_year);
		noteTextView = (TextView) findViewById(R.id.activity_map_details_note);
		streetViewButton = (Button) findViewById(R.id.activity_map_details_street_view);
		wikipediaArticleButton = (Button) findViewById(R.id.activity_map_details_wikipedia_article);
		directionsButton = (Button) findViewById(R.id.activity_map_details_directions);
		moreButton = (Button) findViewById(R.id.activity_map_details_more);
		addClickListenersToButtons();
		addTextToTextViews();
	}

	private void addClickListenersToButtons() {
		streetViewButton.setOnClickListener(this);
		wikipediaArticleButton.setOnClickListener(this);
		directionsButton.setOnClickListener(this);
		moreButton.setOnClickListener(this);
		moreButton.setVisibility(placemarks.size() == 1 ? View.GONE
				: View.VISIBLE);
	}

	private void addTextToTextViews() {
		// take the first one from the list of placemarks and run with it
		currentPlacemark = placemarks.get(0);
		setTitle(currentPlacemark.getTitle());
		occupationTextView.setText(currentPlacemark.getOccupation());
		addressTextView.setText(currentPlacemark.getAddress());
		String councilAndYear = currentPlacemark.getCouncilAndYear();
		if (councilAndYear != null) {
			councilAndYearTextView.setText(councilAndYear);
			councilAndYearTextView.setVisibility(View.VISIBLE);
		} else {
			councilAndYearTextView.setVisibility(View.GONE);
		}
		String note = currentPlacemark.getNote();
		if (note != null) {
			noteTextView.setText(note);
			noteTextView.setVisibility(View.VISIBLE);
		} else {
			noteTextView.setVisibility(View.GONE);
		}
	}

	private void moreButtonClicked() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(getString(R.string.multiple_placemarks_select_one));
		final MultiplePlacemarksAdapter arrayAdapter = new MultiplePlacemarksAdapter(
				this, R.layout.multiple_placemarks_item, placemarks);
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switchToPlacemark(arrayAdapter.getItem(which));
			}
		});
		builder.show();
		BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
		app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
				BluePlaquesConstants.DETAILS_BUTTON_PRESSED_EVENT, placemarks
						.get(0).getTitle());
	}

	private void directionsButtonClicked() {
		Placemark currentPlacemark = placemarks.get(0);
		BluePlaquesLondonApplication application = (BluePlaquesLondonApplication) getApplication();
		Location currentLocation = application.getCurrentLocation();
		application.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
				BluePlaquesConstants.DIRECTIONS_BUTTON_PRESSED_EVENT,
				currentPlacemark.getTitle());
		if (currentLocation != null) {
			String url = "http://maps.google.com/maps?saddr="
					+ currentLocation.getLatitude() + ","
					+ currentLocation.getLongitude() + "&daddr="
					+ currentPlacemark.getLatitude() + ","
					+ currentPlacemark.getLongitude() + "&mode=walking";
			Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
					Uri.parse(url));
			intent.setClassName("com.google.android.apps.maps",
					"com.google.android.maps.MapsActivity");
			List<ResolveInfo> intentActivities = getPackageManager()
					.queryIntentActivities(intent, 0);
			if (intentActivities.size() > 0) {
				startActivity(intent);
			} else {
				promptUserToInstallGoogleMaps();
			}
		}
	}

	private void wikipediaArticleButtonClicked() {
		Placemark placemark = placemarks.get(0);
		BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
		app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
				BluePlaquesConstants.WIKIPEDIA_BUTTON_PRESSED_EVENT,
				placemark.getTitle());
		Intent intent = new Intent(this, WikipediaActivity.class);
		intent.putExtra(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY,
				placemark);
		startActivity(intent);
	}

	private void streetViewButtonClicked() {
		BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
		app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
				BluePlaquesConstants.STREETVIEW_BUTTON_PRESSED_EVENT,
				placemarks.get(0).getTitle());
		Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
				Uri.parse("google.streetview:cbll="
						+ currentPlacemark.getLatitude() + ","
						+ currentPlacemark.getLongitude()
						+ "&cbp=1,99.56,,1,-5.27&mz=21"));
		List<ResolveInfo> intentActivities = getPackageManager()
				.queryIntentActivities(intent, 0);
		if (intentActivities.size() > 0) {
			startActivity(intent);
		} else {
			promptUserToInstallGoogleMaps();
		}
	}

	private void promptUserToInstallGoogleMaps() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.ic_launcher);
		builder.setTitle(getString(R.string.app_required));
		builder.setMessage(getString(R.string.install_google_maps));
		builder.setPositiveButton(getString(R.string.ok),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		builder.setNegativeButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});

		builder.show();
	}
}
