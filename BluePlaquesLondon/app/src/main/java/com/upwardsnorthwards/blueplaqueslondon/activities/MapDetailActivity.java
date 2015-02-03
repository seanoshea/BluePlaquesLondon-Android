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

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.adapters.MultiplePlacemarksAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import java.util.List;

public class MapDetailActivity extends BaseActivity implements
        OnClickListener {

    private List<Placemark> placemarks;
    private Placemark currentPlacemark;

    private TextView occupationTextView;
    private TextView addressTextView;
    private TextView councilAndYearTextView;
    private TextView noteTextView;
    private Button streetViewButton;
    private Button wikipediaArticleButton;
    private Button moreButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_map_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.title_bar);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPlacemarksFromIntent();
        setupViews();
    }

    @Override
    public void onClick(final View v) {
        if (v.equals(streetViewButton)) {
            streetViewButtonClicked();
        } else if (v.equals(wikipediaArticleButton)) {
            wikipediaArticleButtonClicked();
        } else if (v.equals(moreButton)) {
            moreButtonClicked();
        }
    }

    protected void switchToPlacemark(final Placemark placemark) {
        placemarks.remove(placemark);
        placemarks.add(0, placemark);
        addTextToTextViews();
    }

    private void getPlacemarksFromIntent() {
        final Intent intent = getIntent();
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
        moreButton = (Button) findViewById(R.id.activity_map_details_more);
        addClickListenersToButtons();
        addTextToTextViews();
    }

    private void addClickListenersToButtons() {
        streetViewButton.setOnClickListener(this);
        wikipediaArticleButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
        moreButton.setVisibility(placemarks.size() == 1 ? View.GONE
                : View.VISIBLE);
    }

    private void addTextToTextViews() {
        // take the first one from the list of placemarks and run with it
        currentPlacemark = placemarks.get(0);
        setCustomTitleBarText(currentPlacemark.getName());
        occupationTextView.setText(currentPlacemark.getOccupation());
        addressTextView.setText(currentPlacemark.getAddress());
        final String councilAndYear = currentPlacemark.getCouncilAndYear();
        if (councilAndYear != null) {
            councilAndYearTextView.setText(councilAndYear);
            councilAndYearTextView.setVisibility(View.VISIBLE);
        } else {
            councilAndYearTextView.setVisibility(View.GONE);
        }
        final String note = currentPlacemark.getNote();
        if (note != null) {
            noteTextView.setText(note);
            noteTextView.setVisibility(View.VISIBLE);
        } else {
            noteTextView.setVisibility(View.GONE);
        }
    }

    private void moreButtonClicked() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.multiple_placemarks_select_one));
        final MultiplePlacemarksAdapter arrayAdapter = new MultiplePlacemarksAdapter(
                this, R.layout.multiple_placemarks_item, placemarks);
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(final DialogInterface dialog, final int which) {
                        dialog.dismiss();
                    }
                });

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(final DialogInterface dialog, final int which) {
                switchToPlacemark(arrayAdapter.getItem(which));
            }
        });
        builder.show();
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.DETAILS_BUTTON_PRESSED_EVENT, placemarks
                        .get(0).getTitle());
    }

    private void wikipediaArticleButtonClicked() {
        final Placemark placemark = placemarks.get(0);
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.WIKIPEDIA_BUTTON_PRESSED_EVENT,
                placemark.getTitle());
        final Intent intent = new Intent(this, WikipediaActivity.class);
        intent.putExtra(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY,
                placemark);
        startActivity(intent);
    }

    private void streetViewButtonClicked() {
        final Placemark placemark = placemarks.get(0);
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.STREETVIEW_BUTTON_PRESSED_EVENT,
                placemark.getTitle());
        final Intent intent = new Intent(this, PanoramaActivity.class);
        intent.putExtra(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY,
                placemark);
        startActivity(intent);
    }
}