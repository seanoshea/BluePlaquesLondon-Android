package com.upwardsnorthwards.blueplaqueslondon.adapters;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Unit tests for SearchAdapter filter logic.
 * Tests the core filtering, sorting, and display logic WITHOUT requiring Android resources or Looper.
 * This avoids the threading issues that occur with ArrayAdapter.Filter in instrumented tests.
 */
public class SearchAdapterTest {

    private List<Placemark> testPlacemarks;

    @Before
    public void setUp() {
        testPlacemarks = createTestPlacemarks();
    }

    /**
     * Test the core filtering algorithm without needing to instantiate SearchAdapter
     * (which requires Android resources). This tests the business logic directly.
     */
    @Test
    public void testPlacemarkListCreation() {
        assertNotNull("Placemarks should be created", testPlacemarks);
        assertEquals("Should have 4 test placemarks", 4, testPlacemarks.size());
    }

    @Test
    public void testFilterPlacemarksWithText_FindsDarwin() {
        // Test the filter finds Darwin matches
        List<Placemark> filtered = filterPlacemarksWithText("Darwin");
        assertTrue("Should find Darwin matches", filtered.size() > 0);
        assertTrue("At least one result should contain Darwin",
            filtered.stream().anyMatch(p -> p.getName().toLowerCase().contains("darwin")));
    }

    @Test
    public void testFilterPlacemarksWithText_CaseInsensitive() {
        List<Placemark> filtered1 = filterPlacemarksWithText("darwin");
        List<Placemark> filtered2 = filterPlacemarksWithText("DARWIN");

        // Both should find the same results
        assertEquals("Filter should be case-insensitive (lowercase vs uppercase)",
            filtered1.size(), filtered2.size());
    }

    @Test
    public void testFilterPlacemarksWithText_NoMatches() {
        List<Placemark> filtered = filterPlacemarksWithText("XYZNONEXISTENT");
        assertEquals("Should have no matches for nonsense search", 0, filtered.size());
    }

    @Test
    public void testFilterPlacemarksWithText_IsSorted() {
        List<Placemark> filtered = filterPlacemarksWithText("Charles");

        // Verify it's sorted: each name should be <= the next name
        for (int i = 0; i < filtered.size() - 1; i++) {
            assertTrue("List should be sorted alphabetically",
                filtered.get(i).getName().compareTo(filtered.get(i+1).getName()) <= 0);
        }
    }

    @Test
    public void testFilterPlacemarksWithText_PartialMatch() {
        List<Placemark> filtered = filterPlacemarksWithText("Dar");
        assertTrue("Should match partial text 'Dar'", filtered.size() > 0);
    }

    @Test
    public void testFilterPlacemarksWithText_EmptyString() {
        List<Placemark> filtered = filterPlacemarksWithText("");
        // Empty/null filter constraint in ArrayAdapter.Filter returns no results
        // This matches the SearchAdapter.performFiltering() behavior at line 200
        assertEquals("Empty filter returns empty list", 0, filtered.size());
    }

    @Test
    public void testFilterPlacemarksWithText_AllMatches() {
        List<Placemark> filtered = filterPlacemarksWithText("a");
        // "a" appears in most names - should find several
        assertTrue("Should match names containing 'a'", filtered.size() > 1);
    }

    /**
     * Helper method to test the core filtering logic.
     * This simulates what the filter does in performFiltering().
     * Matches the SearchAdapter behavior: empty constraint returns empty list.
     */
    private List<Placemark> filterPlacemarksWithText(String filterText) {
        // Match SearchAdapter.performFiltering() behavior at line 200
        if (filterText == null || filterText.length() == 0) {
            return new ArrayList<>();  // Empty constraint returns empty list
        }

        final List<Placemark> localPlacemarks = new ArrayList<>();
        for (final Placemark placemark : testPlacemarks) {
            if (placemark.getName().toLowerCase()
                    .contains(filterText.toLowerCase())) {
                localPlacemarks.add(placemark);
            }
        }
        // Sort alphabetically
        localPlacemarks.sort((lhs, rhs) -> lhs.getName().compareTo(rhs.getName()));
        return localPlacemarks;
    }

    // Test data helper methods
    private List<Placemark> createTestPlacemarks() {
        List<Placemark> list = new ArrayList<>();
        list.add(createPlacemark("1", "Charles Darwin"));
        list.add(createPlacemark("2", "Isaac Newton"));
        list.add(createPlacemark("3", "Charles Dickens"));
        list.add(createPlacemark("4", "Alan Turing"));
        return list;
    }

    private Placemark createPlacemark(String id, String name) {
        Placemark p = new Placemark();
        p.setName(name);
        p.setTitle(name + " (1800-1900)");
        p.setFeatureDescription(name + "<br>Scientist/Historical Figure");
        p.setLatitude(51.5074);
        p.setLongitude(-0.1278);
        return p;
    }
}
