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
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;

public class MapModel {

	private static final String COORDINATES_KEY = "coordinates";
	private static final String DESCRIPTION_KEY = "description";
	private static final String NAME_KEY = "name";
	private static final String PLACEMARK_KEY = "placemark";

	private boolean processingNameTag = false;
	private boolean processingDescriptionTag = false;
	private boolean processingCoordinateTag = false;

	private List<Placemark> placemarks = new ArrayList<Placemark>();
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
						setCurrentPlacemark(new Placemark());
					} else if (qName.equalsIgnoreCase(NAME_KEY)) {
						processingNameTag = true;
					} else if (qName.equals(DESCRIPTION_KEY)) {
						processingDescriptionTag = true;
					} else if (qName.equalsIgnoreCase(COORDINATES_KEY)) {
						processingCoordinateTag = true;
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
					}
				} else if (eventType == XmlPullParser.TEXT) {
					if (this.processingNameTag) {
						if (getCurrentPlacemark() == null)
							setCurrentPlacemark(new Placemark());
						getCurrentPlacemark().setTitle(xpp.getText());
					} else if (this.processingDescriptionTag) {
						if (getCurrentPlacemark() == null) {
							setCurrentPlacemark(new Placemark());
						}
						getCurrentPlacemark().setFeatureDescription(
								xpp.getText());
						getCurrentPlacemark().digestFeatureDescription();
						this.processingDescriptionTag = false;
					} else if (this.processingCoordinateTag) {
						if (getCurrentPlacemark() == null) {
							setCurrentPlacemark(new Placemark());
						}
						digestCoordinates(xpp.getText());
					}
				}
				eventType = xpp.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void digestCoordinates(String input) {
		String[] parts = input.split(",");
		if (parts.length == 3) {
			try {
				getCurrentPlacemark().setLatitude(Double.parseDouble(parts[1]));
				getCurrentPlacemark()
						.setLongitude(Double.parseDouble(parts[0]));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void addCurrentPlacemark() {
		placemarks.add(currentPlacemark);
	}

	public List<Placemark> getPlacemarks() {
		return placemarks;
	}

	public void setPlacemarks(ArrayList<Placemark> placemarks) {
		this.placemarks = placemarks;
	}

	public Placemark getCurrentPlacemark() {
		return currentPlacemark;
	}

	public void setCurrentPlacemark(Placemark currentPlacemark) {
		this.currentPlacemark = currentPlacemark;
	}
}
