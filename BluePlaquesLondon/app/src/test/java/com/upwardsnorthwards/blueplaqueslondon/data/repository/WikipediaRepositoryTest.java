package com.upwardsnorthwards.blueplaqueslondon.data.repository;

import com.upwardsnorthwards.blueplaqueslondon.data.remote.WikipediaApiService;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModelSearchResult;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for WikipediaRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class WikipediaRepositoryTest {

    @Mock
    private WikipediaApiService apiService;

    private WikipediaRepository repository;

    @Before
    public void setUp() {
        // Set RxJava to use trampolineScheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        repository = new WikipediaRepository(apiService);
    }

    @org.junit.After
    public void tearDown() {
        // Reset RxJava plugins after tests
        RxJavaPlugins.reset();
    }

    @Test
    public void searchWikipedia_apiCall() {
        // Given
        String query = "Winston Churchill";
        WikipediaModelSearchResult expectedResult = new WikipediaModelSearchResult("result", "Churchill");

        when(apiService.searchWikipedia("opensearch", query, 10, 0, "json"))
                .thenReturn(Single.just(expectedResult));

        // When
        Single<WikipediaModelSearchResult> result = repository.searchWikipedia(query);

        // Then
        verify(apiService).searchWikipedia("opensearch", query, 10, 0, "json");

        // Verify we can observe the result
        result.test()
                .assertComplete()
                .assertNoErrors()
                .assertValue(expectedResult);
    }

    @Test
    public void searchWikipedia_responseMapping() {
        // Given
        String query = "Charles Darwin";
        WikipediaModelSearchResult result = new WikipediaModelSearchResult("mapped result", "Darwin");

        when(apiService.searchWikipedia(anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Single.just(result));

        // When
        Single<WikipediaModelSearchResult> searchResult = repository.searchWikipedia(query);

        // Then
        searchResult.test()
                .assertComplete()
                .assertValue(result);

        assertNotNull(searchResult.blockingGet());
    }

    @Test
    public void searchWikipedia_apiError() {
        // Given
        String query = "test";
        Throwable error = new RuntimeException("API Error");

        when(apiService.searchWikipedia(anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Single.error(error));

        // When
        Single<WikipediaModelSearchResult> result = repository.searchWikipedia(query);

        // Then
        result.test()
                .assertError(error);
    }

    @Test
    public void searchWikipedia_networkTimeout() {
        // Given
        String query = "network test";
        Throwable timeoutError = new Exception("Network timeout");

        when(apiService.searchWikipedia(anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Single.error(timeoutError));

        // When
        Single<WikipediaModelSearchResult> result = repository.searchWikipedia(query);

        // Then
        result.test()
                .assertError(timeoutError);
    }

    @Test
    public void searchWikipedia_returnsNonNull() {
        // Given
        WikipediaModelSearchResult result = new WikipediaModelSearchResult("data", "name");
        when(apiService.searchWikipedia(anyString(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(Single.just(result));

        // When
        WikipediaModelSearchResult searchResult = repository.searchWikipedia("query").blockingGet();

        // Then
        assertNotNull(searchResult);
        assertNotNull(searchResult.getName());
    }
}
