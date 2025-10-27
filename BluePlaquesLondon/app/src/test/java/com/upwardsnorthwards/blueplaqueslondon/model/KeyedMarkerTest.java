package com.upwardsnorthwards.blueplaqueslondon.model;

import com.google.android.gms.maps.model.Marker;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive unit tests for KeyedMarker.
 * Tests edge cases, error scenarios, and boundary conditions.
 */
@RunWith(RobolectricTestRunner.class)
public class KeyedMarkerTest {

    @Mock
    private Marker mockMarker;

    private KeyedMarker keyedMarker;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        keyedMarker = new KeyedMarker();
    }

    @Test
    public void constructor_shouldCreateEmptyKeyedMarker() {
        // When
        KeyedMarker newKeyedMarker = new KeyedMarker();

        // Then
        assertNotNull(newKeyedMarker);
        assertNull(newKeyedMarker.getKey());
        assertNull(newKeyedMarker.getMarker());
    }

    @Test
    public void setKey_withValidKey_shouldSetKey() {
        // Given
        String key = "51.5074-0.1278";

        // When
        keyedMarker.setKey(key);

        // Then
        assertEquals(key, keyedMarker.getKey());
    }

    @Test
    public void setKey_withNullKey_shouldSetNull() {
        // When
        keyedMarker.setKey(null);

        // Then
        assertNull(keyedMarker.getKey());
    }

    @Test
    public void setKey_withEmptyKey_shouldSetEmpty() {
        // Given
        String emptyKey = "";

        // When
        keyedMarker.setKey(emptyKey);

        // Then
        assertEquals("", keyedMarker.getKey());
    }

    @Test
    public void setKey_withSpecialCharacters_shouldSetKey() {
        // Given
        String specialKey = "key_with-special.chars@123";

        // When
        keyedMarker.setKey(specialKey);

        // Then
        assertEquals(specialKey, keyedMarker.getKey());
    }

    @Test
    public void setKey_withUnicodeCharacters_shouldSetKey() {
        // Given
        String unicodeKey = "key_with_émojis_🗺️_and_unicode_\u2603";

        // When
        keyedMarker.setKey(unicodeKey);

        // Then
        assertEquals(unicodeKey, keyedMarker.getKey());
    }

    @Test
    public void setKey_withVeryLongKey_shouldSetKey() {
        // Given - very long key
        StringBuilder longKey = new StringBuilder();
        for (int i = 0; i < 1000; i++) {
            longKey.append("very_long_key_");
        }
        String key = longKey.toString();

        // When
        keyedMarker.setKey(key);

        // Then
        assertEquals(key, keyedMarker.getKey());
        assertTrue(keyedMarker.getKey().length() > 10000);
    }

    @Test
    public void setMarker_withValidMarker_shouldSetMarker() {
        // When
        keyedMarker.setMarker(mockMarker);

        // Then
        assertEquals(mockMarker, keyedMarker.getMarker());
    }

    @Test
    public void setMarker_withNullMarker_shouldSetNull() {
        // When
        keyedMarker.setMarker(null);

        // Then
        assertNull(keyedMarker.getMarker());
    }

    @Test
    public void setKeyAndMarker_shouldSetBoth() {
        // Given
        String key = "test_key";

        // When
        keyedMarker.setKey(key);
        keyedMarker.setMarker(mockMarker);

        // Then
        assertEquals(key, keyedMarker.getKey());
        assertEquals(mockMarker, keyedMarker.getMarker());
    }

    @Test
    public void getKey_initialState_shouldReturnNull() {
        // Given - fresh KeyedMarker
        KeyedMarker newKeyedMarker = new KeyedMarker();

        // Then
        assertNull(newKeyedMarker.getKey());
    }

    @Test
    public void getMarker_initialState_shouldReturnNull() {
        // Given - fresh KeyedMarker
        KeyedMarker newKeyedMarker = new KeyedMarker();

        // Then
        assertNull(newKeyedMarker.getMarker());
    }

    @Test
    public void setKey_multipleUpdates_shouldUpdateCorrectly() {
        // Given
        String firstKey = "first_key";
        String secondKey = "second_key";

        // When
        keyedMarker.setKey(firstKey);
        assertEquals(firstKey, keyedMarker.getKey());

        keyedMarker.setKey(secondKey);

        // Then
        assertEquals(secondKey, keyedMarker.getKey());
    }

    @Test
    public void setMarker_multipleUpdates_shouldUpdateCorrectly() {
        // Given
        Marker firstMarker = mockMarker;
        // Create another mock for testing
        Marker secondMarker = org.mockito.Mockito.mock(Marker.class);

        // When
        keyedMarker.setMarker(firstMarker);
        assertEquals(firstMarker, keyedMarker.getMarker());

        keyedMarker.setMarker(secondMarker);

        // Then
        assertEquals(secondMarker, keyedMarker.getMarker());
    }

    @Test
    public void setKey_afterSettingMarker_shouldNotAffectMarker() {
        // Given
        String key = "test_key";
        keyedMarker.setMarker(mockMarker);

        // When
        keyedMarker.setKey(key);

        // Then
        assertEquals(key, keyedMarker.getKey());
        assertEquals(mockMarker, keyedMarker.getMarker()); // Marker should remain unchanged
    }

    @Test
    public void setMarker_afterSettingKey_shouldNotAffectKey() {
        // Given
        String key = "test_key";
        keyedMarker.setKey(key);

        // When
        keyedMarker.setMarker(mockMarker);

        // Then
        assertEquals(key, keyedMarker.getKey()); // Key should remain unchanged
        assertEquals(mockMarker, keyedMarker.getMarker());
    }

    @Test
    public void resetToNull_shouldClearBothFields() {
        // Given - KeyedMarker with both fields set
        keyedMarker.setKey("test_key");
        keyedMarker.setMarker(mockMarker);

        // When - reset both to null
        keyedMarker.setKey(null);
        keyedMarker.setMarker(null);

        // Then
        assertNull(keyedMarker.getKey());
        assertNull(keyedMarker.getMarker());
    }

    @Test
    public void typicalUsageScenario_shouldWorkCorrectly() {
        // Simulate typical usage pattern
        
        // Step 1: Create and set up KeyedMarker
        String placemarkKey = Placemark.keyFromLatLng(51.5074, -0.1278);
        keyedMarker.setKey(placemarkKey);
        keyedMarker.setMarker(mockMarker);

        // Step 2: Verify setup
        assertEquals(placemarkKey, keyedMarker.getKey());
        assertEquals(mockMarker, keyedMarker.getMarker());

        // Step 3: Simulate lookup scenario
        String searchKey = Placemark.keyFromLatLng(51.5074, -0.1278);
        boolean found = keyedMarker.getKey().equals(searchKey);
        
        assertTrue(found);
        if (found) {
            Marker foundMarker = keyedMarker.getMarker();
            assertEquals(mockMarker, foundMarker);
        }
    }

    @Test
    public void edgeCases_keyComparison() {
        // Test key comparison edge cases
        
        // Case 1: Identical keys
        keyedMarker.setKey("identical_key");
        assertTrue(keyedMarker.getKey().equals("identical_key"));
        
        // Case 2: Case sensitivity
        keyedMarker.setKey("CaseSensitive");
        assertFalse(keyedMarker.getKey().equals("casesensitive"));
        
        // Case 3: Whitespace differences
        keyedMarker.setKey("key_with_spaces");
        assertFalse(keyedMarker.getKey().equals(" key_with_spaces "));
    }
}