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

package com.upwardsnorthwards.blueplaqueslondon.utils;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.upwardsnorthwards.blueplaqueslondon.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.model.MapModel;

public class BluePlaquesKMLParser extends DefaultHandler {

	private static final String COORDINATES_KEY = "coordinates";
	private static final String DESCRIPTION_KEY = "description";
	private static final String NAME_KEY = "name";
	private static final String PLACEMARK_KEY = "placemark";

	private boolean processingNameTag = false;
	private boolean processingDescriptionTag = false;
	private boolean processingCoordinateTag = false;

	private MapModel mapModel = new MapModel();

	public MapModel getMapModel() {
		return this.mapModel;
	}

	@Override
	public void startDocument() throws SAXException {
		this.mapModel = new MapModel();
	}

	@Override
	public void endDocument() throws SAXException {

	}

	@Override
	public void startElement(String namespaceURI, String localName,
			String qName, Attributes atts) throws SAXException {

		if (qName.equalsIgnoreCase(PLACEMARK_KEY)) {
			mapModel.setCurrentPlacemark(new Placemark());
		} else if (qName.equalsIgnoreCase(NAME_KEY)) {
			this.processingNameTag = true;
		} else if (qName.equalsIgnoreCase(DESCRIPTION_KEY)) {
			this.processingDescriptionTag = true;
		} else if (qName.equalsIgnoreCase(COORDINATES_KEY)) {
			this.processingCoordinateTag = true;
		}
	}

	/**
	 * Gets be called on closing tags like: </tag>
	 */
	@Override
	public void endElement(String namespaceURI, String localName, String qName)
			throws SAXException {

		if (qName.equalsIgnoreCase(PLACEMARK_KEY)) {
			mapModel.addCurrentPlacemark();
		} else if (qName.equalsIgnoreCase(NAME_KEY)) {
			this.processingNameTag = false;
		} else if (qName.equalsIgnoreCase(DESCRIPTION_KEY)) {
			this.processingDescriptionTag = false;
		} else if (qName.equalsIgnoreCase(COORDINATES_KEY)) {
			this.processingCoordinateTag = false;
		}
	}

	/**
	 * Gets be called on the following structure: <tag>characters</tag>
	 */
	@Override
	public void characters(char ch[], int start, int length) {
		if (this.processingNameTag) {
			if (mapModel.getCurrentPlacemark() == null)
				mapModel.setCurrentPlacemark(new Placemark());
			mapModel.getCurrentPlacemark().setTitle(
					new String(ch, start, length));
		} else if (this.processingDescriptionTag) {
			if (mapModel.getCurrentPlacemark() == null) {
				mapModel.setCurrentPlacemark(new Placemark());
			}
			mapModel.getCurrentPlacemark().setFeatureDescription(
					new String(ch, start, length));
		} else if (this.processingCoordinateTag) {
			if (mapModel.getCurrentPlacemark() == null) {
				mapModel.setCurrentPlacemark(new Placemark());
			}
			String[] parts = new String(ch, start, length).split(",");
			if (parts.length == 3) {
				mapModel.getCurrentPlacemark().setLatitude(
						Double.parseDouble(parts[0]));
				mapModel.getCurrentPlacemark().setLongitude(
						Double.parseDouble(parts[1]));
			}
		}
	}
}
