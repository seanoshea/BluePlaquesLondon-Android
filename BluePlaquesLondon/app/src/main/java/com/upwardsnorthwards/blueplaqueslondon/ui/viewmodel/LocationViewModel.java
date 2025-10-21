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
 * ViewModel for location services.
 * Manages user location and finding closest plaques.
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
