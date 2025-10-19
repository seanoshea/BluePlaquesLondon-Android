package com.upwardsnorthwards.blueplaqueslondon.fragments;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class MapDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private MapDetailViewModel viewModel;

    @Before
    public void setUp() {
        viewModel = new MapDetailViewModel();
    }

    @Test
    public void setPlacemarks_shouldUpdateCurrentPlacemark() {
        List<Placemark> placemarks = new ArrayList<>();
        Placemark placemark1 = new Placemark();
        placemark1.setName("Placemark 1");
        placemarks.add(placemark1);

        viewModel.setPlacemarks(placemarks);

        assertEquals(placemark1, viewModel.getCurrentPlacemark().getValue());
    }

    @Test
    public void switchToPlacemark_shouldUpdateCurrentPlacemark() {
        List<Placemark> placemarks = new ArrayList<>();
        Placemark placemark1 = new Placemark();
        placemark1.setName("Placemark 1");
        Placemark placemark2 = new Placemark();
        placemark2.setName("Placemark 2");
        placemarks.add(placemark1);
        placemarks.add(placemark2);

        viewModel.setPlacemarks(placemarks);
        viewModel.switchToPlacemark(placemark2);

        assertEquals(placemark2, viewModel.getCurrentPlacemark().getValue());
    }
}
