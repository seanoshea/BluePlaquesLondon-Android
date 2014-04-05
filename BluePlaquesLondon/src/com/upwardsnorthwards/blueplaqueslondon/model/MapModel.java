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

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.content.Context;

import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesKMLParser;

public class MapModel {

	private List<Placemark> placemarks = new ArrayList<Placemark>();
	private Placemark currentPlacemark;

	public void loadMapData(Context context) {

		try {

			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();

			XMLReader xr = sp.getXMLReader();

			BluePlaquesKMLParser navSax2Handler = new BluePlaquesKMLParser();
			xr.setContentHandler(navSax2Handler);

			xr.parse(new InputSource(context.getAssets()
					.open("blueplaques.kml")));

			MapModel mm = navSax2Handler.getParsedData();

			int s = mm.placemarks.size();

			for (Placemark placemark : mm.placemarks) {

				System.out.println("Placemark " + placemark);
			}

			System.out.println("The size is " + s);

			int j = 0;

		} catch (Exception e) {

			e.printStackTrace();
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
