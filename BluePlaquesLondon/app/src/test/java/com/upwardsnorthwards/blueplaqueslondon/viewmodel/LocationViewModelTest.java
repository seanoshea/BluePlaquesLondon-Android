package com.upwardsnorthwards.blueplaqueslondon.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.LocationViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for LocationViewModel.
 */
@RunWith(MockitoJUnitRunner.class)
public class LocationViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private FusedLocationProviderClient locationClient;

    @Mock
    private Observer<Placemark> closestPlaqueObserver;

    @Mock
    private Observer<String> errorObserver;

    private LocationViewModel viewModel;

    @Before
    public void setUp() {
        // Set RxJava to use trampolineScheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        viewModel = new LocationViewModel(locationClient);
    }

    @org.junit.After
    public void tearDown() {
        RxJavaPlugins.reset();
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.reset();
    }

    @Test
    public void setLocationPermissionGranted() {
        // Given
        Observer<Boolean> permissionObserver = granted -> {};
        viewModel.getLocationPermissionGranted().observeForever(permissionObserver);

        // When
        viewModel.setLocationPermissionGranted(true);

        // Then
        assert viewModel.getLocationPermissionGranted().getValue() != null;
        assert viewModel.getLocationPermissionGranted().getValue();
    }

    @Test
    public void findClosestPlaque_noLocation() {
        // Given
        Placemark plaque = new Placemark();
        plaque.setName("Test Plaque");
        plaque.setLatitude(51.5074);
        plaque.setLongitude(-0.1278);
        List<Placemark> plaques = Arrays.asList(plaque);

        viewModel.getError().observeForever(errorObserver);

        // When
        viewModel.findClosestPlaque(plaques);

        // Then
        verify(errorObserver).onChanged("Current location not available. Please enable location services.");
    }

    @Test
    public void findClosestPlaque_noPlaques() {
        // Given
        viewModel.getError().observeForever(errorObserver);

        // When
        viewModel.findClosestPlaque(null);

        // Then
        // Note: The ViewModel checks location first, so we get location error before plaques check
        verify(errorObserver).onChanged("Current location not available. Please enable location services.");
    }
}
