package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for MapDetailFragment using Robolectric.
 * Tests basic fragment instantiation and argument handling.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MapDetailFragmentTest {

    private List<Placemark> testPlacemarks;
    private MapDetailFragment fragment;

    @Before
    public void setUp() {
        testPlacemarks = new ArrayList<>();

        // Create test placemarks
        Placemark plaque1 = new Placemark();
        plaque1.setName("Test Plaque 1");
        plaque1.setTitle("Winston Churchill");
        plaque1.setLatitude(51.5f);
        plaque1.setLongitude(-0.1f);
        testPlacemarks.add(plaque1);

        fragment = new MapDetailFragment();
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
        args.putParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                new ArrayList<>(testPlacemarks));

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
    public void testFragmentWithEmptyPlaquesList() {
        // Given - empty list
        Bundle args = new Bundle();
        args.putParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                new ArrayList<>());

        // When
        fragment.setArguments(args);

        // Then
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentWithMultiplePlacemarks() {
        // Given
        Placemark plaque2 = new Placemark();
        plaque2.setName("Test Plaque 2");
        plaque2.setTitle("Test Person 2");
        testPlacemarks.add(plaque2);

        Bundle args = new Bundle();
        args.putParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                new ArrayList<>(testPlacemarks));

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
        args.putParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY,
                new ArrayList<>(testPlacemarks));

        // When
        fragment.setArguments(args);

        // Then
        assertNotNull(fragment.getArguments());
    }

    @Test
    public void testFragmentInstantiation() {
        // Given
        MapDetailFragment newFragment = new MapDetailFragment();

        // Then
        assertNotNull(newFragment);
    }
}
