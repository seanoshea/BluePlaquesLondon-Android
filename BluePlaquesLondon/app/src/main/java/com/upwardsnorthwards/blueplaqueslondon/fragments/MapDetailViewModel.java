package com.upwardsnorthwards.blueplaqueslondon.fragments;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.List;

public class MapDetailViewModel extends ViewModel {

    private final MutableLiveData<Placemark> currentPlacemark = new MutableLiveData<>();
    private List<Placemark> allPlacemarks;

    public LiveData<Placemark> getCurrentPlacemark() {
        return currentPlacemark;
    }

    public void setPlacemarks(List<Placemark> placemarks) {
        this.allPlacemarks = placemarks;
        if (placemarks != null && !placemarks.isEmpty()) {
            currentPlacemark.setValue(placemarks.get(0));
        }
    }

    public void switchToPlacemark(Placemark placemark) {
        allPlacemarks.remove(placemark);
        allPlacemarks.add(0, placemark);
        currentPlacemark.setValue(placemark);
    }

    public List<Placemark> getAllPlacemarks() {
        return allPlacemarks;
    }
}
