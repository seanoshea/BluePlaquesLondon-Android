package com.upwardsnorthwards.blueplaqueslondon.data.preferences;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.datastore.preferences.core.MutablePreferences;
import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.core.PreferencesKeys;
import androidx.datastore.preferences.rxjava3.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava3.RxDataStore;

import com.google.android.gms.maps.model.LatLng;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * DataStore-based preferences manager for Blue Plaques London app.
 * Replaces the old SharedPreferences implementation with modern DataStore.
 */
@Singleton
public class AppPreferencesDataStore {

    private static final String DATASTORE_NAME = "blue_plaques_preferences";

    // Default values
    private static final float MAP_ZOOM_DEFAULT = 15.0f;
    private static final boolean ANALYTICS_ENABLED_DEFAULT = true;

    // Preference keys
    private static final Preferences.Key<Float> LAST_KNOWN_BPL_COORDINATE_LATITUDE =
            PreferencesKeys.floatKey("last_known_bpl_coordinate_latitude");
    private static final Preferences.Key<Float> LAST_KNOWN_BPL_COORDINATE_LONGITUDE =
            PreferencesKeys.floatKey("last_known_bpl_coordinate_longitude");
    private static final Preferences.Key<Float> LAST_KNOWN_COORDINATE_LATITUDE =
            PreferencesKeys.floatKey("last_known_coordinate_latitude");
    private static final Preferences.Key<Float> LAST_KNOWN_COORDINATE_LONGITUDE =
            PreferencesKeys.floatKey("last_known_coordinate_longitude");
    private static final Preferences.Key<Float> MAP_ZOOM =
            PreferencesKeys.floatKey("map_zoom");
    private static final Preferences.Key<Boolean> ANALYTICS_ENABLED =
            PreferencesKeys.booleanKey("analytics_enabled");
    private static final Preferences.Key<Integer> LAUNCH_COUNT =
            PreferencesKeys.intKey("launch_count");
    private static final Preferences.Key<Boolean> COMPLETED_REVIEW =
            PreferencesKeys.booleanKey("completed_review");

    private final RxDataStore<Preferences> dataStore;

    @Inject
    public AppPreferencesDataStore(@NonNull @ApplicationContext Context context) {
        this.dataStore = new RxPreferenceDataStoreBuilder(context, DATASTORE_NAME).build();
    }

    /**
     * Get the last known Blue Plaque London coordinate as a Flowable.
     */
    public Flowable<LatLng> getLastKnownBPLCoordinate() {
        return dataStore.data().map(prefs -> {
            Float latitudeObj = prefs.get(LAST_KNOWN_BPL_COORDINATE_LATITUDE);
            float latitude = (latitudeObj != null) ? latitudeObj : (float) BluePlaquesConstants.DEFAULT_LATITUDE;
            Float longitudeObj = prefs.get(LAST_KNOWN_BPL_COORDINATE_LONGITUDE);
            float longitude = (longitudeObj != null) ? longitudeObj : (float) BluePlaquesConstants.DEFAULT_LONGITUDE;
            return new LatLng(latitude, longitude);
        });
    }

    /**
     * Get the last known Blue Plaque London coordinate synchronously.
     */
    public Single<LatLng> getLastKnownBPLCoordinateSingle() {
        return dataStore.data().firstOrError().map(prefs -> {
            Float latitude = prefs.get(LAST_KNOWN_BPL_COORDINATE_LATITUDE);
            if (latitude == null) {
                latitude = (float) BluePlaquesConstants.DEFAULT_LATITUDE;
            }
            Float longitude = prefs.get(LAST_KNOWN_BPL_COORDINATE_LONGITUDE);
            if (longitude == null) {
                longitude = (float) BluePlaquesConstants.DEFAULT_LONGITUDE;
            }
            return new LatLng(latitude, longitude);
        });
    }

