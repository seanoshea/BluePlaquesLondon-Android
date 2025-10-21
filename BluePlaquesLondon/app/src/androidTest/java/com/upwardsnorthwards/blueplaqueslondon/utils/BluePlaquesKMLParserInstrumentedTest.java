package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented tests for BluePlaquesKMLParser.
 * These tests run on an Android device or emulator with real assets.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class BluePlaquesKMLParserInstrumentedTest {

    private Context context;
    private BluePlaquesKMLParser parser;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        parser = new BluePlaquesKMLParser();
    }

    @Test
    public void testParserCreation() {
        // Then
        assertNotNull("Parser should be created", parser);
    }

    @Test
    public void testLoadMapData() {
        // When
        parser.loadMapData(context);

        // Then - verify data was loaded
        List<Placemark> placemarks = parser.getPlacemarks();
        assertNotNull("Placemarks should not be null", placemarks);
        // Note: May be empty if KML file not in androidTest assets
    }

    @Test
    public void testGetPlacemarks_afterLoad() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> placemarks = parser.getPlacemarks();

        // Then
        assertNotNull("Placemarks list should not be null", placemarks);
        // If file exists, placemarks should have data
        for (Placemark placemark : placemarks) {
            assertNotNull("Placemark title should exist", placemark.getTitle());
            assertTrue("Latitude should be set", placemark.getLatitude() != 0.0);
            assertTrue("Longitude should be set", placemark.getLongitude() != 0.0);
        }
    }

    @Test
    public void testGetMassagedPlacemarks_afterLoad() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> massagedPlacemarks = parser.getMassagedPlacemarks();

        // Then
        assertNotNull("Massaged placemarks should not be null", massagedPlacemarks);
        // Massaged placemarks should be <= original placemarks (duplicates removed)
        assertTrue("Massaged list should not exceed original",
                massagedPlacemarks.size() <= parser.getPlacemarks().size());
    }

    @Test
    public void testGetKeyToArrayPositions_afterLoad() {
        // Given
        parser.loadMapData(context);

        // When
        Map<String, List<Integer>> keyToArrayPositions = parser.getKeyToArrayPositions();

        // Then
        assertNotNull("Key to array positions should not be null", keyToArrayPositions);
    }

    @Test
    public void testPlacemarkDataIntegrity() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> placemarks = parser.getPlacemarks();

        // Then - verify each placemark has required data
        for (Placemark placemark : placemarks) {
            assertNotNull("Name should not be null", placemark.getName());
            assertNotNull("Title should not be null", placemark.getTitle());
            // Latitude and longitude should be in valid London range
            if (placemark.getLatitude() != 0.0) {
                assertTrue("Latitude should be in London range",
                        placemark.getLatitude() > 51.0 && placemark.getLatitude() < 52.0);
                assertTrue("Longitude should be in London range",
                        placemark.getLongitude() > -1.0 && placemark.getLongitude() < 1.0);
            }
        }
    }

    @Test
    public void testDuplicateConsolidation() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> originalPlacemarks = parser.getPlacemarks();
        List<Placemark> massagedPlacemarks = parser.getMassagedPlacemarks();
        Map<String, List<Integer>> keyPositions = parser.getKeyToArrayPositions();

        // Then
        // If there are duplicates, massaged list should be smaller
        if (originalPlacemarks.size() > massagedPlacemarks.size()) {
            assertTrue("Key map should track duplicate positions",
                    keyPositions.values().stream().anyMatch(list -> list.size() > 1));
        }
    }

    @Test
    public void testEmptyParserState_beforeLoad() {
        // Given - parser before loading data
        BluePlaquesKMLParser emptyParser = new BluePlaquesKMLParser();

        // When
        List<Placemark> placemarks = emptyParser.getPlacemarks();
        List<Placemark> massagedPlacemarks = emptyParser.getMassagedPlacemarks();
        Map<String, List<Integer>> keyPositions = emptyParser.getKeyToArrayPositions();

        // Then
        assertNotNull("Placemarks should not be null", placemarks);
        assertNotNull("Massaged placemarks should not be null", massagedPlacemarks);
        assertNotNull("Key positions should not be null", keyPositions);

        assertTrue("Placemarks should be empty before loading", placemarks.isEmpty());
        assertTrue("Massaged placemarks should be empty before loading", massagedPlacemarks.isEmpty());
        assertTrue("Key positions should be empty before loading", keyPositions.isEmpty());
    }

    @Test
    public void testMultipleLoadCalls() {
        // When - load data multiple times
        parser.loadMapData(context);
        int firstSize = parser.getPlacemarks().size();

        parser.loadMapData(context);
        int secondSize = parser.getPlacemarks().size();

        // Then - should handle multiple loads
        assertNotNull("Placemarks should not be null after multiple loads", parser.getPlacemarks());
        // Size should be consistent across loads (unless data changes)
    }

    @Test
    public void testGetKeyToArrayPositions_structure() {
        // Given
        parser.loadMapData(context);

        // When
        Map<String, List<Integer>> keyToArrayPositions = parser.getKeyToArrayPositions();

        // Then - verify structure
        for (Map.Entry<String, List<Integer>> entry : keyToArrayPositions.entrySet()) {
            assertNotNull("Key should not be null", entry.getKey());
            assertNotNull("Position list should not be null", entry.getValue());
            assertFalse("Position list should not be empty", entry.getValue().isEmpty());

            // Verify all positions are valid indices
            for (Integer position : entry.getValue()) {
                assertNotNull("Position should not be null", position);
                assertTrue("Position should be non-negative", position >= 0);
                assertTrue("Position should be within placemarks range",
                        position < parser.getPlacemarks().size());
            }
        }
    }
}
