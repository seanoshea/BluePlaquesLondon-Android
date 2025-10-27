package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.model.LatLng;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Location services ViewModel managing GPS functionality and proximity-based features.
 * 
 * <p>This ViewModel handles all location-related operations including GPS access,
 * permission management, and proximity calculations for finding the closest blue plaques
 * to the user's current location.</p>
 * 
 * <h3>Key Responsibilities:</h3>
 * <ul>
 *   <li><strong>Location Services:</strong> GPS location retrieval via FusedLocationProviderClient</li>
 *   <li><strong>Permission Management:</strong> Tracks and manages location permission state</li>
 *   <li><strong>Proximity Calculations:</strong> Finds closest plaques using distance algorithms</li>
 *   <li><strong>State Management:</strong> Loading states, errors, and location data</li>
 *   <li><strong>Reactive Updates:</strong> LiveData streams for UI observation</li>
 * </ul>
 * 
 * <h3>Location Services Integration:</h3>
 * <p>Uses Google Play Services for accurate location detection:</p>
 * <ul>
 *   <li><strong>FusedLocationProviderClient:</strong> High-accuracy location requests</li>
 *   <li><strong>Priority.PRIORITY_HIGH_ACCURACY:</strong> GPS-level precision</li>
 *   <li><strong>Permission Handling:</strong> Runtime permission state management</li>
 *   <li><strong>Error Handling:</strong> Comprehensive error scenarios and user feedback</li>
 * </ul>
 * 
 * <h3>Proximity Algorithm:</h3>
 * <p>Implements efficient closest plaque detection:</p>
 * <ol>
 *   <li><strong>Distance Calculation:</strong> Uses {@link Location#distanceBetween} for accuracy</li>
 *   <li><strong>Comparison:</strong> Iterates through all plaques to find minimum distance</li>
 *   <li><strong>Background Processing:</strong> Computation performed on background thread</li>
 *   <li><strong>Result Delivery:</strong> Updates UI thread via LiveData</li>
 * </ol>
 * 
 * <h3>LiveData Streams:</h3>
 * <ul>
 *   <li><strong>Current Location:</strong> {@link #getCurrentLocation()} - User's GPS coordinates</li>
 *   <li><strong>Closest Plaque:</strong> {@link #getClosestPlaque()} - Nearest blue plaque</li>
 *   <li><strong>Loading State:</strong> {@link #getLoading()} - Location operation progress</li>
 *   <li><strong>Permission State:</strong> {@link #getLocationPermissionGranted()} - Permission status</li>
 *   <li><strong>Error Messages:</strong> {@link #getError()} - Error information and user guidance</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Observe location updates
 * locationViewModel.getCurrentLocation().observe(this, location -> {
 *     if (location != null) {
 *         updateMapPosition(location);
 *     }
 * });
 * 
 * // Find closest plaque
 * locationViewModel.getClosestPlaque().observe(this, closestPlaque -> {
 *     if (closestPlaque != null) {
 *         highlightPlaque(closestPlaque);
 *     }
 * });
 * 
 * // Request location and find closest plaque
 * locationViewModel.setLocationPermissionGranted(true);
 * locationViewModel.requestCurrentLocation();
 * locationViewModel.findClosestPlaque(allPlaques);
 * }</pre>
 * 
 * @see FusedLocationProviderClient
 * @see com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity
 * @see Placemark
 * 
 * @author Blue Plaques London Team
 * @since 3.0
 */
@HiltViewModel
public class LocationViewModel extends ViewModel {

    private final FusedLocationProviderClient locationClient;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Location> currentLocationLiveData = new MutableLiveData<>();
    private final MutableLiveData<Placemark> closestPlaqueLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> locationPermissionGrantedLiveData = new MutableLiveData<>(false);

    @Inject
    public LocationViewModel(@NonNull FusedLocationProviderClient locationClient) {
        this.locationClient = locationClient;
    }

    /**
     * Get current location.
     */
    public LiveData<Location> getCurrentLocation() {
        return currentLocationLiveData;
    }

    /**
     * Get closest plaque.
     */
    public LiveData<Placemark> getClosestPlaque() {
        return closestPlaqueLiveData;
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
     * Get location permission granted state.
     */
    public LiveData<Boolean> getLocationPermissionGranted() {
        return locationPermissionGrantedLiveData;
    }

    /**
     * Update location permission state.
     */
    public void setLocationPermissionGranted(boolean granted) {
        locationPermissionGrantedLiveData.setValue(granted);
    }

    /**
     * Request current location.
     * Requires location permission to be granted first.
     */
    public void requestCurrentLocation() {
        if (locationPermissionGrantedLiveData.getValue() == null ||
            !locationPermissionGrantedLiveData.getValue()) {
            errorLiveData.setValue("Location permission not granted");
            return;
        }

        loadingLiveData.setValue(true);

        try {
            locationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            currentLocationLiveData.setValue(location);
                        } else {
                            errorLiveData.setValue("Unable to get current location");
                        }
                        loadingLiveData.setValue(false);
                    })
                    .addOnFailureListener(e -> {
                        errorLiveData.setValue("Location error: " + e.getMessage());
                        loadingLiveData.setValue(false);
                    });
        } catch (SecurityException e) {
            errorLiveData.setValue("Location permission denied");
            loadingLiveData.setValue(false);
        }
    }

    /**
     * Find the closest plaque to the current location.
     */
    public void findClosestPlaque(@NonNull List<Placemark> placemarks) {
        Location currentLocation = currentLocationLiveData.getValue();
        if (currentLocation == null) {
            errorLiveData.setValue("Current location not available. Please enable location services.");
            return;
        }

        if (placemarks == null || placemarks.isEmpty()) {
            errorLiveData.setValue("No plaques available");
            return;
        }

        loadingLiveData.setValue(true);

        Disposable disposable = Single.fromCallable(() -> {
            Placemark closestPlaque = null;
            float shortestDistance = Float.MAX_VALUE;

            LatLng currentLatLng = new LatLng(currentLocation.getLatitude(),
                                               currentLocation.getLongitude());

            for (Placemark placemark : placemarks) {
                LatLng plaqueLatLng = new LatLng(placemark.getLatitude(),
                                                  placemark.getLongitude());
                float distance = calculateDistance(currentLatLng, plaqueLatLng);

                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    closestPlaque = placemark;
                }
            }

            return closestPlaque;
        })
        .subscribeOn(Schedulers.computation())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                closestPlaque -> {
                    if (closestPlaque != null) {
                        closestPlaqueLiveData.setValue(closestPlaque);
                    } else {
                        errorLiveData.setValue("Could not find closest plaque");
                    }
                    loadingLiveData.setValue(false);
                },
                error -> {
                    errorLiveData.setValue("Error finding closest plaque: " + error.getMessage());
                    loadingLiveData.setValue(false);
                }
        );

        disposables.add(disposable);
    }

    /**
     * Calculate distance between two coordinates in meters.
     */
    private float calculateDistance(@NonNull LatLng point1, @NonNull LatLng point2) {
        float[] results = new float[1];
        Location.distanceBetween(
                point1.latitude, point1.longitude,
                point2.latitude, point2.longitude,
                results
        );
        return results[0];
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
