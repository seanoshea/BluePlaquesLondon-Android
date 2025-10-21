package com.upwardsnorthwards.blueplaqueslondon.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for BluePlaquesConstants.
 */
public class BluePlaquesConstantsTest {

    @Test
    public void testDefaultCoordinates() {
        // Verify default coordinates are in London
        assertEquals(51.50016999993306, BluePlaquesConstants.DEFAULT_LATITUDE, 0.0001);
        assertEquals(-0.1814680000049975, BluePlaquesConstants.DEFAULT_LONGITUDE, 0.0001);
    }

    @Test
    public void testParcelableKeys() {
        assertNotNull(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY);
        assertNotNull(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY);
        assertNotNull(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY);
    }

    @Test
    public void testEventConstants() {
        assertEquals("ApplicationLoaded", BluePlaquesConstants.APPLICATION_LOADED);
        assertEquals("BPLUIActionCategory", BluePlaquesConstants.UI_ACTION_CATEGORY);
        assertEquals("BPLErrorCategory", BluePlaquesConstants.ERROR_CATEGORY);
    }

    @Test
    public void testUIEventConstants() {
        assertEquals("BPLDetailsButtonPressedEvent", BluePlaquesConstants.DETAILS_BUTTON_PRESSED_EVENT);
        assertEquals("BPLWikipediaButtonPressedEvent", BluePlaquesConstants.WIKIPEDIA_BUTTON_PRESSED_EVENT);
        assertEquals("BPLStreetViewButtonPressedEvent", BluePlaquesConstants.STREETVIEW_BUTTON_PRESSED_EVENT);
        assertEquals("BPLTableRowPressedEvent", BluePlaquesConstants.TABLE_ROW_PRESSED_EVENT);
        assertEquals("BPLMarkerPressedEvent", BluePlaquesConstants.MARKER_PRESSED_EVENT);
        assertEquals("BPLMarkerInfoWindowPressedEvent", BluePlaquesConstants.MARKER_INFO_WINDOW_PRESSED_EVENT);
    }

    @Test
    public void testRateAppEventConstants() {
        assertEquals("BPLRateAppButtonPressedEvent", BluePlaquesConstants.RATE_APP_BUTTON_PRESSED_EVENT);
        assertEquals("BPLDeclineRateAppButtonPressedEvent", BluePlaquesConstants.DECLINE_RATE_APP_BUTTON_PRESSED_EVENT);
        assertEquals("BPLRemindRateAppButtonPressedEvent", BluePlaquesConstants.REMIND_RATE_APP_BUTTON_PRESSED_EVENT);
        assertEquals("BPLRateAppStoreOpenedEvent", BluePlaquesConstants.RATE_APP_STORE_OPENED_EVENT);
    }

    @Test
    public void testGooglePlayServicesEventConstants() {
        assertEquals("BPLGooglePlayServicesPromptEvent", BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT);
        assertEquals("BPLGooglePlayServicesPromptRecoverable", BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT_RECOVERABLE);
        assertEquals("BPLGooglePlayServicesPromptUnrecoverable", BluePlaquesConstants.GOOGLE_PLAY_SERVICES_PROMPT_UNRECOVERABLE);
    }
}
