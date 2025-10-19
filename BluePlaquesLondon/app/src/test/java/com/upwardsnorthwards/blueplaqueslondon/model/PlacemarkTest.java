package com.upwardsnorthwards.blueplaqueslondon.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class PlacemarkTest {

    @Test
    public void testPlacemarkCreation() {
        Placemark placemark = new Placemark();
        assertNotNull(placemark);
    }

    @Test
    public void testKeyGeneration() {
        double latitude = 51.5074;
        double longitude = -0.1278;
        String expectedKey = "51.5074-0.1278";
        
        String actualKey = Placemark.keyFromLatLng(latitude, longitude);
        assertEquals(expectedKey, actualKey);
    }

    @Test
    public void testKeyGenerationWithNegativeValues() {
        double latitude = -51.5074;
        double longitude = 0.1278;
        String expectedKey = "-51.50740.1278";
        
        String actualKey = Placemark.keyFromLatLng(latitude, longitude);
        assertEquals(expectedKey, actualKey);
    }

    @Test
    public void testPlacemarkKey() {
        Placemark placemark = new Placemark();
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);
        
        String expectedKey = "51.5074-0.1278";
        String actualKey = placemark.key();
        assertEquals(expectedKey, actualKey);
    }

    @Test
    public void testPlacemarkProperties() {
        Placemark placemark = new Placemark();
        
        String testName = "Test Placemark";
        String testTitle = "Test Title";
        String testStyleUrl = "#testStyle";
        
        placemark.setName(testName);
        placemark.setTitle(testTitle);
        placemark.setStyleUrl(testStyleUrl);
        
        assertEquals(testName, placemark.getName());
        assertEquals(testTitle, placemark.getTitle());
        assertEquals(testStyleUrl, placemark.getStyleUrl());
    }
    
    @Test
    public void testPlacemarkCoordinates() {
        Placemark placemark = new Placemark();
        
        double testLatitude = 51.5074;
        double testLongitude = -0.1278;
        
        placemark.setLatitude(testLatitude);
        placemark.setLongitude(testLongitude);
        
        assertEquals(testLatitude, placemark.getLatitude(), 0.0001);
        assertEquals(testLongitude, placemark.getLongitude(), 0.0001);
    }
}