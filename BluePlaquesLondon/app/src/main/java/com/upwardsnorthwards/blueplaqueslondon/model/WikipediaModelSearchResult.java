// Copyright (c) 2014 - 2016 Upwards Northwards Software Limited
// All rights reserved.
//
// Redistribution and use in source and binary forms, with or without
// modification, are permitted provided that the following conditions are met:
// 1. Redistributions of source code must retain the above copyright
// notice, this list of conditions and the following disclaimer.
// 2. Redistributions in binary form must reproduce the above copyright
// notice, this list of conditions and the following disclaimer in the
// documentation and/or other materials provided with the distribution.
// 3. All advertising materials mentioning features or use of this software
// must display the following acknowledgement:
// This product includes software developed by Upwards Northwards Software Limited.
// 4. Neither the name of Upwards Northwards Software Limited nor the
// names of its contributors may be used to endorse or promote products
// derived from this software without specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY UPWARDS NORTHWARDS SOFTWARE LIMITED ''AS IS'' AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
// DISCLAIMED. IN NO EVENT SHALL THE UPWARDS NORTHWARDS SOFTWARE LIMITED BE LIABLE FOR ANY
// DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
// (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
// LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
// ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
// (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.upwardsnorthwards.blueplaqueslondon.model;

/**
 * Data transfer object for Wikipedia search results.
 * 
 * <p>This class encapsulates the results of a Wikipedia API search operation,
 * providing both the search result content and the associated name/query that
 * was used to obtain the result.</p>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * WikipediaModelSearchResult result = new WikipediaModelSearchResult(
 *     "Charles Dickens was an English writer...", 
 *     "Charles Dickens"
 * );
 * 
 * if (result.hasResult()) {
 *     String content = result.getResult();
 *     String searchTerm = result.getName();
 *     // Display Wikipedia content in WebView
 * }
 * }</pre>
 * 
 * <p><strong>Architecture Integration:</strong></p>
 * <ul>
 *   <li>Created by {@link WikipediaModel} during search operations</li>
 *   <li>Consumed by {@link com.upwardsnorthwards.blueplaqueslondon.fragments.WikipediaFragment}</li>
 *   <li>Used to determine if valid Wikipedia content was found</li>
 * </ul>
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 * @see WikipediaModel
 * @see com.upwardsnorthwards.blueplaqueslondon.fragments.WikipediaFragment
 */
public class WikipediaModelSearchResult {

    private final String result;
    private final String name;

    /**
     * Constructs a new Wikipedia search result.
     * 
     * @param result the Wikipedia content or search result, may be null or empty
     * @param name the search term or person name used for the query, may be null
     */
    public WikipediaModelSearchResult(String result, String name) {
        this.result = result;
        this.name = name;
    }

    /**
     * Determines if this search result contains valid content.
     * 
     * <p>A result is considered valid if it is not null and contains
     * at least one character of content.</p>
     * 
     * @return true if valid Wikipedia content is available, false otherwise
     */
    public boolean hasResult() {
        return this.result != null && this.result.length() > 0;
    }

    /**
     * Returns the Wikipedia search result content.
     * 
     * @return the Wikipedia content, may be null or empty
     * @see #hasResult() Check this first to verify content availability
     */
    public String getResult() {
        return result;
    }

    /**
     * Returns the search term or name used for this query.
     * 
     * @return the original search term, may be null
     */
    public String getName() {
        return name;
    }
}
