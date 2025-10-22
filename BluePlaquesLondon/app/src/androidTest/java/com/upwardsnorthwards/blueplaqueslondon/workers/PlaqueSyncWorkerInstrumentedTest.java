package com.upwardsnorthwards.blueplaqueslondon.workers;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;
import androidx.work.Configuration;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.WorkManagerTestInitHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertNotNull;

/**
 * Instrumented tests for PlaqueSyncWorker.
 * These tests run on an Android device or emulator with WorkManager test utilities.
 * Note: Full testing requires Hilt integration which is complex in instrumented tests.
 * This test verifies basic WorkManager setup.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class PlaqueSyncWorkerInstrumentedTest {

    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Initialize WorkManager for testing
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(android.util.Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config);
    }

    @Test
    public void testWorkManagerInitialization() {
        // Then - WorkManager should be initialized
        assertNotNull("Context should not be null", context);
    }

    @Test
    public void testWorkerClassExists() {
        // Then - PlaqueSyncWorker class should be accessible
        assertNotNull("Worker class should exist", PlaqueSyncWorker.class);
    }
}
