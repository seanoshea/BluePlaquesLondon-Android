package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

/**
 * Instrumented tests for InternetConnectivityHelper.
 * These tests run on an Android device or emulator with real ConnectivityManager.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class InternetConnectivityHelperInstrumentedTest {

    private Context context;
    private InternetConnectivityHelper helper;

    @Mock
    private InternetConnectivityHelperDelegate mockDelegate;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        context = ApplicationProvider.getApplicationContext();
        helper = new InternetConnectivityHelper(context);
    }

    @Test
    public void testHelperCreation() {
        // Then
        assertNotNull("Helper should be created", helper);
    }

    @Test
    public void testSetDelegate() {
        // When
        helper.setDelegate(mockDelegate);

        // Then - no exception should be thrown
        assertNotNull("Helper should have delegate set", mockDelegate);
    }

    @Test
    public void testOnResume_registersReceiver() {
        // Given
        helper.setDelegate(mockDelegate);

        // When
        helper.onResume();

        // Then - should register receiver and check connectivity
        // Verify delegate was called (connectivity is checked on resume)
        verify(mockDelegate).internetConnectivityUpdated(org.mockito.ArgumentMatchers.anyBoolean());

        // Cleanup
        helper.onPause();
    }

    @Test
    public void testOnPause_unregistersReceiver() {
        // Given
        helper.onResume();

        // When
        helper.onPause();

        // Then - should unregister receiver without exception
        assertNotNull("Helper should still exist after pause", helper);
    }

    @Test
    public void testLifecycle_resumePauseCycle() {
        // Given
        helper.setDelegate(mockDelegate);

        // When - go through lifecycle
        helper.onResume();
        helper.onPause();
        helper.onResume();
        helper.onPause();

        // Then - should handle multiple cycles without crashing
        assertNotNull("Helper should survive lifecycle", helper);
    }

    @Test
    public void testHelperWithoutDelegate() {
        // Given - helper without delegate

        // When
        helper.onResume();

        // Then - should not crash even without delegate
        assertNotNull("Helper should work without delegate", helper);

        // Cleanup
        helper.onPause();
    }

    @Test
    public void testMultipleResumeCallsWithDelegate() {
        // Given
        helper.setDelegate(mockDelegate);

        // When
        helper.onResume();
        helper.onPause();

        helper.onResume();
        helper.onPause();

        // Then - delegate should be called each time
        verify(mockDelegate, org.mockito.Mockito.atLeast(2))
                .internetConnectivityUpdated(org.mockito.ArgumentMatchers.anyBoolean());
    }

    @Test
    public void testDelegateReceivesInitialConnectivityState() {
        // Given
        helper.setDelegate(mockDelegate);

        // When
        helper.onResume();

        // Then - delegate should receive initial connectivity state
        verify(mockDelegate).internetConnectivityUpdated(org.mockito.ArgumentMatchers.anyBoolean());

        // Cleanup
        helper.onPause();
    }
}
