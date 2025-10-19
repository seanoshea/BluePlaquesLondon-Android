package com.upwardsnorthwards.blueplaqueslondon.model;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(RobolectricTestRunner.class)
public class MapModelTest {

    private MapModel mapModel;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        mapModel = new MapModel();
    }

    @Test
    public void loadMapData_shouldLoadAndParsePlacemarks() {
        mapModel.loadMapData(context);

        assertNotNull(mapModel.getMassagedPlacemarks());
        assertFalse(mapModel.getMassagedPlacemarks().isEmpty());
    }
}
