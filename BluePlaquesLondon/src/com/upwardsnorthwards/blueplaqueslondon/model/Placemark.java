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

import android.util.Log;

public class Placemark {

	private static String OverlayTitleDelimiter = "<br>";
	private static String NameDelimiter = "(";
	private static String EmphasisNoteOpeningTag = "<em>";
	private static String EmphasisNoteClosingTag = "</em>";

	private String featureDescription;
	private String title;
	private String name;
	private String occupation;
	private String address;
	private String note;
	private String councilAndYear;

	private String styleUrl;
	private double latitude;
	private double longitude;
	private int placemarkPinType;

	public String key() {
		return Double.toString(latitude) + Double.toString(longitude);
	}

	public void digestFeatureDescription() {
		if (featureDescription != null) {
			// these must be done in order ...
			digestTitle();
			digestName();
			digestOccupation();
			digestAddress();
			digestNote();
			digestCouncilAndYear();
		}
	}

	private void digestTitle() {
		this.title = featureDescription;
		int index = featureDescription.indexOf(OverlayTitleDelimiter);
		if (index != -1) {
			title = featureDescription.substring(0, index);
		}
		this.title = this.trimWhitespaceFromString(this.title);
	}

	private void digestName() {
		this.name = title;
		int startOfYears = this.name.indexOf(NameDelimiter);
		if (startOfYears != -1) {
			this.name = this.name.replaceAll(EmphasisNoteOpeningTag, "");
			this.name = this.name.replaceAll(EmphasisNoteClosingTag, "");
			this.name = this.name.substring(0, startOfYears);
		}
		this.name = this.trimWhitespaceFromString(this.name);
	}

	private void digestOccupation() {
		occupation = featureDescription;
		int index = featureDescription.indexOf(OverlayTitleDelimiter);
		if (index != -1) {
			occupation = occupation.substring(index);
			int start = occupation.indexOf(OverlayTitleDelimiter);
			if (start == 0) {
				try {
					int delimiterLength = OverlayTitleDelimiter.length();
					int end = occupation.indexOf(OverlayTitleDelimiter, start
							+ delimiterLength);
					this.occupation = this.trimWhitespaceFromString(occupation
							.substring(start + delimiterLength, end));
					if (occupation.length() == 9) {
						// TODO - parse components
					}
				} catch (java.lang.StringIndexOutOfBoundsException e) {

					Log.v("ASD", "ERROR " + featureDescription);
				}
			}
		}
		occupation = this.trimWhitespaceFromString(occupation);
	}

	private void digestAddress() {
		String[] components = this.featureDescription
				.split(OverlayTitleDelimiter);
		if (components.length != 0) {
			switch (components.length) {
			case 2:
			case 3: {
				address = this.trimWhitespaceFromString(components[1]);
			}
				break;
			case 4:
			case 5: {
				address = this.trimWhitespaceFromString(components[2]);
			}
				break;
			case 6: {
				address = this.trimWhitespaceFromString(components[3]);
			}
				break;
			case 7: {
				address = this.trimWhitespaceFromString(components[4]);
			}
				break;
			}
		}
	}

	private void digestNote() {
		int startOfEmphasis = this.featureDescription
				.indexOf(EmphasisNoteOpeningTag);
		if (startOfEmphasis != -1) {
			int endOfEmphasisIndex = this.featureDescription
					.indexOf(EmphasisNoteClosingTag);
			if (endOfEmphasisIndex == -1) {
				// some notes don't have the correct closing tag ... search for
				// the starting tag again
				int locationOfLastEmphasis = this.featureDescription
						.lastIndexOf(EmphasisNoteOpeningTag);
				if (locationOfLastEmphasis != startOfEmphasis) {
					endOfEmphasisIndex = this.featureDescription.length()
							- EmphasisNoteOpeningTag.length();
				} else {
					endOfEmphasisIndex = this.featureDescription.length();
				}
			}
			note = this.featureDescription.substring(startOfEmphasis
					+ EmphasisNoteOpeningTag.length(), endOfEmphasisIndex);
			note = this.trimWhitespaceFromString(note);
		}
	}

	private void digestCouncilAndYear() {
		String withoutNote = this.removeNoteFromString(this.featureDescription);
		String[] components = withoutNote.split(OverlayTitleDelimiter);
		if (components.length > 2) {
			councilAndYear = this.trimWhitespaceFromString(components[1]);
		}
	}

	private String removeNoteFromString(String input) {
		String inputWithNoteRemoved = input;
		if (note != null) {
			inputWithNoteRemoved = this
					.trimWhitespaceFromString(inputWithNoteRemoved);
			inputWithNoteRemoved = inputWithNoteRemoved.replaceAll(
					EmphasisNoteOpeningTag, "");
			inputWithNoteRemoved = inputWithNoteRemoved.replaceAll(note, "");
			inputWithNoteRemoved = inputWithNoteRemoved.replaceAll(
					EmphasisNoteClosingTag, "");
			// check for a trailing delimiter
			int locationOfFinalDelimiter = inputWithNoteRemoved
					.lastIndexOf(OverlayTitleDelimiter);
			if (locationOfFinalDelimiter != -1
					&& locationOfFinalDelimiter == inputWithNoteRemoved
							.length() - OverlayTitleDelimiter.length()) {
				inputWithNoteRemoved.substring(locationOfFinalDelimiter);
			}
		}
		return inputWithNoteRemoved;
	}

	private String trimWhitespaceFromString(String string) {

		return string;
	}

	@Override
	public String toString() {
		String description = this.key() + " " + this.title + " " + this.name
				+ " occupation " + this.occupation + " " + this.note + " "
				+ this.councilAndYear;
		return description;
	}

	public String getFeatureDescription() {
		return featureDescription;
	}

	public void setFeatureDescription(String featureDescription) {
		
		Log.v("SEAN ", featureDescription);
		
		this.featureDescription = featureDescription;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getOccupation() {
		return occupation;
	}

	public void setOccupation(String occupation) {
		this.occupation = occupation;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getCouncilAndYear() {
		return councilAndYear;
	}

	public void setCouncilAndYear(String councilAndYear) {
		this.councilAndYear = councilAndYear;
	}

	public String getStyleUrl() {
		return styleUrl;
	}

	public void setStyleUrl(String styleUrl) {
		this.styleUrl = styleUrl;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public int getPlacemarkPinType() {
		return placemarkPinType;
	}

	public void setPlacemarkPinType(int placemarkPinType) {
		this.placemarkPinType = placemarkPinType;
	}
}
