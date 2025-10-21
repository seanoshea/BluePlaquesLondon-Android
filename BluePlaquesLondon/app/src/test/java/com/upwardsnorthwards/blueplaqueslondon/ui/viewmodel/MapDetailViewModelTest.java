package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MapDetailViewModel.
 */
@RunWith(MockitoJUnitRunner.class)
public class MapDetailViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private PlaquesRepository plaquesRepository;

    @Mock
    private Observer<Placemark> plaqueObserver;

    @Mock
    private Observer<Boolean> loadingObserver;

    @Mock
    private Observer<String> errorObserver;

    private MapDetailViewModel viewModel;

    @Before
    public void setUp() {
        // Set RxJava to use trampolineScheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        viewModel = new MapDetailViewModel(plaquesRepository);
    }

    @org.junit.After
    public void tearDown() {
        // Reset RxJava plugins after tests
        RxJavaPlugins.reset();
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.reset();
    }

    @Test
    public void loadPlaque_success() {
        // Given
        String plaqueId = "test-plaque-1";
        Placemark testPlaque = new Placemark();
        testPlaque.setName("Test Plaque");

        when(plaquesRepository.getPlaqueById(plaqueId))
                .thenReturn(Single.just(testPlaque));

        viewModel.getPlaque().observeForever(plaqueObserver);
        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.loadPlaque(plaqueId);

        // Then
        verify(plaquesRepository).getPlaqueById(plaqueId);
        verify(plaqueObserver, times(1)).onChanged(testPlaque);
        verify(loadingObserver).onChanged(true);
        verify(loadingObserver, atLeastOnce()).onChanged(false);
        assertNotNull(viewModel.getPlaque().getValue());
    }

    @Test
    public void loadPlaque_notFound() {
        // Given
        String plaqueId = "nonexistent-plaque";
        when(plaquesRepository.getPlaqueById(plaqueId))
                .thenReturn(Single.error(new Exception("Not found")));

        viewModel.getLoading().observeForever(loadingObserver);
        viewModel.getError().observeForever(errorObserver);

        // When
        viewModel.loadPlaque(plaqueId);

        // Then
        verify(plaquesRepository).getPlaqueById(plaqueId);
        verify(loadingObserver).onChanged(true);
        verify(loadingObserver, atLeastOnce()).onChanged(false);
        verify(errorObserver).onChanged("Error loading plaque: Not found");
    }

    @Test
    public void loadPlaque_error() {
        // Given
        String plaqueId = "test-plaque-1";
        String errorMessage = "Network error";
        when(plaquesRepository.getPlaqueById(plaqueId))
                .thenReturn(Single.error(new Exception(errorMessage)));

        viewModel.getError().observeForever(errorObserver);
        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.loadPlaque(plaqueId);

        // Then
        verify(errorObserver).onChanged("Error loading plaque: " + errorMessage);
        verify(loadingObserver).onChanged(true);
        verify(loadingObserver, atLeastOnce()).onChanged(false);
    }

    @Test
    public void getPlaque_returnsCorrectData() {
        // Given
        Placemark testPlaque = new Placemark();
        testPlaque.setName("Test Plaque");

        // When
        viewModel.setPlaque(testPlaque);

        // Then
        assertNotNull(viewModel.getPlaque().getValue());
        assertEquals("Test Plaque", viewModel.getPlaque().getValue().getName());
    }

    @Test
    public void loadingState_transitions() {
        // Given
        String plaqueId = "test-plaque-1";
        Placemark testPlaque = new Placemark();
        testPlaque.setName("Test Plaque");

        when(plaquesRepository.getPlaqueById(plaqueId))
                .thenReturn(Single.just(testPlaque));

        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.loadPlaque(plaqueId);

        // Then - verify loading state transitions
        verify(loadingObserver).onChanged(true);  // Loading starts
        verify(loadingObserver, atLeastOnce()).onChanged(false); // Loading ends
    }

    @Test
    public void errorState_propagation() {
        // Given
        String plaqueId = "test-plaque-1";
        String errorMessage = "Repository error";
        when(plaquesRepository.getPlaqueById(plaqueId))
                .thenReturn(Single.error(new RuntimeException(errorMessage)));

        viewModel.getError().observeForever(errorObserver);

        // When
        viewModel.loadPlaque(plaqueId);

        // Then
        verify(errorObserver, times(1)).onChanged("Error loading plaque: " + errorMessage);
    }

    @Test
    public void setPlaque_directlyUpdatesPlaqueData() {
        // Given
        Placemark plaque1 = new Placemark();
        plaque1.setName("Plaque 1");
        Placemark plaque2 = new Placemark();
        plaque2.setName("Plaque 2");

        viewModel.getPlaque().observeForever(plaqueObserver);

        // When
        viewModel.setPlaque(plaque1);
        viewModel.setPlaque(plaque2);

        // Then
        verify(plaqueObserver, times(1)).onChanged(plaque1);
        verify(plaqueObserver, times(1)).onChanged(plaque2);
        assertEquals("Plaque 2", viewModel.getPlaque().getValue().getName());
    }
}
