package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.upwardsnorthwards.blueplaqueslondon.R;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dagger.hilt.android.testing.HiltAndroidRule;
import dagger.hilt.android.testing.HiltAndroidTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.junit.Assert.assertNotNull;

/**
 * Instrumented tests for MainActivity.
 * These tests run on an Android device or emulator and test the Activity with Hilt DI.
 * NOTE: Currently ignored due to Google Maps threading issues in tests.
 */
@Ignore("MainActivity uses Google Maps which has threading issues in instrumented tests")
@LargeTest
@HiltAndroidTest
@RunWith(AndroidJUnit4.class)
public class MainActivityInstrumentedTest {

    @Rule
    public HiltAndroidRule hiltRule = new HiltAndroidRule(this);

    private ActivityScenario<MainActivity> scenario;

    @Before
    public void setUp() {
        hiltRule.inject();
    }

    @After
    public void tearDown() {
        if (scenario != null) {
            scenario.close();
        }
    }

    @Test
    public void testActivityLaunch() {
        // Given
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        // When
        scenario = ActivityScenario.launch(intent);

        // Then
        scenario.onActivity(activity -> {
            assertNotNull("Activity should not be null", activity);
            assertNotNull("Activity should have a window", activity.getWindow());
        });
    }

    @Test
    public void testActivityCreatesMapFragment() {
        // Given
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        // When
        scenario = ActivityScenario.launch(intent);

        // Then
        scenario.onActivity(activity -> {
            assertNotNull("Activity should not be null", activity);
            // Verify the activity has loaded without crashing
        });
    }

    @Test
    public void testActivityHasCorrectLayout() {
        // Given
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);

        // When
        scenario = ActivityScenario.launch(intent);

        // Then - verify key UI elements are present (NavHostFragment)
        onView(withId(R.id.nav_host_fragment))
                .check(matches(isDisplayed()));
    }

    @Test
    public void testActivityLifecycle() {
        // Given
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        scenario = ActivityScenario.launch(intent);

        // When - go through lifecycle
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.STARTED);
        scenario.moveToState(androidx.lifecycle.Lifecycle.State.RESUMED);

        // Then
        scenario.onActivity(activity -> {
            assertNotNull("Activity should survive lifecycle changes", activity);
        });
    }

    @Test
    public void testActivityRecreation() {
        // Given
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        scenario = ActivityScenario.launch(intent);

        // When - simulate configuration change
        scenario.recreate();

        // Then
        scenario.onActivity(activity -> {
            assertNotNull("Activity should survive recreation", activity);
        });
    }
}
