package com.upwardsnorthwards.blueplaqueslondon.model;

import android.os.Parcel;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for Placemark model.
 */
@RunWith(RobolectricTestRunner.class)
public class PlacemarkTest {

    @Test
    public void testConstructor() {
        Placemark placemark = new Placemark();
        assertNotNull(placemark);
    }

    @Test
    public void testSettersAndGetters() {
        Placemark placemark = new Placemark();

        placemark.setName("Test Name");
        placemark.setTitle("Test Title");
        placemark.setFeatureDescription("Test Description");
        placemark.setStyleUrl("#myStyle");
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);

        assertEquals("Test Name", placemark.getName());
        assertEquals("Test Title", placemark.getTitle());
        assertEquals("#myStyle", placemark.getStyleUrl());
        assertEquals(51.5074, placemark.getLatitude(), 0.0001);
        assertEquals(-0.1278, placemark.getLongitude(), 0.0001);
    }

    @Test
    public void testKey() {
        Placemark placemark = new Placemark();
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);

        String key = placemark.key();
        assertNotNull(key);
        assertEquals("51.5074-0.1278", key);
    }

    @Test
    public void testKeyFromLatLng() {
        String key = Placemark.keyFromLatLng(51.5074, -0.1278);
        assertNotNull(key);
        assertEquals("51.5074-0.1278", key);
    }

    @Test
    public void testDigestFeatureDescription_simple() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("John Smith<br>Writer<br>123 Main St<br>English Heritage 2000");

        placemark.digestFeatureDescription();

        assertEquals("John Smith", placemark.getTitle());
        assertNotNull(placemark.getName());
        assertNotNull(placemark.getOccupation());
    }

    @Test
    public void testDigestFeatureDescription_withYears() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("<em>John Smith</em> (1920-1990)<br>Writer<br>123 Main St");

        placemark.digestFeatureDescription();

        assertNotNull(placemark.getTitle());
        assertNotNull(placemark.getName());
        // Name will be parsed from the title
    }

    @Test
    public void testDigestAncillaryInformation() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("John Smith<br>Writer<br>123 Main St<br>English Heritage 2000");
        placemark.digestFeatureDescription();

        placemark.digestAnciliaryInformation();

        assertNotNull(placemark.getAddress());
        // Address parsing depends on the number of components
    }

    @Test
    public void testDigestAncillaryInformation_withNote() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("John Smith<br>Writer<br><em>This is a note</em><br>English Heritage 2000");
        placemark.digestFeatureDescription();

        placemark.digestAnciliaryInformation();

        assertNotNull(placemark.getNote());
        assertNotNull(placemark.getCouncilAndYear());
    }

    @Test
    public void testTrimmedTitle() {
        Placemark placemark = new Placemark();
        placemark.setTitle("  Test Title  ");

        String trimmed = placemark.getTrimmedTitle();
        assertEquals("Test Title", trimmed.trim());
    }

    @Test
    public void testTrimmedName() {
        Placemark placemark = new Placemark();
        placemark.setName("  Test Name  ");

        String trimmed = placemark.getTrimmedName();
        assertEquals("Test Name", trimmed.trim());
    }

    @Test
    public void testParcelable() {
        // Create placemark with data
        Placemark original = new Placemark();
        original.setName("Test Name");
        original.setTitle("Test Title");
        original.setFeatureDescription("Test Description");
        original.setStyleUrl("#myStyle");
        original.setLatitude(51.5074);
        original.setLongitude(-0.1278);

        // Write to parcel
        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, 0);

        // Reset parcel for reading
        parcel.setDataPosition(0);

        // Create from parcel
        Placemark fromParcel = Placemark.CREATOR.createFromParcel(parcel);

        // Verify
        assertEquals(original.getName(), fromParcel.getName());
        assertEquals(original.getTitle(), fromParcel.getTitle());
        assertEquals(original.getStyleUrl(), fromParcel.getStyleUrl());
        assertEquals(original.getLatitude(), fromParcel.getLatitude(), 0.0001);
        assertEquals(original.getLongitude(), fromParcel.getLongitude(), 0.0001);

        parcel.recycle();
    }

    @Test
    public void testDescribeContents() {
        Placemark placemark = new Placemark();
        assertEquals(0, placemark.describeContents());
    }

    @Test
    public void testToString() {
        Placemark placemark = new Placemark();
        placemark.setName("Test Name");
        placemark.setTitle("Test Title");
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);

        String result = placemark.toString();
        assertNotNull(result);
        // toString includes key, title, name, occupation, note, and councilAndYear
    }

    @Test
    public void testCreatorNewArray() {
        Placemark[] array = Placemark.CREATOR.newArray(5);
        assertNotNull(array);
        assertEquals(5, array.length);
    }
}
