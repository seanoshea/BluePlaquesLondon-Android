package com.upwardsnorthwards.blueplaqueslondon.activities;

import android.content.Intent;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

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
 * Unit tests for MapDetailActivity.
 * Note: Disabled due to resource requirements. Use instrumented tests.
 */
@Ignore("MapDetailActivity requires resources - use instrumented tests instead")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class MapDetailActivityTest {

    private ActivityController<MapDetailActivity> controller;
    private MapDetailActivity activity;

    @Before
    public void setUp() {
        // Create intent with placemark data
        Intent intent = new Intent();
        Placemark placemark = createTestPlacemark();
        intent.putExtra(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY, placemark);

        controller = Robolectric.buildActivity(MapDetailActivity.class, intent);
    }

    @Test
    public void testActivityCreation() {
        activity = controller.create().get();
        assertNotNull(activity);
    }

    @Test
    public void testActivityLifecycle() {
        activity = controller.create().start().resume().get();
        assertNotNull(activity);
        assertTrue("Activity should not be finishing", !activity.isFinishing());
    }

    @Test
    public void testActivityWithPlacemarkIntent() {
        activity = controller.create().start().resume().get();
        assertNotNull(activity);
        // Activity should handle placemark data from intent
    }

    private Placemark createTestPlacemark() {
        Placemark placemark = new Placemark();
        placemark.setName("Charles Darwin");
        placemark.setTitle("Charles Darwin (1809-1882)");
        placemark.setFeatureDescription("Charles Darwin<br>Naturalist<br>12 Upper Gower Street");
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);
        placemark.setStyleUrl("#myDefaultStyles");
        return placemark;
    }
}
