package com.upwardsnorthwards.blueplaqueslondon.data.repository;

import androidx.annotation.NonNull;

import com.upwardsnorthwards.blueplaqueslondon.data.remote.WikipediaApiService;
import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModelSearchResult;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Repository for Wikipedia API operations.
 */
@Singleton
public class WikipediaRepository {

    private final WikipediaApiService apiService;

    @Inject
    public WikipediaRepository(@NonNull WikipediaApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Search Wikipedia for articles.
     */
    public Single<WikipediaModelSearchResult> searchWikipedia(@NonNull String query) {
        return apiService.searchWikipedia("opensearch", query, 10, 0, "json")
                .subscribeOn(Schedulers.io());
    }
}
