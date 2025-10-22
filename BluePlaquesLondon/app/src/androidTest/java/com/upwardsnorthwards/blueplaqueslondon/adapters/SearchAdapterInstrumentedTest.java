package com.upwardsnorthwards.blueplaqueslondon.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented tests for SearchAdapter.
 * These tests run on an Android device or emulator with real views.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class SearchAdapterInstrumentedTest {

    private Context context;
    private SearchAdapter adapter;
    private List<Placemark> testPlacemarks;
    private ViewGroup parent;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        testPlacemarks = createTestPlacemarks();
        adapter = new SearchAdapter(context, testPlacemarks);

        // Create a parent ViewGroup for view inflation
        parent = new FrameLayout(context);
    }

    @Test
    public void testAdapterCreation() {
        // Then
        assertNotNull("Adapter should be created", adapter);
        assertNotNull("Adapter should have placemarks", testPlacemarks);
    }

    @Test
    public void testGetCount() {
        // When
        int count = adapter.getCount();

        // Then - should have placemarks + 1 for "closest" option
        assertEquals(testPlacemarks.size() + 1, count);
    }

    @Test
    public void testGetItem() {
        // When
        Placemark item = adapter.getItem(0);

        // Then
        assertNotNull("Should return an item", item);
    }

    @Test
    public void testGetView() {
        // When - get view for first position (closest)
        View view = adapter.getView(0, null, parent);

        // Then
        assertNotNull("View should be created", view);
        assertNotNull("View should have tag", view.getTag());
        assertTrue("Tag should be ViewHolder", view.getTag() instanceof SearchAdapter.ViewHolder);
    }

    @Test
    public void testGetViewRecycling() {
        // Given - create a view first
        View view1 = adapter.getView(0, null, parent);

        // When - recycle the view
        View view2 = adapter.getView(1, view1, parent);

        // Then - should reuse the same view
        assertNotNull("Recycled view should not be null", view2);
    }

    @Test
    public void testGetViewForClosestPosition() {
        // When - position 0 is always "closest"
        View view = adapter.getView(0, null, parent);

        // Then
        assertNotNull("View should be created for closest position", view);
        SearchAdapter.ViewHolder holder = (SearchAdapter.ViewHolder) view.getTag();
        assertNotNull("ViewHolder should exist", holder);
        assertNotNull("Title should exist", holder.title);
    }

    @Test
    public void testGetViewForPlacemarkPosition() {
        // When - position 1+ should be actual placemarks
        View view = adapter.getView(1, null, parent);

        // Then
        assertNotNull("View should be created for placemark position", view);
        SearchAdapter.ViewHolder holder = (SearchAdapter.ViewHolder) view.getTag();
        assertNotNull("ViewHolder should exist", holder);
        assertNotNull("Placemark should be set", holder.placemark);
    }


    @Test
    public void testSetPlacemarks() {
        // Given
        List<Placemark> newPlacemarks = new ArrayList<>();
        newPlacemarks.add(createPlacemark("1", "New Person"));

        // When
        adapter.setPlacemarks(newPlacemarks);

        // Then - count should update (new list + closest option)
        int count = adapter.getCount();
        assertEquals(newPlacemarks.size() + 1, count);
    }

    @Test
    public void testGetFilteredPlacemarkAtPosition() {
        // When
        Placemark placemark = adapter.getFilteredPlacemarkAtPosition(0);

        // Then
        assertNotNull("Should return a placemark", placemark);
    }

    @Test
    public void testMultipleGetViewCalls() {
        // When - simulate ListView scrolling
        View view1 = adapter.getView(0, null, parent);
        View view2 = adapter.getView(1, null, parent);
        View view3 = adapter.getView(2, null, parent);

        // Then - all views should be created successfully
        assertNotNull("First view should be created", view1);
        assertNotNull("Second view should be created", view2);
        assertNotNull("Third view should be created", view3);
    }

    private List<Placemark> createTestPlacemarks() {
        List<Placemark> placemarks = new ArrayList<>();
        placemarks.add(createPlacemark("1", "Charles Darwin"));
        placemarks.add(createPlacemark("2", "Alan Turing"));
        placemarks.add(createPlacemark("3", "Ada Lovelace"));
        placemarks.add(createPlacemark("4", "Isaac Newton"));
        return placemarks;
    }

    private Placemark createPlacemark(String id, String name) {
        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setTitle(name + " (1800-1900)");
        placemark.setFeatureDescription(name + "<br>Scientist<br>London");
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);
        return placemark;
    }
}
