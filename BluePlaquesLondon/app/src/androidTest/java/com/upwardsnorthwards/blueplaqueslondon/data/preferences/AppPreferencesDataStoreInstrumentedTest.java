package com.upwardsnorthwards.blueplaqueslondon.data.preferences;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.google.android.gms.maps.model.LatLng;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented tests for AppPreferencesDataStore.
 * These tests run on an Android device or emulator with a real DataStore.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class AppPreferencesDataStoreInstrumentedTest {

    private AppPreferencesDataStore dataStore;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Clear any existing datastore files to ensure clean state
        File dataStoreDir = new File(context.getFilesDir(), "datastore");
        if (dataStoreDir.exists()) {
            File[] files = dataStoreDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getName().contains("blue_plaques_preferences")) {
                        file.delete();
                    }
                }
            }
        }

        dataStore = new AppPreferencesDataStore(context);
    }

    @Test
    public void testDataStoreCreation() {
        // Then
        assertNotNull("DataStore should be created", dataStore);
    }

    @Test
    public void testGetLastKnownBPLCoordinate_defaultValue() {
        // When
        LatLng coordinate = dataStore.getLastKnownBPLCoordinateSingle().blockingGet();

        // Then - should return default values
        assertNotNull("Coordinate should not be null", coordinate);
        assertEquals("Default latitude should match",
                BluePlaquesConstants.DEFAULT_LATITUDE, coordinate.latitude, 0.0001);
        assertEquals("Default longitude should match",
                BluePlaquesConstants.DEFAULT_LONGITUDE, coordinate.longitude, 0.0001);
    }

    @Test
    public void testSaveAndGetLastKnownBPLCoordinate() {
        // Given
        LatLng testCoordinate = new LatLng(51.5074, -0.1278);

        // When
        dataStore.saveLastKnownBPLCoordinate(testCoordinate).blockingGet();
        LatLng retrieved = dataStore.getLastKnownBPLCoordinateSingle().blockingGet();

        // Then
        assertNotNull("Retrieved coordinate should not be null", retrieved);
        assertEquals("Latitude should match", testCoordinate.latitude, retrieved.latitude, 0.0001);
        assertEquals("Longitude should match", testCoordinate.longitude, retrieved.longitude, 0.0001);
    }

    @Test
    public void testSaveAndGetLastKnownCoordinate() {
        // Given
        LatLng testCoordinate = new LatLng(52.0, -1.0);

        // When
        dataStore.saveLastKnownCoordinate(testCoordinate).blockingGet();

        // Then - coordinate is saved (can't retrieve user location separately, but save should succeed)
        assertNotNull("Save operation should complete", testCoordinate);
    }

    @Test
    public void testGetMapZoom_defaultValue() {
        // When
        float zoom = dataStore.getMapZoomSingle().blockingGet();

        // Then
        assertEquals("Default zoom should be 15.0", 15.0f, zoom, 0.01f);
    }

    @Test
    public void testSaveAndGetMapZoom() {
        // Given
        float testZoom = 12.5f;
        float minZoom = 10.0f;
        float maxZoom = 20.0f;

        // When
        dataStore.saveMapZoom(testZoom, minZoom, maxZoom).blockingGet();
        float retrieved = dataStore.getMapZoomSingle().blockingGet();

        // Then
        assertEquals("Zoom should match", testZoom, retrieved, 0.01f);
    }

    @Test
    public void testSaveMapZoom_withInvalidValue_tooLow() {
        // Given
        float invalidZoom = 5.0f;
        float minZoom = 10.0f;
        float maxZoom = 20.0f;

        // When
        dataStore.saveMapZoom(invalidZoom, minZoom, maxZoom).blockingGet();
        float retrieved = dataStore.getMapZoomSingle().blockingGet();

        // Then - should not save invalid value, should return default
        assertEquals("Should return default zoom", 15.0f, retrieved, 0.01f);
    }

    @Test
    public void testSaveMapZoom_withInvalidValue_tooHigh() {
        // Given
        float invalidZoom = 25.0f;
        float minZoom = 10.0f;
        float maxZoom = 20.0f;

        // When
        dataStore.saveMapZoom(invalidZoom, minZoom, maxZoom).blockingGet();
        float retrieved = dataStore.getMapZoomSingle().blockingGet();

        // Then - should not save invalid value, should return default
        assertEquals("Should return default zoom", 15.0f, retrieved, 0.01f);
    }

    @Test
    public void testGetAnalyticsEnabled_defaultValue() {
        // When
        boolean enabled = dataStore.getAnalyticsEnabledSingle().blockingGet();

        // Then
        assertTrue("Analytics should be enabled by default", enabled);
    }

    @Test
    public void testSaveAndGetAnalyticsEnabled_true() {
        // Given
        boolean testValue = true;

        // When
        dataStore.saveAnalyticsEnabled(testValue).blockingGet();
        boolean retrieved = dataStore.getAnalyticsEnabledSingle().blockingGet();

        // Then
        assertEquals("Analytics enabled should match", testValue, retrieved);
    }

    @Test
    public void testSaveAndGetAnalyticsEnabled_false() {
        // Given
        boolean testValue = false;

        // When
        dataStore.saveAnalyticsEnabled(testValue).blockingGet();
        boolean retrieved = dataStore.getAnalyticsEnabledSingle().blockingGet();

        // Then
        assertEquals("Analytics enabled should match", testValue, retrieved);
    }

    @Test
    public void testGetLaunchCount_defaultValue() {
        // When
        int count = dataStore.getLaunchCountSingle().blockingGet();

        // Then
        assertEquals("Default launch count should be 0", 0, count);
    }

    @Test
    public void testIncrementLaunchCount() {
        // When - increment once
        dataStore.incrementLaunchCount().blockingGet();
        int count1 = dataStore.getLaunchCountSingle().blockingGet();

        // Then
        assertEquals("Launch count should be 1", 1, count1);

        // When - increment again
        dataStore.incrementLaunchCount().blockingGet();
        int count2 = dataStore.getLaunchCountSingle().blockingGet();

        // Then
        assertEquals("Launch count should be 2", 2, count2);
    }

    @Test
    public void testIncrementLaunchCount_multiple() {
        // When - increment multiple times
        for (int i = 0; i < 5; i++) {
            dataStore.incrementLaunchCount().blockingGet();
        }
        int finalCount = dataStore.getLaunchCountSingle().blockingGet();

        // Then
        assertEquals("Launch count should be 5", 5, finalCount);
    }

    @Test
    public void testHasCompletedReview_defaultValue() {
        // When
        boolean completed = dataStore.hasCompletedReviewSingle().blockingGet();

        // Then
        assertFalse("Review should not be completed by default", completed);
    }

    @Test
    public void testSetAndGetCompletedReview_true() {
        // Given
        boolean testValue = true;

        // When
        dataStore.setCompletedReview(testValue).blockingGet();
        boolean retrieved = dataStore.hasCompletedReviewSingle().blockingGet();

        // Then
        assertEquals("Completed review should match", testValue, retrieved);
    }

    @Test
    public void testSetAndGetCompletedReview_false() {
        // Given - first set to true
        dataStore.setCompletedReview(true).blockingGet();

        // When - then set to false
        dataStore.setCompletedReview(false).blockingGet();
        boolean retrieved = dataStore.hasCompletedReviewSingle().blockingGet();

        // Then
        assertFalse("Completed review should be false", retrieved);
    }

    @Test
    public void testGetLastKnownBPLCoordinate_asFlowable() {
        // Given
        LatLng testCoordinate = new LatLng(51.5, -0.1);
        dataStore.saveLastKnownBPLCoordinate(testCoordinate).blockingGet();

        // When - use Flowable version
        LatLng retrieved = dataStore.getLastKnownBPLCoordinate().blockingFirst();

        // Then
        assertNotNull("Retrieved coordinate should not be null", retrieved);
        assertEquals("Latitude should match", testCoordinate.latitude, retrieved.latitude, 0.0001);
    }

    @Test
    public void testGetMapZoom_asFlowable() {
        // Given
        float testZoom = 14.0f;
        dataStore.saveMapZoom(testZoom, 10.0f, 20.0f).blockingGet();

        // When - use Flowable version
        float retrieved = dataStore.getMapZoom().blockingFirst();

        // Then
        assertEquals("Zoom should match", testZoom, retrieved, 0.01f);
    }

    @Test
    public void testGetAnalyticsEnabled_asFlowable() {
        // Given
        dataStore.saveAnalyticsEnabled(false).blockingGet();

        // When - use Flowable version
        boolean retrieved = dataStore.getAnalyticsEnabled().blockingFirst();

        // Then
        assertFalse("Analytics should be disabled", retrieved);
    }

    @Test
    public void testGetLaunchCount_asFlowable() {
        // Given
        dataStore.incrementLaunchCount().blockingGet();
        dataStore.incrementLaunchCount().blockingGet();

        // When - use Flowable version
        int retrieved = dataStore.getLaunchCount().blockingFirst();

        // Then
        assertEquals("Launch count should be 2", 2, retrieved);
    }

    @Test
    public void testHasCompletedReview_asFlowable() {
        // Given
        dataStore.setCompletedReview(true).blockingGet();

        // When - use Flowable version
        boolean retrieved = dataStore.hasCompletedReview().blockingFirst();

        // Then
        assertTrue("Review should be completed", retrieved);
    }

    @Test
    public void testMultipleOperations() {
        // When - perform multiple operations
        dataStore.saveLastKnownBPLCoordinate(new LatLng(51.0, -0.5)).blockingGet();
        dataStore.saveMapZoom(13.0f, 10.0f, 20.0f).blockingGet();
        dataStore.saveAnalyticsEnabled(false).blockingGet();
        dataStore.incrementLaunchCount().blockingGet();
        dataStore.setCompletedReview(true).blockingGet();

        // Then - all values should be retrievable
        LatLng coord = dataStore.getLastKnownBPLCoordinateSingle().blockingGet();
        float zoom = dataStore.getMapZoomSingle().blockingGet();
        boolean analytics = dataStore.getAnalyticsEnabledSingle().blockingGet();
        int launches = dataStore.getLaunchCountSingle().blockingGet();
        boolean review = dataStore.hasCompletedReviewSingle().blockingGet();

        assertEquals(51.0, coord.latitude, 0.0001);
        assertEquals(13.0f, zoom, 0.01f);
        assertFalse(analytics);
        assertEquals(1, launches);
        assertTrue(review);
    }
}
