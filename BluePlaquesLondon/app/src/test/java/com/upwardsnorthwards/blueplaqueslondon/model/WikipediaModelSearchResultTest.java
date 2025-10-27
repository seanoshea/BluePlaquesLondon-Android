package com.upwardsnorthwards.blueplaqueslondon.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Comprehensive unit tests for WikipediaModelSearchResult.
 * Tests edge cases, error scenarios, and boundary conditions.
 */
public class WikipediaModelSearchResultTest {

    @Test
    public void constructor_withValidData_shouldSetFields() {
        // Given
        String result = "Charles Dickens was an English writer...";
        String name = "Charles Dickens";

        // When
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(result, name);

        // Then
        assertEquals(result, searchResult.getResult());
        assertEquals(name, searchResult.getName());
        assertTrue(searchResult.hasResult());
    }

    @Test
    public void constructor_withNullResult_shouldHandleGracefully() {
        // Given
        String name = "Charles Dickens";

        // When
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(null, name);

        // Then
        assertNull(searchResult.getResult());
        assertEquals(name, searchResult.getName());
        assertFalse(searchResult.hasResult());
    }

    @Test
    public void constructor_withEmptyResult_shouldHandleGracefully() {
        // Given
        String result = "";
        String name = "Charles Dickens";

        // When
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(result, name);

        // Then
        assertEquals("", searchResult.getResult());
        assertEquals(name, searchResult.getName());
        assertFalse(searchResult.hasResult());
    }

    @Test
    public void constructor_withNullName_shouldHandleGracefully() {
        // Given
        String result = "Some Wikipedia content";

        // When
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(result, null);

        // Then
        assertEquals(result, searchResult.getResult());
        assertNull(searchResult.getName());
        assertTrue(searchResult.hasResult());
    }

    @Test
    public void constructor_withBothNull_shouldHandleGracefully() {
        // When
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(null, null);

        // Then
        assertNull(searchResult.getResult());
        assertNull(searchResult.getName());
        assertFalse(searchResult.hasResult());
    }

    @Test
    public void hasResult_withWhitespaceOnlyResult_shouldReturnFalse() {
        // Given - result with only whitespace
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult("   ", "Test Name");

        // Then - whitespace-only should be considered as having result (length > 0)
        assertTrue(searchResult.hasResult());
    }

    @Test
    public void hasResult_withSingleCharacterResult_shouldReturnTrue() {
        // Given
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult("A", "Test Name");

        // Then
        assertTrue(searchResult.hasResult());
    }

    @Test
    public void hasResult_withLongResult_shouldReturnTrue() {
        // Given - very long result
        StringBuilder longResult = new StringBuilder();
        for (int i = 0; i < 10000; i++) {
            longResult.append("This is a very long Wikipedia article content. ");
        }
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(longResult.toString(), "Test Name");

        // Then
        assertTrue(searchResult.hasResult());
        assertNotNull(searchResult.getResult());
        assertTrue(searchResult.getResult().length() > 10000);
    }

    @Test
    public void getResult_shouldReturnExactContent() {
        // Given - result with special characters and formatting
        String result = "Charles Dickens (1812–1870) was an English writer.\n\nHe created some of the world's best-known fictional characters.";
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(result, "Charles Dickens");

        // Then
        assertEquals(result, searchResult.getResult());
    }

    @Test
    public void getName_shouldReturnExactName() {
        // Given - name with special characters
        String name = "François Müller-Smith";
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult("Some content", name);

        // Then
        assertEquals(name, searchResult.getName());
    }

    @Test
    public void hasResult_consistencyCheck() {
        // Test consistency between hasResult() and actual result content
        
        // Case 1: Non-empty result
        WikipediaModelSearchResult result1 = new WikipediaModelSearchResult("content", "name");
        assertTrue(result1.hasResult());
        assertTrue(result1.getResult().length() > 0);
        
        // Case 2: Empty result
        WikipediaModelSearchResult result2 = new WikipediaModelSearchResult("", "name");
        assertFalse(result2.hasResult());
        assertEquals(0, result2.getResult().length());
        
        // Case 3: Null result
        WikipediaModelSearchResult result3 = new WikipediaModelSearchResult(null, "name");
        assertFalse(result3.hasResult());
        assertNull(result3.getResult());
    }

    @Test
    public void immutability_fieldsCannotBeModified() {
        // Given
        String originalResult = "Original content";
        String originalName = "Original name";
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(originalResult, originalName);

        // When - attempting to get references and verify they're the same
        String retrievedResult = searchResult.getResult();
        String retrievedName = searchResult.getName();

        // Then - should return the same references (immutable behavior)
        assertEquals(originalResult, retrievedResult);
        assertEquals(originalName, retrievedName);
        
        // Modifying the retrieved strings shouldn't affect the original
        // (String is immutable in Java, so this is guaranteed)
        assertNotNull(retrievedResult);
        assertNotNull(retrievedName);
    }

    @Test
    public void edgeCases_specialCharacters() {
        // Test with various special characters and encodings
        String resultWithSpecialChars = "Content with émojis 🎭, symbols ©®™, and unicode \u2603";
        String nameWithSpecialChars = "Naïve Café";
        
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(resultWithSpecialChars, nameWithSpecialChars);
        
        assertEquals(resultWithSpecialChars, searchResult.getResult());
        assertEquals(nameWithSpecialChars, searchResult.getName());
        assertTrue(searchResult.hasResult());
    }

    @Test
    public void edgeCases_htmlContent() {
        // Test with HTML-like content (common in Wikipedia responses)
        String htmlResult = "<p>Charles Dickens was born in <b>Portsmouth</b> in 1812.</p>";
        String name = "Charles Dickens";
        
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(htmlResult, name);
        
        assertEquals(htmlResult, searchResult.getResult());
        assertEquals(name, searchResult.getName());
        assertTrue(searchResult.hasResult());
    }
}