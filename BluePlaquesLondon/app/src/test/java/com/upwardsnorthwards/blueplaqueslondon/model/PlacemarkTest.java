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
    public void testKey_withZeroCoordinates() {
        Placemark placemark = new Placemark();
        placemark.setLatitude(0.0);
        placemark.setLongitude(0.0);

        String key = placemark.key();
        assertNotNull(key);
        assertEquals("0.00.0", key);
    }

    @Test
    public void testKey_withNegativeCoordinates() {
        Placemark placemark = new Placemark();
        placemark.setLatitude(-51.5074);
        placemark.setLongitude(-0.1278);

        String key = placemark.key();
        assertNotNull(key);
        assertEquals("-51.5074-0.1278", key);
    }

    @Test
    public void testKey_withExtremeCoordinates() {
        Placemark placemark = new Placemark();
        placemark.setLatitude(90.0); // North Pole
        placemark.setLongitude(180.0); // International Date Line

        String key = placemark.key();
        assertNotNull(key);
        assertEquals("90.0180.0", key);
    }

    @Test
    public void testKeyFromLatLng() {
        String key = Placemark.keyFromLatLng(51.5074, -0.1278);
        assertNotNull(key);
        assertEquals("51.5074-0.1278", key);
    }

    @Test
    public void testKeyFromLatLng_consistency() {
        double lat = 51.5074;
        double lng = -0.1278;
        
        Placemark placemark = new Placemark();
        placemark.setLatitude(lat);
        placemark.setLongitude(lng);
        
        String instanceKey = placemark.key();
        String staticKey = Placemark.keyFromLatLng(lat, lng);
        
        assertEquals(instanceKey, staticKey);
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
    public void testDigestFeatureDescription_nullDescription() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription(null);

        // Should not throw exception
        placemark.digestFeatureDescription();
        
        // Fields should remain null/empty
        assertEquals("", placemark.getTrimmedTitle());
        assertEquals("", placemark.getTrimmedName());
    }

    @Test
    public void testDigestFeatureDescription_emptyDescription() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("");

        placemark.digestFeatureDescription();
        
        assertEquals("", placemark.getTrimmedTitle());
        assertEquals("", placemark.getTrimmedName());
    }

    @Test
    public void testDigestFeatureDescription_malformedHtml() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("<em>John Smith</em><br>Writer<br>Address<br>Council 2000");

        // Should handle malformed HTML gracefully
        placemark.digestFeatureDescription();
        
        assertNotNull(placemark.getTitle());
        assertNotNull(placemark.getName());
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
    public void testDigestAncillaryInformation_complexStructure() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("Charles Dickens (1812-1870)<br>Novelist<br>48 Doughty Street<br><em>Lived here 1837-1839</em><br>Camden Council 1985");
        placemark.digestFeatureDescription();

        placemark.digestAnciliaryInformation();

        assertNotNull(placemark.getAddress());
        assertNotNull(placemark.getNote());
        assertNotNull(placemark.getCouncilAndYear());
        
        // Verify specific parsing
        assertEquals("Lived here 1837-1839", placemark.getNote().trim());
    }

    @Test
    public void testDigestAncillaryInformation_withoutNote() {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("John Smith<br>Writer<br>123 Main St<br>English Heritage 2000");
        placemark.digestFeatureDescription();

        placemark.digestAnciliaryInformation();

        // Note should be null when no emphasis tags present
        assertEquals(null, placemark.getNote());
        assertNotNull(placemark.getCouncilAndYear());
    }

    @Test
    public void testDigestAncillaryInformation_edgeCases() {
        // Test with minimal components that won't cause parsing issues
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription("Name<br>Occupation<br>Address<br>Council 2000");
        placemark.digestFeatureDescription();

        placemark.digestAnciliaryInformation();

        // Should handle gracefully without crashing
        assertNotNull(placemark);
        assertNotNull(placemark.getAddress());
    }

    @Test
    public void testTrimmedTitle() {
        Placemark placemark = new Placemark();
        placemark.setTitle("  Test Title  ");

        String trimmed = placemark.getTrimmedTitle();
        assertEquals("Test Title", trimmed.trim());
    }

    @Test
    public void testTrimmedTitle_withHtmlEntities() {
        Placemark placemark = new Placemark();
        placemark.setTitle("&lt;Test&gt; &amp; Title");

        String trimmed = placemark.getTrimmedTitle();
        // HTML entities should be decoded
        assertEquals("<Test> & Title", trimmed);
    }

    @Test
    public void testTrimmedTitle_nullTitle() {
        Placemark placemark = new Placemark();
        placemark.setTitle(null);

        String trimmed = placemark.getTrimmedTitle();
        assertEquals("", trimmed);
    }

    @Test
    public void testTrimmedName_nullName() {
        Placemark placemark = new Placemark();
        placemark.setName(null);

        String trimmed = placemark.getTrimmedName();
        assertEquals("", trimmed);
    }

    @Test
    public void testTrimmedOccupation_nullOccupation() {
        Placemark placemark = new Placemark();
        // occupation is not directly settable, but can be null
        
        String trimmed = placemark.getTrimmedOccupation();
        assertEquals("", trimmed);
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

    @Test
    public void testCreatorNewArray_zeroSize() {
        Placemark[] array = Placemark.CREATOR.newArray(0);
        assertNotNull(array);
        assertEquals(0, array.length);
    }

    @Test
    public void testParcelable_withNullFields() {
        // Test parceling with null fields
        Placemark original = new Placemark();
        original.setLatitude(51.5074);
        original.setLongitude(-0.1278);
        // Leave other fields null

        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Placemark fromParcel = Placemark.CREATOR.createFromParcel(parcel);

        assertEquals(original.getLatitude(), fromParcel.getLatitude(), 0.0001);
        assertEquals(original.getLongitude(), fromParcel.getLongitude(), 0.0001);
        assertEquals(original.getName(), fromParcel.getName());
        
        parcel.recycle();
    }

    @Test
    public void testParcelable_withSpecialCharacters() {
        // Test parceling with special characters and Unicode
        Placemark original = new Placemark();
        original.setName("François Müller"); // Unicode characters
        original.setTitle("Test & Title <with> 'quotes'"); // Special HTML characters
        original.setLatitude(51.5074);
        original.setLongitude(-0.1278);

        Parcel parcel = Parcel.obtain();
        original.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Placemark fromParcel = Placemark.CREATOR.createFromParcel(parcel);

        assertEquals(original.getName(), fromParcel.getName());
        assertEquals(original.getTitle(), fromParcel.getTitle());
        
        parcel.recycle();
    }
}
