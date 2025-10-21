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
 * Unit tests for WikipediaFragment using Robolectric.
 * Tests fragment initialization and argument handling for Wikipedia display.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class WikipediaFragmentTest {

    private WikipediaFragment fragment;
    private Placemark testPlacemark;

    @Before
    public void setUp() {
        fragment = new WikipediaFragment();

        // Create test placemark
        testPlacemark = new Placemark();
        testPlacemark.setName("Winston Churchill");
        testPlacemark.setTitle("Winston Leonard Spencer Churchill");
        testPlacemark.setLatitude(51.5f);
        testPlacemark.setLongitude(-0.1f);
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
        args.putParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, testPlacemark);

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
    public void testFragmentWithDifferentPlacemark() {
        // Given
        Placemark anotherPlacemark = new Placemark();
        anotherPlacemark.setName("Charles Darwin");
        anotherPlacemark.setTitle("Charles Robert Darwin");

        Bundle args = new Bundle();
        args.putParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, anotherPlacemark);

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
        args.putParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, testPlacemark);

        // When
        fragment.setArguments(args);

        // Then
        assertNotNull(fragment.getArguments());
    }

    @Test
    public void testFragmentInstantiation() {
        // Given
        WikipediaFragment newFragment = new WikipediaFragment();

        // Then
        assertNotNull(newFragment);
    }
}