    /**
     * Save the last known Blue Plaque London coordinate.
     */
    public Single<Preferences> saveLastKnownBPLCoordinate(@NonNull LatLng latLng) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            mutablePrefs.set(LAST_KNOWN_BPL_COORDINATE_LATITUDE, (float) latLng.latitude);
            mutablePrefs.set(LAST_KNOWN_BPL_COORDINATE_LONGITUDE, (float) latLng.longitude);
            return Single.just(mutablePrefs);
        });
    }

    /**
     * Save the last known coordinate (user's location).
     */
    public Single<Preferences> saveLastKnownCoordinate(@NonNull LatLng latLng) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            mutablePrefs.set(LAST_KNOWN_COORDINATE_LATITUDE, (float) latLng.latitude);
            mutablePrefs.set(LAST_KNOWN_COORDINATE_LONGITUDE, (float) latLng.longitude);
            return Single.just(mutablePrefs);
        });
    }

    /**
     * Get map zoom level as a Flowable.
     */
    public Flowable<Float> getMapZoom() {
        return dataStore.data().map(prefs -> {
            Float zoom = prefs.get(MAP_ZOOM);
            return zoom != null ? zoom : MAP_ZOOM_DEFAULT;
        });
    }

    /**
     * Get map zoom level synchronously.
     */
    public Single<Float> getMapZoomSingle() {
        return dataStore.data().firstOrError().map(prefs -> {
            Float zoom = prefs.get(MAP_ZOOM);
            return zoom != null ? zoom : MAP_ZOOM_DEFAULT;
        });
    }

    /**
     * Save map zoom level.
     */
    public Single<Preferences> saveMapZoom(float zoom, float minZoom, float maxZoom) {
        if (zoom <= maxZoom && zoom >= minZoom) {
            return dataStore.updateDataAsync(prefs -> {
                MutablePreferences mutablePrefs = prefs.toMutablePreferences();
                mutablePrefs.set(MAP_ZOOM, zoom);
                return Single.just(mutablePrefs);
            });
        }
        return dataStore.data().firstOrError();
    }

    /**
     * Get analytics enabled state as a Flowable.
     */
    public Flowable<Boolean> getAnalyticsEnabled() {
        return dataStore.data().map(prefs -> {
            Boolean enabled = prefs.get(ANALYTICS_ENABLED);
            return enabled != null ? enabled : ANALYTICS_ENABLED_DEFAULT;
        });
    }

    /**
     * Get analytics enabled state synchronously.
     */
    public Single<Boolean> getAnalyticsEnabledSingle() {
        return dataStore.data().firstOrError().map(prefs -> {
            Boolean enabled = prefs.get(ANALYTICS_ENABLED);
            return enabled != null ? enabled : ANALYTICS_ENABLED_DEFAULT;
        });
    }

    /**
     * Save analytics enabled state.
     */
    public Single<Preferences> saveAnalyticsEnabled(boolean enabled) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            mutablePrefs.set(ANALYTICS_ENABLED, enabled);
            return Single.just(mutablePrefs);
        });
    }

    /**
     * Get launch count as a Flowable.
     */
    public Flowable<Integer> getLaunchCount() {
        return dataStore.data().map(prefs -> {
            Integer count = prefs.get(LAUNCH_COUNT);
            return count != null ? count : 0;
        });
    }

    /**
     * Get launch count synchronously.
     */
    public Single<Integer> getLaunchCountSingle() {
        return dataStore.data().firstOrError().map(prefs -> {
            Integer count = prefs.get(LAUNCH_COUNT);
            return count != null ? count : 0;
        });
    }

    /**
     * Increment launch count.
     */
    public Single<Preferences> incrementLaunchCount() {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            Integer currentCount = prefs.get(LAUNCH_COUNT);
            int newCount = (currentCount != null ? currentCount : 0) + 1;
            mutablePrefs.set(LAUNCH_COUNT, newCount);
            return Single.just(mutablePrefs);
        });
    }

    /**
     * Check if user has completed review as a Flowable.
     */
    public Flowable<Boolean> hasCompletedReview() {
        return dataStore.data().map(prefs -> {
            Boolean completed = prefs.get(COMPLETED_REVIEW);
            return completed != null ? completed : false;
        });
    }

    /**
     * Check if user has completed review synchronously.
     */
    public Single<Boolean> hasCompletedReviewSingle() {
        return dataStore.data().firstOrError().map(prefs -> {
            Boolean completed = prefs.get(COMPLETED_REVIEW);
            return completed != null ? completed : false;
        });
    }

    /**
     * Set review completed state.
     */
    public Single<Preferences> setCompletedReview(boolean completed) {
        return dataStore.updateDataAsync(prefs -> {
            MutablePreferences mutablePrefs = prefs.toMutablePreferences();
            mutablePrefs.set(COMPLETED_REVIEW, completed);
            return Single.just(mutablePrefs);
        });
    }
}
