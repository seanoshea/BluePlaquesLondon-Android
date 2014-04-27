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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnQueryTextListener;

import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.adapters.SearchAdapter.ViewHolder;
import com.upwardsnorthwards.blueplaqueslondon.fragments.AboutFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.MapFragment;
import com.upwardsnorthwards.blueplaqueslondon.fragments.SettingsFragment;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

public class MainActivity extends FragmentActivity implements
		OnFocusChangeListener, OnQueryTextListener, OnItemClickListener {

	private ListView searchListView;
	private SearchAdapter searchAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
		SearchView searchView = (SearchView) menu.findItem(R.id.search)
				.getActionView();
		searchView.setOnQueryTextFocusChangeListener(this);
		searchView.setOnQueryTextListener(this);
		searchView.setSearchableInfo(searchManager
				.getSearchableInfo(getComponentName()));
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		FragmentManager fm = getSupportFragmentManager();
		switch (item.getItemId()) {
		case R.id.action_about:
			AboutFragment aboutFragment = new AboutFragment();
			aboutFragment.show(fm, "fragment_about");
			break;
		case R.id.action_settings:
			SettingsFragment settingsFragment = new SettingsFragment();
			settingsFragment.show(fm, "fragment_settings");
			break;
		default:
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setupSearchListView();
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if (!hasFocus) {
			toggleSearchListVisibility(false, null);
		}
	}

	@Override
	public boolean onQueryTextChange(String newText) {
		toggleSearchListVisibility(newText.length() > 0, newText);
		return false;
	}

	@Override
	public boolean onQueryTextSubmit(String query) {
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		toggleSearchListVisibility(false, null);
		MapFragment mapFragment = getMapFragment();
		if (mapFragment != null) {
			ViewHolder holder = (ViewHolder) view.getTag();
			// if the holder doesnt have a placemark associated with it,
			// it must be the 'find the closest plaque' item
			Placemark placemark = holder.placemark;
			if (placemark == null) {
				placemark = getClosestPlacemark();
			}
			mapFragment.navigateToPlacemark(placemark);
		}
	}

	private void toggleSearchListVisibility(boolean show, String newText) {
		View searchList = findViewById(R.id.search_list);
		View map = findViewById(R.id.map);
		if (map != null && searchList != null) {
			searchList.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
			map.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
			if (show) {
				searchAdapter.setPlacemarks(filterPlacemarksWithText(newText));
				searchAdapter.notifyDataSetChanged();
			}
		}
	}

	private List<Placemark> filterPlacemarksWithText(String newText) {
		List<Placemark> placemarks = new ArrayList<Placemark>();
		MapFragment mapFragment = getMapFragment();
		if (mapFragment != null) {
			for (Placemark placemark : mapFragment.getModel()
					.getMassagedPlacemarks()) {
				if (placemark.getName().toLowerCase()
						.contains(newText.toLowerCase())) {
					placemarks.add(placemark);
				}
			}
			// alphabetically sort 'em
			Collections.sort(placemarks, new Comparator<Placemark>() {
				@Override
				public int compare(Placemark lhs, Placemark rhs) {
					return lhs.getName().compareTo(rhs.getName());
				}
			});
		}
		return placemarks;
	}

	private void setupSearchListView() {
		searchListView = (ListView) findViewById(R.id.search_list);
		searchAdapter = new SearchAdapter(this, R.layout.search_item,
				new ArrayList<Placemark>());
		searchListView.setOnItemClickListener(this);
		searchListView.setAdapter(searchAdapter);
	}

	private MapFragment getMapFragment() {
		MapFragment mapFragment = null;
		List<Fragment> fragments = getSupportFragmentManager().getFragments();
		if (fragments != null && fragments.size() > 0) {
			mapFragment = (MapFragment) fragments.get(0);
		}
		return mapFragment;
	}

	private Placemark getClosestPlacemark() {
		Placemark closestPlacemark = null;
		BluePlaquesLondonApplication application = (BluePlaquesLondonApplication) getApplication();
		Location currentLocation = application.getCurrentLocation();
		MapFragment mapFragment = getMapFragment();
		if (mapFragment != null) {
			closestPlacemark = mapFragment.getModel()
					.getPlacemarkClosestToPlacemark(currentLocation);
		}
		return closestPlacemark;
	}
}