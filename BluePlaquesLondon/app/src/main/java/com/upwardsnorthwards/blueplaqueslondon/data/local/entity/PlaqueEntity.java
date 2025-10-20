package com.upwardsnorthwards.blueplaqueslondon.data.local.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Room entity representing a Blue Plaque in the database.
 * Maps to the Placemark domain model.
 */
@Entity(tableName = "plaques")
public class PlaqueEntity {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String id; // Composite key from latitude + longitude

    @ColumnInfo(name = "feature_description")
    private String featureDescription;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "name")
    private String name;

    @ColumnInfo(name = "occupation")
    private String occupation;

    @ColumnInfo(name = "address")
    private String address;

    @ColumnInfo(name = "note")
    private String note;

    @ColumnInfo(name = "council_and_year")
    private String councilAndYear;

    @ColumnInfo(name = "style_url")
    private String styleUrl;

    @ColumnInfo(name = "latitude")
    private double latitude;

    @ColumnInfo(name = "longitude")
    private double longitude;

    public PlaqueEntity() {
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getFeatureDescription() {
        return featureDescription;
    }

    public void setFeatureDescription(String featureDescription) {
        this.featureDescription = featureDescription;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
