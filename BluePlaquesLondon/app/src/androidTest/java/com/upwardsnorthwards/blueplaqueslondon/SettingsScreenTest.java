package com.upwardsnorthwards.blueplaqueslondon;

import androidx.compose.ui.test.junit4.createAndroidComposeRule;
import androidx.compose.ui.test.onNodeWithText;
import androidx.compose.ui.test.performClick;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesSharedPreferences;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;

@RunWith(AndroidJUnit4.class)
public class SettingsScreenTest {

    @Rule
    public final createAndroidComposeRule<MainActivity> composeTestRule = new createAndroidComposeRule<>(MainActivity.class);

    @Test
    public void settingsScreen_toggleAnalytics() {
        ActivityScenario<MainActivity> scenario = composeTestRule.getScenario();
        scenario.onActivity(activity -> {
            BluePlaquesSharedPreferences.saveAnalyticsEnabled(activity, true);
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            navController.navigate(R.id.settings_fragment);
        });

        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.analytics_title)).assertExists();
        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.analytics_title)).performClick();

        scenario.onActivity(activity -> {
            assertFalse(BluePlaquesSharedPreferences.getAnalyticsEnabled(activity));
        });
    }
}
