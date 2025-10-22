package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

/**
 * Fragment for displaying Street View panoramas for plaque locations.
 */
public class PanoramaFragment extends Fragment implements OnStreetViewPanoramaReadyCallback {

    private static final String TAG = "PanoramaFragment";
    private Placemark placemark;
    private StreetViewPanoramaFragment streetViewFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_panorama, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getPlacemarkFromArguments();

        if (placemark != null) {
            BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) requireActivity().getApplication();
            app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                    BluePlaquesConstants.STREETVIEW_BUTTON_PRESSED_EVENT,
                    placemark.getTrimmedName());

            setupStreetView();
        } else {
            Log.w(TAG, "No placemark found in arguments");
        }
    }

    private void getPlacemarkFromArguments() {
        if (getArguments() != null) {
            placemark = getArguments().getParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY);
        }
    }

    private void setupStreetView() {
        if (placemark == null) {
            Log.w(TAG, "Cannot setup StreetView: placemark is null");
            return;
        }

        try {
            streetViewFragment = (StreetViewPanoramaFragment) requireActivity().getFragmentManager()
                    .findFragmentById(R.id.street_view_panorama);
            
            if (streetViewFragment != null) {
                streetViewFragment.getStreetViewPanoramaAsync(this);
            } else {
                Log.e(TAG, "StreetViewPanoramaFragment not found in layout");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error setting up StreetView", e);
        }
    }

    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        if (placemark != null) {
            LatLng position = new LatLng(placemark.getLatitude(), placemark.getLongitude());
            streetViewPanorama.setPosition(position);
            Log.d(TAG, "StreetView positioned at: " + position.latitude + ", " + position.longitude);
        } else {
            Log.w(TAG, "Cannot position StreetView: placemark is null");
        }
    }

    @Override
    public void onDestroyView() {
        streetViewFragment = null;
        super.onDestroyView();
    }
}
