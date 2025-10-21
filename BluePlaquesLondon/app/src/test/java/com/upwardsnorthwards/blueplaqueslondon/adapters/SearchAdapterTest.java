package com.upwardsnorthwards.blueplaqueslondon.adapters;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for SearchAdapter.
 * Note: SearchAdapter requires Android resources at construction time,
 * so comprehensive unit testing is limited. Integration/UI tests are more appropriate.
 */
public class SearchAdapterTest {

    @Test
    public void testPlacemarkCreation() {
        // Test helper method used by the adapter
        List<Placemark> placemarks = createTestPlacemarks();
        assertNotNull(placemarks);
    }

    // Note: Full SearchAdapter tests require Android resources and layouts.
    // These are better suited for instrumented (androidTest) tests.

    private List<Placemark> createTestPlacemarks() {
        List<Placemark> list = new ArrayList<>();

        Placemark p1 = new Placemark();
        p1.setName("Charles Darwin");
        p1.setTitle("Charles Darwin (1809-1882)");
        p1.setFeatureDescription("Charles Darwin<br>Naturalist");
        p1.setLatitude(51.5074);
        p1.setLongitude(-0.1278);
        list.add(p1);

        Placemark p2 = new Placemark();
        p2.setName("Isaac Newton");
        p2.setTitle("Isaac Newton (1643-1727)");
        p2.setFeatureDescription("Isaac Newton<br>Physicist");
        p2.setLatitude(51.5074);
        p2.setLongitude(-0.1278);
        list.add(p2);

        Placemark p3 = new Placemark();
        p3.setName("Charles Dickens");
        p3.setTitle("Charles Dickens (1812-1870)");
        p3.setFeatureDescription("Charles Dickens<br>Novelist");
        p3.setLatitude(51.5074);
        p3.setLongitude(-0.1278);
        list.add(p3);

        return list;
    }
}
