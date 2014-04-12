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
		title = featureDescription;
		int index = featureDescription.indexOf(OverlayTitleDelimiter);
		if (index != -1) {
			title = featureDescription.substring(0, index);
		}
		title = trimWhitespaceFromString(title);
	}

	private void digestName() {
		name = title;
		int startOfYears = name.indexOf(NameDelimiter);
		if (startOfYears != -1) {
			name = name.replaceAll(EmphasisNoteOpeningTag, "");
			name = name.replaceAll(EmphasisNoteClosingTag, "");
			name = name.substring(0, startOfYears);
		}
		name = trimWhitespaceFromString(name);
	}

	private void digestOccupation() {
		occupation = featureDescription;
		int index = featureDescription.indexOf(OverlayTitleDelimiter);
		if (index != -1) {
			occupation = occupation.substring(index);
			int start = occupation.indexOf(OverlayTitleDelimiter);
			if (start == 0) {
				int delimiterLength = OverlayTitleDelimiter.length();
				int end = occupation.indexOf(OverlayTitleDelimiter, start
						+ delimiterLength);
				try {
					occupation = trimWhitespaceFromString(occupation.substring(
							start + delimiterLength, end));
					if (occupation.length() == 9) {
						// TODO - parse components
					}
				} catch (Exception e) {

				}
			}
		}
		occupation = trimWhitespaceFromString(occupation);
	}

	private void digestAddress() {
		String[] components = featureDescription.split(OverlayTitleDelimiter);
		if (components.length != 0) {
			switch (components.length) {
			case 2:
			case 3: {
				address = trimWhitespaceFromString(components[1]);
			}
				break;
			case 4:
			case 5: {
				address = trimWhitespaceFromString(components[2]);
			}
				break;
			case 6: {
				address = trimWhitespaceFromString(components[3]);
			}
				break;
			case 7: {
				address = trimWhitespaceFromString(components[4]);
			}
				break;
			}
		}
	}

	private void digestNote() {
		int startOfEmphasis = featureDescription
				.indexOf(EmphasisNoteOpeningTag);
		if (startOfEmphasis != -1) {
			int endOfEmphasisIndex = featureDescription
					.indexOf(EmphasisNoteClosingTag);
			if (endOfEmphasisIndex == -1) {
				// some notes don't have the correct closing tag ... search for
				// the starting tag again
				int locationOfLastEmphasis = featureDescription
						.lastIndexOf(EmphasisNoteOpeningTag);
				if (locationOfLastEmphasis != startOfEmphasis) {
					endOfEmphasisIndex = featureDescription.length()
							- EmphasisNoteOpeningTag.length();
				} else {
					endOfEmphasisIndex = featureDescription.length();
				}
			}
			note = featureDescription.substring(startOfEmphasis
					+ EmphasisNoteOpeningTag.length(), endOfEmphasisIndex);
			note = trimWhitespaceFromString(note);
		}
	}

	private void digestCouncilAndYear() {
		String withoutNote = removeNoteFromString(featureDescription);
		String[] components = withoutNote.split(OverlayTitleDelimiter);
		if (components.length > 2) {
			councilAndYear = trimWhitespaceFromString(components[1]);
		}
	}

	private String removeNoteFromString(String input) {
		String inputWithNoteRemoved = input;
		if (note != null) {
			inputWithNoteRemoved = trimWhitespaceFromString(inputWithNoteRemoved);
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
		string = string.replaceAll("\t", "");
		string = string.replaceAll("^\\s*", "");
		return string;
	}

	@Override
	public String toString() {
		String description = key() + " " + title + " " + name + " occupation "
				+ occupation + " " + note + " " + councilAndYear;
		return description;
	}

	public String getFeatureDescription() {
		return featureDescription;
	}

	public void setFeatureDescription(String featureDescription) {
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
