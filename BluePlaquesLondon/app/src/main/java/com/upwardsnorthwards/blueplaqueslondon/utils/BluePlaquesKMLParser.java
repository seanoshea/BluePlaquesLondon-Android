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

package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;
import android.util.Log;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BluePlaquesKMLParser {

    private static final String TAG = "BluePlaquesKMLParser";

    private List<Placemark> massagedPlacemarks = new ArrayList<Placemark>();
    private List<Placemark> placemarks = new ArrayList<Placemark>();
    private Map<String, List<Integer>> keyToArrayPositions = new HashMap<String, List<Integer>>();
    private Placemark currentPlacemark;

    private static final String COORDINATES_KEY = "coordinates";
    private static final String DESCRIPTION_KEY = "description";
    private static final String NAME_KEY = "name";
    private static final String PLACEMARK_KEY = "placemark";
    private static final String STYLE_URL_KEY = "styleUrl";

    private boolean processingNameTag = false;
    private boolean processingDescriptionTag = false;
    private boolean processingCoordinateTag = false;
    private boolean processingStyleUrlTag = false;

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
            Log.e(TAG, "An error occurred when parsing the list of placemarks from the kml file", e);
        }
        conslidateDuplicates();
    }

    private void digestCoordinates(String input) {
        String[] parts = input.split(",");
        if (parts.length == 3) {
            try {
                currentPlacemark.setLatitude(Double.parseDouble(parts[1]));
                currentPlacemark.setLongitude(Double.parseDouble(parts[0]));
            } catch (Exception e) {
                Log.e(TAG, "An error occurred when parsing the lat and long of a placemark", e);
            }
        }
    }

    private void conslidateDuplicates() {
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

    public List<Placemark> getPlacemarks() {
        return placemarks;
    }

    public void setPlacemarks(List<Placemark> placemarks) {
        this.placemarks = placemarks;
    }

    public Map<String, List<Integer>> getKeyToArrayPositions() {
        return keyToArrayPositions;
    }

    public void setKeyToArrayPositions(
            Map<String, List<Integer>> keyToArrayPositions) {
        this.keyToArrayPositions = keyToArrayPositions;
    }

    public Placemark getCurrentPlacemark() {
        return currentPlacemark;
    }

    public List<Placemark> getMassagedPlacemarks() {
        return massagedPlacemarks;
    }

    public void setMassagedPlacemarks(List<Placemark> massagedPlacemarks) {
        this.massagedPlacemarks = massagedPlacemarks;
    }
}
