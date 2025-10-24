package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.adapters.MultiplePlacemarksAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import java.util.ArrayList;
import java.util.List;

/**
 * Detail view fragment displaying comprehensive information about selected blue plaques.
 * 
 * <p>This fragment presents detailed information about one or more blue plaques when a user
 * selects a marker from the map. It provides navigation options to Wikipedia articles and
 * Google Street View, along with comprehensive plaque metadata.</p>
 * 
 * <h3>Key Features:</h3>
 * <ul>
 *   <li><strong>Plaque Details:</strong> Name, occupation, address, council, and installation year</li>
 *   <li><strong>Wikipedia Integration:</strong> Direct navigation to relevant Wikipedia articles</li>
 *   <li><strong>Street View Access:</strong> Launch Google Street View for plaque location</li>
 *   <li><strong>Multiple Plaques:</strong> Handle multiple plaques at the same location</li>
 *   <li><strong>Responsive UI:</strong> Adaptive layout based on available information</li>
 *   <li><strong>Analytics Tracking:</strong> User interaction monitoring</li>
 * </ul>
 * 
 * <h3>Navigation Integration:</h3>
 * <p>This fragment integrates with the Navigation Component architecture:</p>
 * <ul>
 *   <li><strong>Entry:</strong> Navigated from {@link BluePlaquesMapFragment} via info window clicks</li>
 *   <li><strong>Exit:</strong> Navigates to {@link WikipediaFragment} or {@link PanoramaFragment}</li>
 *   <li><strong>Arguments:</strong> Receives plaque data via Bundle arguments</li>
 * </ul>
 * 
 * <h3>Multiple Plaques Handling:</h3>
 * <p>When multiple plaques exist at the same location:</p>
 * <ul>
 *   <li>Displays the first plaque by default</li>
 *   <li>Shows "More" button to select alternative plaques</li>
 *   <li>Uses {@link MultiplePlacemarksAdapter} for selection dialog</li>
 *   <li>Dynamically updates UI when plaque selection changes</li>
 * </ul>
 * 
 * <h3>Data Flow:</h3>
 * <pre>{@code
 * BluePlaquesMapFragment -> MapDetailFragment -> WikipediaFragment/PanoramaFragment
 *                        (Bundle args)        (Bundle args)
 * }</pre>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // Navigation is handled automatically by Navigation Component
 * Bundle args = new Bundle();
 * args.putParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY, placemarks);
 * navController.navigate(R.id.action_mapFragment_to_mapDetailFragment, args);
 * }</pre>
 * 
 * @see BluePlaquesMapFragment
 * @see WikipediaFragment
 * @see PanoramaFragment
 * @see MultiplePlacemarksAdapter
 * 
 * @author Blue Plaques London Team
 * @since 1.0
 */
public class MapDetailFragment extends Fragment implements View.OnClickListener {

    private List<Placemark> placemarks = new ArrayList<>();
    private NavController navController;

    private TextView occupationTextView;
    private TextView addressTextView;
    private TextView councilAndYearTextView;
    private TextView noteTextView;
    private Button streetViewButton;
    private Button wikipediaArticleButton;
    private Button moreButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_map_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = Navigation.findNavController(view);
        getPlacemarksFromArguments();
        setupViews(view);
    }

    @Override
    public void onClick(@NonNull View v) {
        if (v.equals(streetViewButton)) {
            streetViewButtonClicked();
        } else if (v.equals(wikipediaArticleButton)) {
            wikipediaArticleButtonClicked();
        } else if (v.equals(moreButton)) {
            moreButtonClicked();
        }
    }

    private void getPlacemarksFromArguments() {
        if (getArguments() != null) {
            placemarks = getArguments().getParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY);
            if (placemarks == null) {
                placemarks = new ArrayList<>();
            }
        }
    }

    private void setupViews(@NonNull View view) {
        occupationTextView = view.findViewById(R.id.activity_map_details_occupation);
        addressTextView = view.findViewById(R.id.activity_map_details_address);
        councilAndYearTextView = view.findViewById(R.id.activity_map_details_council_and_year);
        noteTextView = view.findViewById(R.id.activity_map_details_note);
        streetViewButton = view.findViewById(R.id.activity_map_details_street_view);
        wikipediaArticleButton = view.findViewById(R.id.activity_map_details_wikipedia_article);
        moreButton = view.findViewById(R.id.activity_map_details_more);
        addClickListenersToButtons();
        addTextToTextViews();
    }

    private void addClickListenersToButtons() {
        streetViewButton.setOnClickListener(this);
        wikipediaArticleButton.setOnClickListener(this);
        moreButton.setOnClickListener(this);
        moreButton.setVisibility(placemarks.size() == 1 ? View.GONE : View.VISIBLE);
    }

    private void addTextToTextViews() {
        if (placemarks.isEmpty()) {
            return;
        }

        Placemark currentPlacemark = placemarks.get(0);
        currentPlacemark.digestAnciliaryInformation();
        occupationTextView.setText(currentPlacemark.getTrimmedOccupation());
        addressTextView.setText(currentPlacemark.getAddress());

        String councilAndYear = currentPlacemark.getCouncilAndYear();
        if (councilAndYear != null) {
            councilAndYearTextView.setText(councilAndYear);
            councilAndYearTextView.setVisibility(View.VISIBLE);
        } else {
            councilAndYearTextView.setVisibility(View.GONE);
        }

        String note = currentPlacemark.getNote();
        if (note != null) {
            noteTextView.setText(note);
            noteTextView.setVisibility(View.VISIBLE);
        } else {
            noteTextView.setVisibility(View.GONE);
        }
    }

    private void switchToPlacemark(final Placemark placemark) {
        placemarks.remove(placemark);
        placemarks.add(0, placemark);
        addTextToTextViews();
    }

    private void moreButtonClicked() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.multiple_placemarks_select_one));
        final MultiplePlacemarksAdapter arrayAdapter = new MultiplePlacemarksAdapter(
                requireActivity(), placemarks);
        builder.setNegativeButton(getString(R.string.cancel),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(@NonNull DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchToPlacemark(arrayAdapter.getItem(which));
            }
        });
        builder.show();
        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) requireActivity().getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.DETAILS_BUTTON_PRESSED_EVENT, placemarks.get(0).getTrimmedName());
    }

    private void wikipediaArticleButtonClicked() {
        final Placemark placemark = placemarks.get(0);
        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) requireActivity().getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.WIKIPEDIA_BUTTON_PRESSED_EVENT,
                placemark.getTrimmedName());

        Bundle args = new Bundle();
        args.putParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, placemark);
        navController.navigate(R.id.action_mapDetailFragment_to_wikipediaFragment, args);
    }

    private void streetViewButtonClicked() {
        final Placemark placemark = placemarks.get(0);
        BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) requireActivity().getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.STREETVIEW_BUTTON_PRESSED_EVENT,
                placemark.getTrimmedName());

        Bundle args = new Bundle();
        args.putParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY, placemark);
        navController.navigate(R.id.action_mapDetailFragment_to_panoramaFragment, args);
    }
}
