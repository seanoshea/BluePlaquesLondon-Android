package com.upwardsnorthwards.blueplaqueslondon.activities;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for BaseActivity.
 */
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class BaseActivityTest {

    private ActivityController<BaseActivity> controller;
    private BaseActivity activity;

    @Before
    public void setUp() {
        controller = Robolectric.buildActivity(BaseActivity.class);
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
    public void testActivityPauseResume() {
        activity = controller.create().start().resume().get();
        controller.pause().resume();
        assertNotNull(activity);
    }

    @Test
    public void testActivityDestroy() {
        activity = controller.create().start().resume().get();
        controller.pause().stop().destroy();
        assertTrue("Activity should be destroyed", activity.isDestroyed());
    }
}
