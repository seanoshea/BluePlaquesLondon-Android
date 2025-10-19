package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

public class PanoramaFragment extends Fragment implements OnStreetViewPanoramaReadyCallback {

    private Placemark placemark;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            placemark = getArguments().getParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_panorama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportStreetViewPanoramaFragment streetViewPanoramaFragment = (SupportStreetViewPanoramaFragment) getChildFragmentManager().findFragmentById(R.id.street_view_panorama);
        if (streetViewPanoramaFragment != null) {
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        }
    }

    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        if (placemark != null) {
            streetViewPanorama.setPosition(new LatLng(placemark.getLatitude(), placemark.getLongitude()));
        }
    }
}
