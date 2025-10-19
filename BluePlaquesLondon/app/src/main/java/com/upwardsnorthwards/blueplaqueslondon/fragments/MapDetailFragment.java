package com.upwardsnorthwards.blueplaqueslondon.fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.compose.runtime.livedata.observeAsState;
import androidx.compose.ui.platform.ComposeView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.upwardsnorthwards.blueplaqueslondon.BluePlaquesLondonApplication;
import com.upwardsnorthwards.blueplaqueslondon.R;
import com.upwardsnorthwards.blueplaqueslondon.adapters.MultiplePlacemarksAdapter;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesConstants;

import java.util.ArrayList;

public class MapDetailFragment extends Fragment {

    private MapDetailViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(this).get(MapDetailViewModel.class);
        if (getArguments() != null) {
            ArrayList<Placemark> placemarks = getArguments().getParcelableArrayList(BluePlaquesConstants.INFO_WINDOW_CLICKED_PARCLEABLE_KEY);
            viewModel.setPlacemarks(placemarks);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return new ComposeView(requireContext());
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((ComposeView) view).setContent {
            Placemark placemark = viewModel.getCurrentPlacemark().observeAsState().getValue();
            if (placemark != null) {
                MapDetailScreen(
                    placemark = placemark,
                    onStreetViewClick = () -> {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(BluePlaquesConstants.PANORAMA_CLICKED_PARCLEABLE_KEY, placemark);
                        NavHostFragment.findNavController(this).navigate(R.id.panorama_fragment, bundle);
                    },
                    onWikipediaClick = () -> {
                        Bundle bundle = new Bundle();
                        bundle.putParcelable(BluePlaquesConstants.WIKIPEDIA_CLICKED_PARCLEABLE_KEY, placemark);
                        NavHostFragment.findNavController(this).navigate(R.id.wikipedia_fragment, bundle);
                    },
                    onMoreClick = this::moreButtonClicked,
                    isMoreButtonVisible = viewModel.getAllPlacemarks().size() > 1
                );
            }
        };
    }

    private void moreButtonClicked() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setIcon(R.drawable.ic_launcher);
        builder.setTitle(getString(R.string.multiple_placemarks_select_one));
        final MultiplePlacemarksAdapter arrayAdapter = new MultiplePlacemarksAdapter(
                getContext(), viewModel.getAllPlacemarks());
        builder.setNegativeButton(getString(R.string.cancel),
                (dialog, which) -> dialog.dismiss());

        builder.setAdapter(arrayAdapter, (dialog, which) -> {
            Placemark selectedPlacemark = arrayAdapter.getItem(which);
            if (selectedPlacemark != null) {
                viewModel.switchToPlacemark(selectedPlacemark);
            }
        });
        builder.show();
        final BluePlaquesLondonApplication app = (BluePlaquesLondonApplication) requireActivity().getApplication();
        app.trackEvent(BluePlaquesConstants.UI_ACTION_CATEGORY,
                BluePlaquesConstants.DETAILS_BUTTON_PRESSED_EVENT, viewModel.getCurrentPlacemark().getValue().getTrimmedName());
    }
}
