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

package com.upwardsnorthwards.blueplaqueslondon.utils;

/**
 * Application-wide constants for Blue Plaques London Android.
 * 
 * <p>This class centralizes all constant values used throughout the application,
 * including navigation keys, default coordinates, and analytics event names.
 * All constants are public static final for compile-time optimization.</p>
 * 
 * <p><strong>Constant Categories:</strong></p>
 * <ul>
 *   <li><strong>Navigation Keys:</strong> Parcelable keys for data transfer between components</li>
 *   <li><strong>Default Location:</strong> Fallback coordinates for London center</li>
 *   <li><strong>Analytics Events:</strong> Firebase Analytics event and category names</li>
 * </ul>
 * 
 * <p><strong>Usage Example:</strong></p>
 * <pre>{@code
 * // Navigation with parcelable data
 * Bundle args = new Bundle();
 * args.putParcelable(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY, placemark);
 * 
 * // Default map location
 * LatLng defaultLocation = new LatLng(
 *     BluePlaquesConstants.DEFAULT_LATITUDE,
 *     BluePlaquesConstants.DEFAULT_LONGITUDE
 * );
 * 
 * // Analytics tracking
 * firebaseAnalytics.logEvent(
 *     BluePlaquesConstants.MARKER_PRESSED_EVENT,
 *     bundle
 * );
 * }</pre>
 * 
 * <p><strong>Architecture Integration:</strong></p>
 * <ul>
 *   <li>Used across all Activities and Fragments for consistent data keys</li>
 *   <li>Integrated with Firebase Analytics for event tracking</li>
 *   <li>Provides fallback coordinates for Google Maps initialization</li>
 * </ul>
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 * @see android.os.Bundle
 * @see com.google.firebase.analytics.FirebaseAnalytics
 */
public class BluePlaquesConstants {

    // Navigation and Data Transfer Keys
    /** Bundle key for passing Placemark data when info window is clicked */
    public static final String INFO_WINDOW_CLICKED_PARCLEABLE_KEY = "com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY";
    
    /** Bundle key for passing Placemark data to Wikipedia fragment */
    public static final String WIKIPEDIA_CLICKED_PARCLEABLE_KEY = "com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY";
    
    /** Bundle key for passing Placemark data to Street View panorama */
    public static final String PANORAMA_CLICKED_PARCLEABLE_KEY = "com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY";

    // Default Map Location (Central London)
    /** Default latitude for map center - approximately Trafalgar Square area */
    public static final double DEFAULT_LATITUDE = 51.50016999993306f;
    
    /** Default longitude for map center - approximately Trafalgar Square area */
    public static final double DEFAULT_LONGITUDE = -0.1814680000049975f;

    // Firebase Analytics Events and Categories
    /** Analytics event fired when application completes loading */
    public static final String APPLICATION_LOADED = "ApplicationLoaded";
    
    /** Analytics category for user interface actions */
    public static final String UI_ACTION_CATEGORY = "BPLUIActionCategory";
    
    /** Analytics category for error events */
    public static final String ERROR_CATEGORY = "BPLErrorCategory";
    
    // User Interaction Events
    /** Analytics event for details button press */
    public static final String DETAILS_BUTTON_PRESSED_EVENT = "BPLDetailsButtonPressedEvent";
    
    /** Analytics event for Wikipedia button press */
    public static final String WIKIPEDIA_BUTTON_PRESSED_EVENT = "BPLWikipediaButtonPressedEvent";
    
    /** Analytics event for Street View button press */
    public static final String STREETVIEW_BUTTON_PRESSED_EVENT = "BPLStreetViewButtonPressedEvent";
    
    /** Analytics event for table row selection */
    public static final String TABLE_ROW_PRESSED_EVENT = "BPLTableRowPressedEvent";
    
    /** Analytics event for map marker tap */
    public static final String MARKER_PRESSED_EVENT = "BPLMarkerPressedEvent";
    
    /** Analytics event for marker info window tap */
    public static final String MARKER_INFO_WINDOW_PRESSED_EVENT = "BPLMarkerInfoWindowPressedEvent";
    
    // Error Events
    /** Analytics event for Wikipedia page load failures */
    public static final String WIKIPEDIA_PAGE_LOAD_ERROR_EVENT = "BPLWikipediaPageLoadErrorEvent";
    
    // App Rating Events
    /** Analytics event for positive app rating response */
    public static final String RATE_APP_BUTTON_PRESSED_EVENT = "BPLRateAppButtonPressedEvent";
    
    /** Analytics event for declining app rating */
    public static final String DECLINE_RATE_APP_BUTTON_PRESSED_EVENT = "BPLDeclineRateAppButtonPressedEvent";
    
    /** Analytics event for postponing app rating */
    public static final String REMIND_RATE_APP_BUTTON_PRESSED_EVENT = "BPLRemindRateAppButtonPressedEvent";
    
    /** Analytics event for opening Play Store from rating prompt */
    public static final String RATE_APP_STORE_OPENED_EVENT = "BPLRateAppStoreOpenedEvent";
    
    // Google Play Services Events
    /** Analytics event for Google Play Services availability prompt */
    public static final String GOOGLE_PLAY_SERVICES_PROMPT = "BPLGooglePlayServicesPromptEvent";
    
    /** Analytics event for recoverable Google Play Services issues */
    public static final String GOOGLE_PLAY_SERVICES_PROMPT_RECOVERABLE = "BPLGooglePlayServicesPromptRecoverable";
    
    /** Analytics event for unrecoverable Google Play Services issues */
    public static final String GOOGLE_PLAY_SERVICES_PROMPT_UNRECOVERABLE = "BPLGooglePlayServicesPromptUnrecoverable";
}
