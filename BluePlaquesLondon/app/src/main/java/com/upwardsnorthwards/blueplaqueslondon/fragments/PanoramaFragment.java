package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

/**
 * Fragment for displaying Street View panoramas for plaque locations.
 * Note: In a real implementation, this would use the Google Maps Street View API.
 */
public class PanoramaFragment extends Fragment {

    private Placemark placemark;

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

            setupStreetView(view);
        }
    }

    private void getPlacemarkFromArguments() {
        if (getArguments() != null) {
            placemark = getArguments().getParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY);
        }
    }

    private void setupStreetView(View view) {
        if (placemark == null) {
            return;
        }
        // The actual StreetViewPanoramaFragment would be loaded here
        // For now, the layout provides the container for Street View display
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
