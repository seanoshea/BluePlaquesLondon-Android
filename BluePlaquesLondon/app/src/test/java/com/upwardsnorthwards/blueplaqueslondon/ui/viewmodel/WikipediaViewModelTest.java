package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.WikipediaRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModelSearchResult;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for WikipediaViewModel.
 */
@RunWith(MockitoJUnitRunner.class)
public class WikipediaViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private WikipediaRepository wikipediaRepository;

    @Mock
    private Observer<WikipediaModelSearchResult> searchResultsObserver;

    @Mock
    private Observer<Boolean> loadingObserver;

    @Mock
    private Observer<String> errorObserver;

    private WikipediaViewModel viewModel;

    @Before
    public void setUp() {
        // Set RxJava to use trampolineScheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        viewModel = new WikipediaViewModel(wikipediaRepository);
    }

    @org.junit.After
    public void tearDown() {
        // Reset RxJava plugins after tests
        RxJavaPlugins.reset();
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.reset();
    }

    @Test
    public void searchWikipedia_success() {
        // Given
        String query = "Winston Churchill";
        WikipediaModelSearchResult results = new WikipediaModelSearchResult("test result", "Churchill");

        when(wikipediaRepository.searchWikipedia(query))
                .thenReturn(Single.just(results));

        viewModel.getSearchResults().observeForever(searchResultsObserver);
        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.searchWikipedia(query);

        // Then
        verify(wikipediaRepository).searchWikipedia(query);
        verify(searchResultsObserver).onChanged(results);
        verify(loadingObserver).onChanged(true);
        verify(loadingObserver, atLeastOnce()).onChanged(false);
    }

    @Test
    public void searchWikipedia_emptyQuery() {
        // Given
        String emptyQuery = "   ";

        viewModel.getError().observeForever(errorObserver);

        // When
        viewModel.searchWikipedia(emptyQuery);

        // Then - should not call repository for empty query
        verify(wikipediaRepository, never()).searchWikipedia(emptyQuery);
        verify(errorObserver).onChanged("Search query cannot be empty");
    }

    @Test
    public void searchWikipedia_networkError() {
        // Given
        String query = "test";
        String errorMessage = "Network timeout";

        when(wikipediaRepository.searchWikipedia(query))
                .thenReturn(Single.error(new Exception(errorMessage)));

        viewModel.getError().observeForever(errorObserver);
        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.searchWikipedia(query);

        // Then
        verify(errorObserver).onChanged("Wikipedia search error: " + errorMessage);
        verify(loadingObserver).onChanged(true);
        verify(loadingObserver, atLeastOnce()).onChanged(false);
    }

    @Test
    public void getSearchResults_observable() {
        // Given
        WikipediaModelSearchResult results = new WikipediaModelSearchResult("result", "name");
        viewModel.getSearchResults().observeForever(searchResultsObserver);

        // When
        viewModel.getSearchResults().observeForever(searchResultsObserver);

        // Then
        assertNotNull(viewModel.getSearchResults());
    }

    @Test
    public void loadingState_search() {
        // Given
        String query = "test query";
        WikipediaModelSearchResult results = new WikipediaModelSearchResult("result", "query");

        when(wikipediaRepository.searchWikipedia(query))
                .thenReturn(Single.just(results));

        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.searchWikipedia(query);

        // Then - verify loading state transitions
        verify(loadingObserver).onChanged(true);  // Loading starts
        verify(loadingObserver, atLeastOnce()).onChanged(false); // Loading ends
    }

    @Test
    public void clearResults_setsNullValues() {
        // Given
        viewModel.getSearchResults().observeForever(searchResultsObserver);
        viewModel.getError().observeForever(errorObserver);

        // When
        viewModel.clearResults();

        // Then
        verify(searchResultsObserver).onChanged(null);
        verify(errorObserver).onChanged(null);
        assertNull(viewModel.getSearchResults().getValue());
        assertNull(viewModel.getError().getValue());
    }
}
