// Copyright (c) 2014 - 2016 Upwards Northwards Software Limited
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
import android.support.annotation.NonNull;
import android.text.Html;

/**
 * Includes all information needed to display one placemark. Main domain object for the application.
 * Each entry in the kml file is represented by one of these objects.
 */
public class Placemark implements Parcelable {

    public static final Parcelable.Creator<Placemark> CREATOR = new Parcelable.Creator<Placemark>() {

        @NonNull
        public Placemark createFromParcel(@NonNull final Parcel source) {
            return new Placemark(source);
        }

        @NonNull
        public Placemark[] newArray(final int size) {
            return new Placemark[size];
        }

    };
    @NonNull
    private static final String OverlayTitleDelimiter = "<br>";
    @NonNull
    private static final String EmphasisNoteOpeningTag = "<em>";
    @NonNull
    private static final String EmphasisNoteClosingTag = "</em>";
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

    public Placemark() {

    }

    private Placemark(@NonNull final Parcel in) {
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

    @NonNull
    public static String keyFromLatLng(final double latitude, final double longitude) {
        return Double.toString(latitude) + Double.toString(longitude);
    }

    private static String trimWhitespaceFromString(@NonNull String string) {
        return string.replaceAll("\t", "").replaceAll("^\\s*", "");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull final Parcel dest, final int flags) {
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

    @NonNull
    public String key() {
        return Double.toString(latitude) + Double.toString(longitude);
    }

    public void digestFeatureDescription() {
        if (featureDescription != null) {
            // these must be done in order ...
            digestTitle();
            digestName();
            digestOccupation();
        }
    }

    /**
     * Used when displaying the title of a placemark to a user. Fully html decodes the title.
     *
     * @return the trimmed & html decoded title
     */
    @NonNull
    public String getTrimmedTitle() {
        return trimWhitespaceAndHTMLDecode(title);
    }

    /**
     * Used when displaying the name of a placemark to a user. Fully html decodes the name.
     *
     * @return the trimmed and html decoded name
     */
    @NonNull
    public String getTrimmedName() {
        return trimWhitespaceAndHTMLDecode(name);
    }

    /**
     * Used when displaying the occupation of a placemark to a user. Fully html decodes the occupation.
     *
     * @return the trimmed and html decoded occupation
     */
    @NonNull
    public String getTrimmedOccupation() {
        return trimWhitespaceAndHTMLDecode(occupation);
    }

    /**
     * Not all information for the placemark is absolutely necessary when the .kml file is parsed. This method should
     * be invoked whenever the user requests more information about the plaque as it parses out additional information
     * such as the address, notes associated with the plaque and the council and year associated with the plaque.
     */
    public void digestAnciliaryInformation() {
        digestAddress();
        digestNote();
        digestCouncilAndYear();
    }

    private void digestTitle() {
        title = featureDescription;
        final int index = featureDescription.indexOf(OverlayTitleDelimiter);
        if (index != -1) {
            title = featureDescription.substring(0, index);
        }
    }

    private void digestName() {
        name = title;
        String nameDelimiter = "(";
        final int startOfYears = name.indexOf(nameDelimiter);
        if (startOfYears != -1) {
            name = name.replaceAll(EmphasisNoteOpeningTag, "");
            name = name.replaceAll(EmphasisNoteClosingTag, "");
            name = name.substring(0, startOfYears);
        }
        name = name.trim();
    }

    private void digestOccupation() {
        occupation = featureDescription;
        final int index = featureDescription.indexOf(OverlayTitleDelimiter);
        if (index != -1) {
            occupation = occupation.substring(index);
            final int start = occupation.indexOf(OverlayTitleDelimiter);
            if (start == 0) {
                final int delimiterLength = OverlayTitleDelimiter.length();
                final int end = occupation.indexOf(OverlayTitleDelimiter, start
                        + delimiterLength);
                occupation = trimWhitespaceFromString(occupation.substring(
                        start + delimiterLength, end));
                occupation = trimWhitespaceFromString(occupation);
                if (occupation.length() == 9
                        && occupation.matches("[0-9]{4}-[0-9]{4}")) {
                    final String[] components = featureDescription
                            .split(OverlayTitleDelimiter);
                    if (components.length > 3) {
                        occupation = components[2];
                    }
                }
            }
        }
    }

    private void digestAddress() {
        final String[] components = featureDescription.split(OverlayTitleDelimiter);
        if (components.length != 0) {
            switch (components.length) {
                case 2:
                case 3: {
                    address = trimWhitespaceAndHTMLDecode(components[1]);
                }
                break;
                case 4:
                case 5: {
                    address = trimWhitespaceAndHTMLDecode(components[2]);
                }
                break;
                case 6: {
                    address = trimWhitespaceAndHTMLDecode(components[3]);
                }
                break;
                case 7: {
                    address = trimWhitespaceAndHTMLDecode(components[4]);
                }
                break;
            }
        }
    }

    private void digestNote() {
        final int startOfEmphasis = featureDescription
                .indexOf(EmphasisNoteOpeningTag);
        if (startOfEmphasis != -1) {
            int endOfEmphasisIndex = featureDescription
                    .indexOf(EmphasisNoteClosingTag);
            if (endOfEmphasisIndex == -1) {
                // some notes don't have the correct closing tag ... search for
                // the starting tag again
                final int locationOfLastEmphasis = featureDescription
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
            note = trimWhitespaceAndHTMLDecode(note);
        }
    }

    private void digestCouncilAndYear() {
        final String withoutNote = removeNoteFromString(featureDescription);
        final String[] components = withoutNote.split(OverlayTitleDelimiter);
        if (components.length > 2) {
            councilAndYear = trimWhitespaceAndHTMLDecode(components[components.length - 1]);
        }
    }

    private String removeNoteFromString(final String input) {
        String inputWithNoteRemoved = input;
        if (note != null) {
            inputWithNoteRemoved = trimWhitespaceFromString(inputWithNoteRemoved);
            inputWithNoteRemoved = inputWithNoteRemoved.replaceAll(
                    EmphasisNoteOpeningTag, "");
            inputWithNoteRemoved = inputWithNoteRemoved.replaceAll(note, "");
            inputWithNoteRemoved = inputWithNoteRemoved.replaceAll(
                    EmphasisNoteClosingTag, "");
            // check for a trailing delimiter
            final int locationOfFinalDelimiter = inputWithNoteRemoved
                    .lastIndexOf(OverlayTitleDelimiter);
            if (locationOfFinalDelimiter != -1
                    && locationOfFinalDelimiter == inputWithNoteRemoved
                    .length() - OverlayTitleDelimiter.length()) {
                inputWithNoteRemoved = inputWithNoteRemoved.substring(locationOfFinalDelimiter);
            }
        }
        return inputWithNoteRemoved;
    }

    private String trimWhitespaceAndHTMLDecode(@NonNull String string) {
        // TODO: Need to use a faster fromHTML implementation.
        return Html.fromHtml(Placemark.trimWhitespaceFromString(string)).toString();
    }

    @NonNull
    @Override
    public String toString() {
        return key() + " " + title + " " + name + occupation + " " + note + " " + councilAndYear;
    }

    public void setFeatureDescription(final String featureDescription) {
        this.featureDescription = featureDescription;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getAddress() {
        return address;
    }

    public String getNote() {
        return note;
    }

    public String getCouncilAndYear() {
        return councilAndYear;
    }

    public String getStyleUrl() {
        return styleUrl;
    }

    public void setStyleUrl(final String styleUrl) {
        this.styleUrl = styleUrl;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(final double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(final double longitude) {
        this.longitude = longitude;
    }

}
