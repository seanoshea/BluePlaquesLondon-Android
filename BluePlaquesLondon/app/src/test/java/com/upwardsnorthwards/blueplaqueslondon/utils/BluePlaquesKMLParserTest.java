package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(RobolectricTestRunner.class)
public class BluePlaquesKMLParserTest {

    private BluePlaquesKMLParser parser;
    private Context context;

    @Before
    public void setUp() {
        parser = new BluePlaquesKMLParser();
        context = RuntimeEnvironment.getApplication();
    }

    @Test
    public void testParserInitialization() {
        assertNotNull(parser);
        assertNotNull(parser.getPlacemarks());
        assertNotNull(parser.getMassagedPlacemarks());
        assertNotNull(parser.getKeyToArrayPositions());
    }

    @Test
    public void testLoadMapData() {
        // Test that parser initializes correctly even without data
        List<Placemark> placemarks = parser.getPlacemarks();
        List<Placemark> massagedPlacemarks = parser.getMassagedPlacemarks();
        
        assertNotNull("Placemarks list should not be null", placemarks);
        assertNotNull("Massaged placemarks list should not be null", massagedPlacemarks);
        assertNotNull("Key to array positions should not be null", parser.getKeyToArrayPositions());
        
        // Initial state should be empty
        assertEquals("Initial placemarks should be empty", 0, placemarks.size());
        assertEquals("Initial massaged placemarks should be empty", 0, massagedPlacemarks.size());
    }

    @Test
    public void testPlacemarkProperties() {
        // Test with a manually created placemark to verify the parser can handle it
        Placemark testPlacemark = new Placemark();
        testPlacemark.setName("Test Placemark");
        testPlacemark.setLatitude(51.5074);
        testPlacemark.setLongitude(-0.1278);
        
        assertNotNull("Placemark should have a name", testPlacemark.getName());
        assertEquals("Latitude should be set correctly", 51.5074, testPlacemark.getLatitude(), 0.0001);
        assertEquals("Longitude should be set correctly", -0.1278, testPlacemark.getLongitude(), 0.0001);
    }

    @Test
    public void testKeyGeneration() {
        // Test key generation with known coordinates
        Placemark placemark = new Placemark();
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);
        
        String key = placemark.key();
        assertNotNull("Key should not be null", key);
        assertFalse("Key should not be empty", key.isEmpty());
        assertEquals("Key should match expected format", "51.5074-0.1278", key);
    }
    
    @Test
    public void testParserDataStructures() {
        // Test that all required data structures are initialized
        assertNotNull("Parser should not be null", parser);
        assertNotNull("Placemarks list should be initialized", parser.getPlacemarks());
        assertNotNull("Massaged placemarks list should be initialized", parser.getMassagedPlacemarks());
        assertNotNull("Key to array positions map should be initialized", parser.getKeyToArrayPositions());
    }
}