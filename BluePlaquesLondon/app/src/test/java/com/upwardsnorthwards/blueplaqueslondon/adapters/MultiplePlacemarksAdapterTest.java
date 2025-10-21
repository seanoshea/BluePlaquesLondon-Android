package com.upwardsnorthwards.blueplaqueslondon.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Unit tests for MultiplePlacemarksAdapter.
 */
@RunWith(RobolectricTestRunner.class)
public class MultiplePlacemarksAdapterTest {

    private Context context;
    private List<Placemark> placemarks;
    private MultiplePlacemarksAdapter adapter;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        placemarks = createTestPlacemarks();
        adapter = new MultiplePlacemarksAdapter(context, placemarks);
    }

    @Test
    public void testConstructor() {
        assertNotNull(adapter);
    }

    @Test
    public void testGetCount() {
        int count = adapter.getCount();
        assertEquals(2, count);
    }

    @Test
    public void testGetCount_emptyList() {
        MultiplePlacemarksAdapter emptyAdapter = new MultiplePlacemarksAdapter(context, new ArrayList<>());
        assertEquals(0, emptyAdapter.getCount());
    }

    // Note: getView tests are commented out due to resource/layout requirements
    // These would be better suited for instrumented tests

    @Test
    public void testGetItem() {
        Placemark item = adapter.getItem(0);
        assertNotNull(item);
        assertEquals("Charles Darwin", item.getName());
    }

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
        p2.setName("Alfred Wallace");
        p2.setTitle("Alfred Wallace (1823-1913)");
        p2.setFeatureDescription("Alfred Wallace<br>Naturalist");
        p2.setLatitude(51.5074);
        p2.setLongitude(-0.1278);
        list.add(p2);

        return list;
    }
}
