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

package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesSharedPreferences;

public class SettingsFragment extends DialogFragment implements
		OnCheckedChangeListener {

	private CheckBox checkBox;

	public SettingsFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_settings, container);
		getDialog().setTitle(getString(R.string.action_settings));
		return view;
	}

	@Override
	public void onStart() {
		super.onStart();
		Dialog dialog = getDialog();
		if (dialog != null) {
			dialog.setCanceledOnTouchOutside(true);
			WindowManager wm = (WindowManager) getActivity().getSystemService(
					Context.WINDOW_SERVICE);
			Display display = wm.getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			dialog.getWindow().setLayout(size.x / 100 * 80, size.y / 100 * 80);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		checkBox = (CheckBox) getView().findViewById(
				R.id.fragment_settings_analytics_checkbox);
		checkBox.setOnCheckedChangeListener(this);
		checkBox.setChecked(BluePlaquesSharedPreferences
				.getAnalyticsEnabled(getActivity()));
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		GoogleAnalytics.getInstance(getActivity()).setAppOptOut(!isChecked);
		BluePlaquesSharedPreferences.saveAnalyticsEnabled(getActivity(),
				isChecked);
	}
}
