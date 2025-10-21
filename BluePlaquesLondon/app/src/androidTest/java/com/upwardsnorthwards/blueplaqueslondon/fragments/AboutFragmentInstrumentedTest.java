package com.upwardsnorthwards.blueplaqueslondon.fragments;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Instrumented tests for AboutFragment.
 * These tests run on an Android device or emulator.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class AboutFragmentInstrumentedTest {

    private AboutFragment fragment;

    @Before
    public void setUp() {
        fragment = new AboutFragment();
    }

    @Test
    public void testFragmentCreation() {
        // Then
        assertNotNull("Fragment should be created", fragment);
    }

    @Test
    public void testFragmentInitialization() {
        // Given
        fragment = new AboutFragment();

        // Then
        assertNotNull("Fragment should initialize successfully", fragment);
    }
}
