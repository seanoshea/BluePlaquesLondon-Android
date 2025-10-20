package com.upwardsnorthwards.blueplaqueslondon.data.remote;

import com.upwardsnorthwards.blueplaqueslondon.model.WikipediaModelSearchResult;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API service for Wikipedia queries.
 */
public interface WikipediaApiService {

    /**
     * Search Wikipedia for articles.
     *
     * @param action   The API action (should be "opensearch")
     * @param search   The search query
     * @param limit    Maximum number of results
     * @param namespace Namespace to search (0 = articles)
     * @param format   Response format (json)
     * @return Single with search results
     */
    @GET("w/api.php")
    Single<WikipediaModelSearchResult> searchWikipedia(
            @Query("action") String action,
            @Query("search") String search,
            @Query("limit") int limit,
            @Query("namespace") int namespace,
            @Query("format") String format
    );
}
