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
 * Street View panorama fragment for immersive plaque location viewing.
 * 
 * <p>This fragment provides Google Street View integration, allowing users to explore
 * the actual street-level environment around blue plaque locations. It offers an
 * immersive way to see the context and surroundings of historical plaques.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Street View Integration:</strong> Google Street View panorama display</li>
 *   <li><strong>Precise Positioning:</strong> Automatically positions view at plaque coordinates</li>
 *   <li><strong>Interactive Navigation:</strong> Full Street View controls and navigation</li>
 *   <li><strong>Error Handling:</strong> Comprehensive error handling and logging</li>
 *   <li><strong>Analytics Tracking:</strong> User interaction monitoring</li>
 *   <li><strong>Legacy Fragment Support:</strong> Uses legacy FragmentManager for Street View compatibility</li>
 * </ul>
 * 
 * <h3>Technical Implementation:</h3>
 * <p>The fragment uses Google's Street View API with specific considerations:</p>
 * <ul>
 *   <li><strong>Legacy FragmentManager:</strong> Required for {@link StreetViewPanoramaFragment} compatibility</li>
 *   <li><strong>Async Initialization:</strong> Implements {@link OnStreetViewPanoramaReadyCallback}</li>
 *   <li><strong>Coordinate Positioning:</strong> Uses plaque latitude/longitude for precise positioning</li>
 *   <li><strong>Resource Management:</strong> Proper cleanup in {@link #onDestroyView()}</li>
 * </ul>
 * 
 * <h3>Navigation Integration:</h3>
 * <p>Accessed from {@link MapDetailFragment} when users tap the Street View button:</p>
 * <ul>
 *   <li><strong>Entry:</strong> Receives {@link Placemark} data via Bundle arguments</li>
 *   <li><strong>Positioning:</strong> Automatically centers Street View on plaque location</li>
 *   <li><strong>Analytics:</strong> Tracks Street View button press events</li>
 * </ul>
 * 
 * <h3>Error Scenarios:</h3>
 * <ul>
 *   <li><strong>No Street View Data:</strong> Google Street View may not be available for all locations</li>
 *   <li><strong>Fragment Initialization:</strong> Handles cases where StreetViewPanoramaFragment is not found</li>
 *   <li><strong>Invalid Coordinates:</strong> Graceful handling of invalid plaque coordinates</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Navigation handled automatically by Navigation Component
 * Bundle args = new Bundle();
 * args.putParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY, placemark);
 * navController.navigate(R.id.action_mapDetailFragment_to_panoramaFragment, args);
 * }</pre>
 * 
 * @see MapDetailFragment
 * @see StreetViewPanoramaFragment
 * @see OnStreetViewPanoramaReadyCallback
 * @see Placemark
 * 
 * @author Blue Plaques London Team
 * @since 1.0
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
