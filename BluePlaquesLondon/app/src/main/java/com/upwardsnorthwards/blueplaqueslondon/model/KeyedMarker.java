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

import com.google.android.gms.maps.model.Marker;

/**
 * Associates Google Maps markers with unique identifiers for efficient lookup.
 * 
 * <p>This utility class maintains a bidirectional relationship between Google Maps
 * {@link Marker} objects and string-based keys, enabling fast marker retrieval
 * during search operations and user interactions.</p>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * // Creating a keyed marker
 * KeyedMarker keyedMarker = new KeyedMarker();
 * keyedMarker.setKey(placemark.key()); // Use placemark's coordinate-based key
 * keyedMarker.setMarker(googleMap.addMarker(markerOptions));
 * 
 * // Later lookup during search
 * String searchKey = Placemark.keyFromLatLng(lat, lng);
 * if (keyedMarker.getKey().equals(searchKey)) {
 *     Marker foundMarker = keyedMarker.getMarker();
 *     foundMarker.showInfoWindow();
 * }
 * }</pre>
 * 
 * <p><strong>Architecture Integration:</strong></p>
 * <ul>
 *   <li>Used by {@link com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment}</li>
 *   <li>Enables efficient marker management in large datasets (900+ markers)</li>
 *   <li>Supports search functionality and marker highlighting</li>
 * </ul>
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 * @see com.google.android.gms.maps.model.Marker
 * @see Placemark#key()
 * @see com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment
 */
public class KeyedMarker {

    private String key;
    private Marker marker;

    /**
     * Returns the unique identifier associated with this marker.
     * 
     * @return the string key, may be null if not set
     * @see Placemark#key() Typical source of keys
     */
    public String getKey() {
        return key;
    }

    /**
     * Sets the unique identifier for this marker.
     * 
     * @param key the string key to associate with the marker, may be null
     */
    public void setKey(final String key) {
        this.key = key;
    }

    /**
     * Returns the Google Maps marker associated with this key.
     * 
     * @return the Google Maps marker, may be null if not set
     */
    public Marker getMarker() {
        return marker;
    }

    /**
     * Sets the Google Maps marker to associate with the key.
     * 
     * @param marker the Google Maps marker, may be null
     */
    public void setMarker(final Marker marker) {
        this.marker = marker;
    }

}
