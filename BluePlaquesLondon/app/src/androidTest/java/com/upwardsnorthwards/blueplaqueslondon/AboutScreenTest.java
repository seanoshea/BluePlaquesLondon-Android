package com.upwardsnorthwards.blueplaqueslondon;

import androidx.compose.ui.test.junit4.createAndroidComposeRule;
import androidx.compose.ui.test.onNodeWithText;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class AboutScreenTest {

    @Rule
    public final createAndroidComposeRule<MainActivity> composeTestRule = new createAndroidComposeRule<>(MainActivity.class);

    @Test
    public void aboutScreen_displaysCorrectly() {
        ActivityScenario<MainActivity> scenario = composeTestRule.getScenario();
        scenario.onActivity(activity -> {
            NavController navController = Navigation.findNavController(activity, R.id.nav_host_fragment);
            navController.navigate(R.id.about_fragment);
        });

        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.developed_details)).assertExists();
        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.developed_by)).assertExists();
        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.designer_details)).assertExists();
        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.designed_by)).assertExists();
        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.map_details)).assertExists();
        composeTestRule.onNodeWithText(composeTestRule.getActivity().getString(R.string.map_data)).assertExists();
    }
}
