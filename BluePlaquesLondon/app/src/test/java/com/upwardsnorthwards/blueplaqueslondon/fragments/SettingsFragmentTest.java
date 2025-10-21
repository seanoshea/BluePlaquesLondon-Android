package com.upwardsnorthwards.blueplaqueslondon.fragments;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for SettingsFragment.
 * Note: SettingsFragment uses android.app.Fragment (deprecated).
 * Full integration testing requires an Activity context.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SettingsFragmentTest {

    private SettingsFragment fragment;

    @Before
    public void setUp() {
        fragment = new SettingsFragment();
    }

    @Test
    public void testFragmentCreation() {
        assertNotNull(fragment);
    }

    @Test
    public void testFragmentIsNotNull() {
        assertNotNull("Fragment should be instantiated", fragment);
    }
}
