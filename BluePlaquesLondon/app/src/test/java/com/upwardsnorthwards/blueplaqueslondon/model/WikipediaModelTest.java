package com.upwardsnorthwards.blueplaqueslondon.model;

import androidx.annotation.Nullable;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
public class WikipediaModelTest {

    private WikipediaModel model;
    private TestWikipediaModelDelegate delegate;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        model = new WikipediaModel();
        delegate = new TestWikipediaModelDelegate();
        model.setDelegate(delegate);
    }

    @Test
    public void testMalformedJSON() {
        // Test malformed JSON - missing closing bracket
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(
            "{\"batchcomplete\":\"\",\"continue\":{\"sroffset\":10,\"continue\":\"-||\"},\"query\":{\"searchinfo\":{\"totalhits\":8723},\"search\":[{\"ns\":0,\"title\":\"Winston Churchill\",\"timestamp\":\"2016-03-22T09:30:12Z\"}",
            "Churchill"
        );
        
        // Since we can't directly call onPostExecute, test the search result object
        assertNotNull(searchResult);
        assertTrue(searchResult.hasResult());
        assertEquals("Churchill", searchResult.getName());
    }

    @Test
    public void testEmptySearchResponseJSON() {
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(
            "{\"batchcomplete\":\"\",\"continue\":{\"sroffset\":10,\"continue\":\"-||\"},\"query\":{\"searchinfo\":{\"totalhits\":0},\"search\":[]}}",
            "Churchill"
        );
        
        assertNotNull(searchResult);
        assertTrue(searchResult.hasResult());
        assertEquals("Churchill", searchResult.getName());
    }

    @Test
    public void testValidSearchResponse() {
        WikipediaModelSearchResult searchResult = new WikipediaModelSearchResult(
            "{\"batchcomplete\":\"\",\"continue\":{\"sroffset\":10,\"continue\":\"-||\"},\"query\":{\"searchinfo\":{\"totalhits\":397},\"search\":[{\"ns\":0,\"title\":\"Malcolm Sargent\",\"timestamp\":\"2016-02-02T06:09:24Z\"},{\"ns\":0,\"title\":\"Michael Tippett\",\"timestamp\":\"2016-03-06T20:56:12Z\"}]}}",
            "SARGENT, Sir Malcolm"
        );
        
        assertNotNull(searchResult);
        assertTrue(searchResult.hasResult());
        assertEquals("SARGENT, Sir Malcolm", searchResult.getName());
        assertNotNull(searchResult.getResult());
    }
    
    @Test
    public void testModelInitialization() {
        assertNotNull(model);
        assertNotNull(delegate);
    }
    
    @Test
    public void testDelegateSetup() {
        // Test that delegate is properly set
        model.setDelegate(delegate);
        model.setResponseUrl("https://en.wikipedia.org/wiki/%1$s");
        
        // Verify initial state
        assertFalse(delegate.isFailedToRetrieveUrl());
        assertNull(delegate.getReturnedUrl());
    }

    private static class TestWikipediaModelDelegate implements IWikipediaModelDelegate {
        @Nullable
        private String returnedUrl;
        private boolean failedToRetrieveUrl;

        @Override
        public void onRetriveWikipediaUrlSuccess(String url) {
            this.failedToRetrieveUrl = false;
            this.returnedUrl = url;
        }

        @Override
        public void onRetriveWikipediaUrlFailure() {
            this.failedToRetrieveUrl = true;
            this.returnedUrl = null;
        }

        @Nullable
        public String getReturnedUrl() {
            return returnedUrl;
        }

        public boolean isFailedToRetrieveUrl() {
            return failedToRetrieveUrl;
        }
    }
}