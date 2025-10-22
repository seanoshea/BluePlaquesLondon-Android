package com.upwardsnorthwards.blueplaqueslondon.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.Observer;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel.MainViewModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for MainViewModel.
 */
@RunWith(MockitoJUnitRunner.class)
public class MainViewModelTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Mock
    private PlaquesRepository plaquesRepository;

    @Mock
    private Observer<List<Placemark>> plaquesObserver;

    @Mock
    private Observer<Boolean> loadingObserver;

    @Mock
    private Observer<String> errorObserver;

    private MainViewModel viewModel;

    @Before
    public void setUp() {
        // Set RxJava to use trampolineScheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.setInitMainThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        viewModel = new MainViewModel(plaquesRepository);
    }

    @org.junit.After
    public void tearDown() {
        // Reset RxJava plugins after tests
        RxJavaPlugins.reset();
        io.reactivex.rxjava3.android.plugins.RxAndroidPlugins.reset();
    }

    @Test
    public void loadPlaques_success() {
        // Given
        Placemark plaque1 = new Placemark();
        plaque1.setName("Test Plaque 1");
        Placemark plaque2 = new Placemark();
        plaque2.setName("Test Plaque 2");
        List<Placemark> testPlaques = Arrays.asList(plaque1, plaque2);

        when(plaquesRepository.loadPlaquesFromAssets()).thenReturn(Completable.complete());
        when(plaquesRepository.getAllPlaques()).thenReturn(Flowable.just(testPlaques));

        viewModel.getPlaques().observeForever(plaquesObserver);
        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.loadPlaques();

        // Then
        verify(plaquesRepository).loadPlaquesFromAssets();
        verify(plaquesRepository).getAllPlaques();
        verify(plaquesObserver).onChanged(testPlaques);
        verify(loadingObserver).onChanged(true);
        verify(loadingObserver, times(2)).onChanged(false); // Called twice: once after loadPlaquesFromAssets and once after getAllPlaques
    }

    @Test
    public void loadPlaques_error() {
        // Given
        String errorMessage = "Test error";
        when(plaquesRepository.loadPlaquesFromAssets()).thenReturn(Completable.complete());
        when(plaquesRepository.getAllPlaques())
                .thenReturn(Flowable.error(new Exception(errorMessage)));

        viewModel.getError().observeForever(errorObserver);
        viewModel.getLoading().observeForever(loadingObserver);

        // When
        viewModel.loadPlaques();

        // Then
        verify(errorObserver).onChanged("Error loading plaques: " + errorMessage);
        verify(loadingObserver, times(2)).onChanged(false); // Called twice: once after loadPlaquesFromAssets and once in error handler
    }

    @Test
    public void searchPlaques_success() {
        // Given
        String query = "test";
        Placemark plaque = new Placemark();
        plaque.setName("Test Plaque");
        List<Placemark> searchResults = Arrays.asList(plaque);

        when(plaquesRepository.searchPlaquesByName(query))
                .thenReturn(Flowable.just(searchResults));

        viewModel.getPlaques().observeForever(plaquesObserver);

        // When
        viewModel.searchPlaques(query);

        // Then
        verify(plaquesRepository).searchPlaquesByName(query);
        verify(plaquesObserver).onChanged(searchResults);
    }

    @Test
    public void selectPlaque() {
        // Given
        Placemark plaque = new Placemark();
        plaque.setName("Selected Plaque");

        Observer<Placemark> selectedPlaqueObserver = plaque1 -> {};
        viewModel.getSelectedPlaque().observeForever(selectedPlaqueObserver);

        // When
        viewModel.selectPlaque(plaque);

        // Then
        // Verify the plaque was set (LiveData value is updated)
        assert viewModel.getSelectedPlaque().getValue() != null;
        assert viewModel.getSelectedPlaque().getValue().getName().equals("Selected Plaque");
    }
}
