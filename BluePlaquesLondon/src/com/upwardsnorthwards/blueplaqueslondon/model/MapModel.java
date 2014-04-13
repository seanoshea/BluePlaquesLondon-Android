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

package com.upwardsnorthwards.blueplaqueslondon.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class MapModel {

	private static final String COORDINATES_KEY = "coordinates";
	private static final String DESCRIPTION_KEY = "description";
	private static final String NAME_KEY = "name";
	private static final String PLACEMARK_KEY = "placemark";
	private static final String STYLE_URL_KEY = "styleUrl";

	private boolean processingNameTag = false;
	private boolean processingDescriptionTag = false;
	private boolean processingCoordinateTag = false;
	private boolean processingStyleUrlTag = false;

	private List<Placemark> placemarks = new ArrayList<Placemark>();
	private List<Placemark> massagedPlacemarks = new ArrayList<Placemark>();
	private Map<String, List<Integer>> keyToArrayPositions = new HashMap<String, List<Integer>>();

	private Placemark currentPlacemark;

	public void loadMapData(Context context) {
		try {
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();
			xpp.setInput(context.getAssets().open("blueplaques.kml"), "UTF-8");
			int eventType = xpp.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if (eventType == XmlPullParser.START_TAG) {
					String qName = xpp.getName();
					if (qName.equalsIgnoreCase(PLACEMARK_KEY)) {
						currentPlacemark = new Placemark();
					} else if (qName.equalsIgnoreCase(NAME_KEY)) {
						processingNameTag = true;
					} else if (qName.equals(DESCRIPTION_KEY)) {
						processingDescriptionTag = true;
					} else if (qName.equalsIgnoreCase(COORDINATES_KEY)) {
						processingCoordinateTag = true;
					} else if (qName.equalsIgnoreCase(STYLE_URL_KEY)) {
						processingStyleUrlTag = true;
					}
				} else if (eventType == XmlPullParser.END_TAG) {
					String qName = xpp.getName();
					if (qName.equalsIgnoreCase(PLACEMARK_KEY)) {
						addCurrentPlacemark();
					} else if (qName.equalsIgnoreCase(NAME_KEY)) {
						processingNameTag = false;
					} else if (qName.equalsIgnoreCase(DESCRIPTION_KEY)) {
						processingDescriptionTag = false;
					} else if (qName.equalsIgnoreCase(COORDINATES_KEY)) {
						processingCoordinateTag = false;
					} else if (qName.equalsIgnoreCase(STYLE_URL_KEY)) {
						processingStyleUrlTag = false;
					}
				} else if (eventType == XmlPullParser.TEXT) {
					if (processingNameTag) {
						if (currentPlacemark == null)
							currentPlacemark = new Placemark();
						currentPlacemark.setTitle(xpp.getText());
					} else if (processingDescriptionTag) {
						setCurrentPlacemarkIfNecessary();
						currentPlacemark.setFeatureDescription(xpp.getText());
						currentPlacemark.digestFeatureDescription();
						processingDescriptionTag = false;
					} else if (processingCoordinateTag) {
						setCurrentPlacemarkIfNecessary();
						digestCoordinates(xpp.getText());
					} else if (processingStyleUrlTag) {
						currentPlacemark.setStyleUrl(xpp.getText());
					}
				}
				eventType = xpp.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		conslidateDuplicates();
	}

	public void conslidateDuplicates() {
		int index = 0;
		for (Placemark placemark : placemarks) {
			String key = placemark.key();
			if (!keyToArrayPositions.containsKey(key)) {
				List<Integer> positions = new ArrayList<Integer>();
				positions.add(index);
				keyToArrayPositions.put(key, positions);
				massagedPlacemarks.add(placemark);
			} else {
				List<Integer> existingPlacemarks = keyToArrayPositions.get(key);
				Placemark existingPlacemark = placemarks.get(existingPlacemarks
						.get(0));
				if (!placemark.getTitle().equals(existingPlacemark.getTitle())) {
					existingPlacemarks.add(index);
					keyToArrayPositions.put(key, existingPlacemarks);
				}
			}
			index++;
		}
	}

	private void digestCoordinates(String input) {
		String[] parts = input.split(",");
		if (parts.length == 3) {
			try {
				currentPlacemark.setLatitude(Double.parseDouble(parts[1]));
				currentPlacemark.setLongitude(Double.parseDouble(parts[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void setCurrentPlacemarkIfNecessary() {
		if (currentPlacemark == null) {
			currentPlacemark = new Placemark();
		}
	}

	public void addCurrentPlacemark() {
		placemarks.add(currentPlacemark);
	}

	public void setCurrentPlacemark(Placemark currentPlacemark) {
		this.currentPlacemark = currentPlacemark;
	}

	public List<Placemark> getMassagedPlacemarks() {
		return massagedPlacemarks;
	}

	public void setMassagedPlacemarks(List<Placemark> massagedPlacemarks) {
		this.massagedPlacemarks = massagedPlacemarks;
	}

	public Map<String, List<Integer>> getKeyToArrayPositions() {
		return keyToArrayPositions;
	}

	public void setKeyToArrayPositions(
			Map<String, List<Integer>> keyToArrayPositions) {
		this.keyToArrayPositions = keyToArrayPositions;
	}
}
