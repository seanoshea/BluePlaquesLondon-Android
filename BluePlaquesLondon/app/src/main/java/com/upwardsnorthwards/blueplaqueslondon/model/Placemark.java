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

package com.upwardsnorthwards.blueplaqueslondon.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;

public class Placemark implements Parcelable {

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

    public Placemark() {

    }

    private Placemark(Parcel in) {
        super();
        featureDescription = in.readString();
        title = in.readString();
        name = in.readString();
        occupation = in.readString();
        address = in.readString();
        note = in.readString();
        councilAndYear = in.readString();
        styleUrl = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(featureDescription);
        dest.writeString(title);
        dest.writeString(name);
        dest.writeString(occupation);
        dest.writeString(address);
        dest.writeString(note);
        dest.writeString(councilAndYear);
        dest.writeString(styleUrl);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    public static final Parcelable.Creator<Placemark> CREATOR = new Parcelable.Creator<Placemark>() {

        public Placemark createFromParcel(Parcel source) {
            return new Placemark(source);
        }

        public Placemark[] newArray(int size) {
            return new Placemark[size];
        }

    };

    public static String keyFromLatLng(double latitude, double longitude) {
        return Double.toString(latitude) + Double.toString(longitude);
    }

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
        name = trimWhitespaceFromString(name).trim();
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
                occupation = trimWhitespaceFromString(occupation.substring(
                        start + delimiterLength, end));
                occupation = trimWhitespaceFromString(occupation);
                if (occupation.length() == 9
                        && occupation.matches("[0-9]{4}-[0-9]{4}")) {
                    String[] components = featureDescription
                            .split(OverlayTitleDelimiter);
                    if (components.length > 3) {
                        occupation = components[2];
                    }
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
            councilAndYear = trimWhitespaceFromString(components[components.length - 1]);
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
        string = string.replaceAll("\t", "").replaceAll("^\\s*", "");
        string = Html.fromHtml(string).toString();
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
