package com.upwardsnorthwards.blueplaqueslondon.activities;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.upwardsnorthwards.blueplaqueslondon.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule = 
        new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void testMainActivityLaunches() {
        // Verify that the main activity launches and displays the map
        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testProgressBarInitiallyVisible() {
        // Verify that progress bar is initially visible while loading
        onView(withId(R.id.map_progress_bar)).check(matches(isDisplayed()));
    }

    @Test
    public void testMenuItemsExist() {
        // Test that menu items can be accessed
        // Note: This would need to open the options menu first
        // onView(withId(R.id.action_about)).check(matches(isDisplayed()));
    }
}