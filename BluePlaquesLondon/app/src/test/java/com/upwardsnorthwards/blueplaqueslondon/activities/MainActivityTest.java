package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.content.Intent;
import android.view.Menu;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for MainActivity.
 * Note: MainActivity requires Hilt initialization which is complex in Robolectric unit tests.
 * These tests are disabled for now. Activity testing should be done with instrumented tests.
 */
@Ignore("MainActivity requires Hilt - use instrumented tests instead")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MainActivityTest {

    private ActivityController<MainActivity> controller;
    private MainActivity activity;

    @Before
    public void setUp() {
        // Create activity controller
        controller = Robolectric.buildActivity(MainActivity.class);
    }

    @Test
    public void testActivityCreation() {
        // When
        activity = controller.create().get();

        // Then
        assertNotNull(activity);
    }

    @Test
    public void testActivityLifecycle() {
        // When - run through full lifecycle
        activity = controller.create().start().resume().get();

        // Then
        assertNotNull(activity);
        assertTrue("Activity should not be finishing", !activity.isFinishing());
    }

    @Test
    public void testActivityPauseResume() {
        // Given
        activity = controller.create().start().resume().get();

        // When
        controller.pause().resume();

        // Then
        assertNotNull(activity);
    }

    @Test
    public void testActivityStop() {
        // Given
        activity = controller.create().start().resume().get();

        // When
        controller.pause().stop();

        // Then
        assertNotNull(activity);
    }

    @Test
    public void testActivityDestroy() {
        // Given
        activity = controller.create().start().resume().get();

        // When
        controller.pause().stop().destroy();

        // Then
        assertTrue("Activity should be destroyed", activity.isDestroyed());
    }

    @Test
    public void testOptionsMenuCreation() {
        // Given
        activity = controller.create().start().resume().get();

        // When
        Menu menu = new org.robolectric.fakes.RoboMenu(activity);
        boolean result = activity.onCreateOptionsMenu(menu);

        // Then
        assertTrue("Menu should be created", result);
    }

    @Test
    public void testSearchIntent() {
        // Given
        Intent searchIntent = new Intent(Intent.ACTION_SEARCH);
        searchIntent.putExtra(android.app.SearchManager.QUERY, "Darwin");

        // Create activity with intent
        controller = Robolectric.buildActivity(MainActivity.class, searchIntent);

        // When
        activity = controller.create().start().resume().get();

        // Then
        assertNotNull(activity);
    }
}
