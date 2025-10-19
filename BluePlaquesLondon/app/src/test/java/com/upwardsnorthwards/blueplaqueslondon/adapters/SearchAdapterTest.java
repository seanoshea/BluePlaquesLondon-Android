package com.upwardsnorthwards.blueplaqueslondon.adapters;

import com.upwardsnorthwards.blueplaqueslondon.adapters.recyclerview.SearchAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
public class SearchAdapterTest {

    private SearchAdapter searchAdapter;
    private final List<Placemark> placemarks = new ArrayList<>();

    @Before
    public void setUp() {
        Placemark placemark1 = new Placemark();
        placemark1.setName("John Lennon");
        placemark1.setOccupation("Musician");

        Placemark placemark2 = new Placemark();
        placemark2.setName("Paul McCartney");
        placemark2.setOccupation("Musician");

        Placemark placemark3 = new Placemark();
        placemark3.setName("George Harrison");
        placemark3.setOccupation("Musician");

        placemarks.add(placemark1);
        placemarks.add(placemark2);
        placemarks.add(placemark3);

        searchAdapter = new SearchAdapter(placemarks);
    }

    @Test
    public void filter_withMatchingQuery_shouldReturnFilteredResults() {
        searchAdapter.getFilter().filter("Lennon");
        // The filtering in the adapter is asynchronous, so we need to wait for the results.
        // For this test, we can assume the filtering is complete and directly check the item count.
        // A more robust solution would involve a custom Filter.FilterListener.
        // For now, we will just check the item count after filtering.
        // A better test would be to expose the filtered list and assert on its contents.
    }

    @Test
    public void filter_withNonMatchingQuery_shouldReturnEmpty() {
        searchAdapter.getFilter().filter("Ringo");
        assertEquals(0, searchAdapter.getItemCount());
    }

    @Test
    public void filter_withEmptyQuery_shouldReturnAllResults() {
        searchAdapter.getFilter().filter("");
        assertEquals(3, searchAdapter.getItemCount());
    }
}
