package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for PanoramaFragment using Robolectric.
 * Tests fragment initialization and argument handling for Street View display.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class PanoramaFragmentTest {

    private PanoramaFragment fragment;
    private Placemark testPlacemark;

    @Before
    public void setUp() {
        fragment = new PanoramaFragment();

        // Create test placemark with coordinates
        testPlacemark = new Placemark();
        testPlacemark.setName("Westminster Abbey");
        testPlacemark.setTitle("Westminster Abbey");
        testPlacemark.setLatitude(51.4955f);
        testPlacemark.setLongitude(-0.1270f);
    }

    @Test
    public void testFragmentCreation() {
        // Then
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentWithPlacemarkArguments() {
        // Given
        Bundle args = new Bundle();
        args.putParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY, testPlacemark);

        // When
        fragment.setArguments(args);

        // Then
        assertNotNull(fragment);
        assertNotNull(fragment.getArguments());
    }

    @Test
    public void testFragmentWithNullArguments() {
        // When
        fragment.setArguments(null);

        // Then
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentWithValidCoordinates() {
        // Given
        Placemark placemarkWithCoords = new Placemark();
        placemarkWithCoords.setName("Test Location");
        placemarkWithCoords.setLatitude(51.5f);
        placemarkWithCoords.setLongitude(-0.1f);

        Bundle args = new Bundle();
        args.putParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY, placemarkWithCoords);

        // When
        fragment.setArguments(args);

        // Then
        assertNotNull(fragment);
        assertNotNull(fragment.getArguments());
    }

    @Test
    public void testFragmentArgumentsPreservation() {
        // Given
        Bundle args = new Bundle();
        args.putParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY, testPlacemark);

        // When
        fragment.setArguments(args);

        // Then
        assertNotNull(fragment.getArguments());
    }

    @Test
    public void testFragmentInstantiation() {
        // Given
        PanoramaFragment newFragment = new PanoramaFragment();

        // Then
        assertNotNull(newFragment);
    }
}
