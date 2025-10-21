package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.WikipediaRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModelSearchResult;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * ViewModel for Wikipedia search functionality.
 * Manages Wikipedia API interactions.
 */
@HiltViewModel
public class WikipediaViewModel extends ViewModel {

    private final WikipediaRepository wikipediaRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<WikipediaModelSearchResult> searchResultsLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    @Inject
    public WikipediaViewModel(@NonNull WikipediaRepository wikipediaRepository) {
        this.wikipediaRepository = wikipediaRepository;
    }

    /**
     * Get Wikipedia search results.
     */
    public LiveData<WikipediaModelSearchResult> getSearchResults() {
        return searchResultsLiveData;
    }

    /**
     * Get loading state.
     */
    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    /**
     * Get error messages.
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Search Wikipedia for articles.
     */
    public void searchWikipedia(@NonNull String query) {
        if (query.trim().isEmpty()) {
            errorLiveData.setValue("Search query cannot be empty");
            return;
        }

        loadingLiveData.setValue(true);

        Disposable disposable = wikipediaRepository.searchWikipedia(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        results -> {
                            searchResultsLiveData.setValue(results);
                            loadingLiveData.setValue(false);
                        },
                        error -> {
                            errorLiveData.setValue("Wikipedia search error: " + error.getMessage());
                            loadingLiveData.setValue(false);
                        }
                );

        disposables.add(disposable);
    }

    /**
     * Clear search results.
     */
    public void clearResults() {
        searchResultsLiveData.setValue(null);
        errorLiveData.setValue(null);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
