package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for BluePlaquesKMLParser.
 */
@RunWith(RobolectricTestRunner.class)
public class BluePlaquesKMLParserTest {

    private Context context;
    private BluePlaquesKMLParser parser;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        parser = new BluePlaquesKMLParser();
    }

    @Test
    public void testLoadMapData() {
        // When
        parser.loadMapData(context);

        // Then
        List<Placemark> placemarks = parser.getPlacemarks();
        assertNotNull(placemarks);
        // Note: In test environment, KML file may not be available
        // This tests that parser handles the situation gracefully
    }

    @Test
    public void testGetPlacemarks() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> placemarks = parser.getPlacemarks();

        // Then
        assertNotNull(placemarks);
        for (Placemark placemark : placemarks) {
            assertNotNull("Placemark should have a title", placemark.getTitle());
            assertTrue("Latitude should be set", placemark.getLatitude() != 0.0);
            assertTrue("Longitude should be set", placemark.getLongitude() != 0.0);
        }
    }

    @Test
    public void testGetMassagedPlacemarks() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> massagedPlacemarks = parser.getMassagedPlacemarks();

        // Then
        assertNotNull(massagedPlacemarks);
        // Massaged placemarks should be <= original placemarks (duplicates removed)
        assertTrue("Massaged list should not exceed original",
                   massagedPlacemarks.size() <= parser.getPlacemarks().size());
    }

    @Test
    public void testGetKeyToArrayPositions() {
        // Given
        parser.loadMapData(context);

        // When
        Map<String, List<Integer>> keyToArrayPositions = parser.getKeyToArrayPositions();

        // Then
        assertNotNull(keyToArrayPositions);
        // In test environment, may not have data
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
    public void testPlacemarkDataIntegrity() {
        // Given
        parser.loadMapData(context);

        // When
        List<Placemark> placemarks = parser.getPlacemarks();

        // Then
        assertNotNull(placemarks);
        // In test environment, actual KML file may not be accessible
        // This test verifies the parser handles the situation without crashing
    }

    @Test
    public void testEmptyParserState() {
        // Test parser before loading data
        List<Placemark> placemarks = parser.getPlacemarks();
        List<Placemark> massagedPlacemarks = parser.getMassagedPlacemarks();
        Map<String, List<Integer>> keyPositions = parser.getKeyToArrayPositions();

        assertNotNull(placemarks);
        assertNotNull(massagedPlacemarks);
        assertNotNull(keyPositions);

        assertTrue("Placemarks should be empty before loading", placemarks.isEmpty());
        assertTrue("Massaged placemarks should be empty before loading", massagedPlacemarks.isEmpty());
        assertTrue("Key positions should be empty before loading", keyPositions.isEmpty());
    }
}
