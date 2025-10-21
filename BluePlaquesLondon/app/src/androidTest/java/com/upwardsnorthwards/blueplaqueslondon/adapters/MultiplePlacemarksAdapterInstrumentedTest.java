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
 * Instrumented tests for MultiplePlacemarksAdapter.
 * These tests run on an Android device or emulator with real views.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class MultiplePlacemarksAdapterInstrumentedTest {

    private Context context;
    private MultiplePlacemarksAdapter adapter;
    private List<Placemark> testPlacemarks;
    private ViewGroup parent;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        testPlacemarks = createTestPlacemarks();
        adapter = new MultiplePlacemarksAdapter(context, testPlacemarks);

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

        // Then
        assertEquals(testPlacemarks.size(), count);
    }

    @Test
    public void testGetCountWithEmptyList() {
        // Given - adapter with empty list
        MultiplePlacemarksAdapter emptyAdapter = new MultiplePlacemarksAdapter(context, new ArrayList<>());

        // When
        int count = emptyAdapter.getCount();

        // Then
        assertEquals(0, count);
    }

    @Test
    public void testGetView() {
        // When
        View view = adapter.getView(0, null, parent);

        // Then
        assertNotNull("View should be created", view);
        assertNotNull("View should have tag", view.getTag());
        assertTrue("Tag should be ViewHolder", view.getTag() instanceof MultiplePlacemarksAdapter.ViewHolder);
    }

    @Test
    public void testGetViewRecycling() {
        // Given - create a view first
        View view1 = adapter.getView(0, null, parent);

        // When - recycle the view
        View view2 = adapter.getView(1, view1, parent);

        // Then - should reuse the same view
        assertNotNull("Recycled view should not be null", view2);
        assertEquals("Should reuse the same view instance", view1, view2);
    }

    @Test
    public void testGetViewSetsPlacemark() {
        // When
        View view = adapter.getView(0, null, parent);

        // Then
        MultiplePlacemarksAdapter.ViewHolder holder = (MultiplePlacemarksAdapter.ViewHolder) view.getTag();
        assertNotNull("ViewHolder should exist", holder);
        assertNotNull("Placemark should be set in holder", holder.placemark);
        assertEquals("Should be first placemark", testPlacemarks.get(0).getName(), holder.placemark.getName());
    }

    @Test
    public void testGetViewSetsTitle() {
        // When
        View view = adapter.getView(0, null, parent);

        // Then
        MultiplePlacemarksAdapter.ViewHolder holder = (MultiplePlacemarksAdapter.ViewHolder) view.getTag();
        assertNotNull("Title TextView should exist", holder.title);
        assertNotNull("Title text should be set", holder.title.getText());
    }

    @Test
    public void testGetViewForAllPositions() {
        // When - get views for all placemarks
        for (int i = 0; i < testPlacemarks.size(); i++) {
            View view = adapter.getView(i, null, parent);

            // Then
            assertNotNull("View should be created for position " + i, view);
            MultiplePlacemarksAdapter.ViewHolder holder = (MultiplePlacemarksAdapter.ViewHolder) view.getTag();
            assertNotNull("Placemark should be set at position " + i, holder.placemark);
            assertEquals("Placemark at position " + i + " should match",
                    testPlacemarks.get(i).getName(), holder.placemark.getName());
        }
    }

    @Test
    public void testGetViewWithSinglePlacemark() {
        // Given - adapter with single placemark
        List<Placemark> singlePlacemark = new ArrayList<>();
        singlePlacemark.add(createPlacemark("1", "Single Person"));
        MultiplePlacemarksAdapter singleAdapter = new MultiplePlacemarksAdapter(context, singlePlacemark);

        // When
        View view = singleAdapter.getView(0, null, parent);

        // Then
        assertNotNull("View should be created", view);
        MultiplePlacemarksAdapter.ViewHolder holder = (MultiplePlacemarksAdapter.ViewHolder) view.getTag();
        assertEquals("Single Person", holder.placemark.getName());
    }

    @Test
    public void testGetViewWithMultiplePlacemarks() {
        // Given - adapter with multiple placemarks at same location
        List<Placemark> multiplePlacemarks = new ArrayList<>();
        multiplePlacemarks.add(createPlacemark("1", "Person One"));
        multiplePlacemarks.add(createPlacemark("2", "Person Two"));
        multiplePlacemarks.add(createPlacemark("3", "Person Three"));
        MultiplePlacemarksAdapter multiAdapter = new MultiplePlacemarksAdapter(context, multiplePlacemarks);

        // When
        int count = multiAdapter.getCount();

        // Then
        assertEquals(3, count);

        // Verify all views can be created
        for (int i = 0; i < count; i++) {
            View view = multiAdapter.getView(i, null, parent);
            assertNotNull("View " + i + " should be created", view);
        }
    }

    @Test
    public void testViewHolderStructure() {
        // When
        View view = adapter.getView(0, null, parent);

        // Then
        MultiplePlacemarksAdapter.ViewHolder holder = (MultiplePlacemarksAdapter.ViewHolder) view.getTag();
        assertNotNull("ViewHolder should have title field", holder.title);
        assertNotNull("ViewHolder should have placemark field", holder.placemark);
    }

    @Test
    public void testGetViewWithConvertView() {
        // Given - get initial view
        View convertView = adapter.getView(0, null, parent);
        Object originalTag = convertView.getTag();

        // When - pass it as convertView for next position
        View recycledView = adapter.getView(1, convertView, parent);

        // Then - should reuse the view and update its content
        assertNotNull("Recycled view should not be null", recycledView);
        assertEquals("Should return same view instance", convertView, recycledView);
        assertEquals("Tag should remain same", originalTag, recycledView.getTag());

        // Verify the placemark was updated
        MultiplePlacemarksAdapter.ViewHolder holder = (MultiplePlacemarksAdapter.ViewHolder) recycledView.getTag();
        assertEquals("Placemark should be updated to position 1",
                testPlacemarks.get(1).getName(), holder.placemark.getName());
    }

    private List<Placemark> createTestPlacemarks() {
        List<Placemark> placemarks = new ArrayList<>();
        placemarks.add(createPlacemark("1", "Charles Darwin"));
        placemarks.add(createPlacemark("2", "Mary Darwin"));
        placemarks.add(createPlacemark("3", "Francis Darwin"));
        return placemarks;
    }

    private Placemark createPlacemark(String id, String name) {
        Placemark placemark = new Placemark();
        placemark.setName(name);
        placemark.setTitle(name + " (1800-1900)");
        placemark.setFeatureDescription(name + "<br>Scientist<br>12 Upper Gower Street");
        placemark.setLatitude(51.5074);
        placemark.setLongitude(-0.1278);
        return placemark;
    }
}
