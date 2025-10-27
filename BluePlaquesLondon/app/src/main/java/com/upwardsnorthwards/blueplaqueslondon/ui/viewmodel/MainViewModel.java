package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * Primary ViewModel managing blue plaque data and application state.
 * 
 * <p>This ViewModel serves as the central data management component for the main application
 * screens, coordinating between the UI layer and the data repository. It follows MVVM
 * architecture principles and provides reactive data streams via LiveData.</p>
 * 
 * <h3>Key Responsibilities:</h3>
 * <ul>
 *   <li><strong>Data Management:</strong> Loads and manages blue plaque data from repository</li>
 *   <li><strong>State Management:</strong> Tracks loading states, errors, and user selections</li>
 *   <li><strong>Search Functionality:</strong> Provides plaque search capabilities</li>
 *   <li><strong>Reactive Updates:</strong> Exposes data via LiveData for UI observation</li>
 *   <li><strong>Resource Management:</strong> Proper RxJava disposable handling</li>
 * </ul>
 * 
 * <h3>Architecture Integration:</h3>
 * <p>Integrates with the application architecture as follows:</p>
 * <ul>
 *   <li><strong>Repository Layer:</strong> Uses {@link PlaquesRepository} for data operations</li>
 *   <li><strong>Dependency Injection:</strong> Hilt-managed ViewModel with injected dependencies</li>
 *   <li><strong>Reactive Programming:</strong> RxJava3 for asynchronous operations</li>
 *   <li><strong>UI Layer:</strong> Observed by {@link com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity} and fragments</li>
 * </ul>
 * 
 * <h3>Data Flow:</h3>
 * <pre>{@code
 * Assets/Database -> PlaquesRepository -> MainViewModel -> UI Components
 *                                      (RxJava3)      (LiveData)
 * }</pre>
 * 
 * <h3>LiveData Streams:</h3>
 * <ul>
 *   <li><strong>Plaques:</strong> {@link #getPlaques()} - Complete list of blue plaques</li>
 *   <li><strong>Selected Plaque:</strong> {@link #getSelectedPlaque()} - Currently selected plaque</li>
 *   <li><strong>Loading State:</strong> {@link #getLoading()} - Data loading indicator</li>
 *   <li><strong>Error Messages:</strong> {@link #getError()} - Error information for user feedback</li>
 *   <li><strong>Data Loaded:</strong> {@link #getDataLoaded()} - Initial data load completion</li>
 * </ul>
 * 
 * <h3>Usage Example:</h3>
 * <pre>{@code
 * // In Activity/Fragment
 * mainViewModel.getPlaques().observe(this, plaques -> {
 *     // Update UI with plaque data
 *     updateMapMarkers(plaques);
 * });
 * 
 * mainViewModel.getLoading().observe(this, isLoading -> {
 *     progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
 * });
 * 
 * // Load data
 * mainViewModel.loadPlaques();
 * }</pre>
 * 
 * @see PlaquesRepository
 * @see com.upwardsnorthwards.blueplaqueslondon.activities.MainActivity
 * @see com.upwardsnorthwards.blueplaqueslondon.fragments.BluePlaquesMapFragment
 * 
 * @author Blue Plaques London Team
 * @since 3.0
 */
@HiltViewModel
public class MainViewModel extends ViewModel {

    private final PlaquesRepository plaquesRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<List<Placemark>> plaquesLiveData = new MutableLiveData<>();
    private final MutableLiveData<Placemark> selectedPlaqueLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dataLoadedLiveData = new MutableLiveData<>(false);

    @Inject
    public MainViewModel(@NonNull PlaquesRepository plaquesRepository) {
        this.plaquesRepository = plaquesRepository;
    }

    /**
     * Get all plaques as LiveData.
     */
    public LiveData<List<Placemark>> getPlaques() {
        return plaquesLiveData;
    }

    /**
     * Get the currently selected plaque.
     */
    public LiveData<Placemark> getSelectedPlaque() {
        return selectedPlaqueLiveData;
    }

    /**
     * Get loading state.
     */
    public LiveData<Boolean> getLoading() {
        return loadingLiveData;
    }

    /**
     * Get error messages.
     */
    public LiveData<String> getError() {
        return errorLiveData;
    }

    /**
     * Get data loaded state.
     */
    public LiveData<Boolean> getDataLoaded() {
        return dataLoadedLiveData;
    }

    /**
     * Load plaques from repository.
     * First loads from assets into database, then observes database.
     */
    public void loadPlaques() {
        loadingLiveData.setValue(true);

        // First, ensure data is loaded from assets into database
        Disposable disposable = plaquesRepository.loadPlaquesFromAssets()
                .andThen(plaquesRepository.getAllPlaques())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        plaques -> {
                            plaquesLiveData.setValue(plaques);
                            loadingLiveData.setValue(false);
                            dataLoadedLiveData.setValue(true);
                        },
                        error -> {
                            errorLiveData.setValue("Error loading plaques: " + error.getMessage());
                            loadingLiveData.setValue(false);
                        }
                );

        disposables.add(disposable);
    }

    /**
     * Select a plaque.
     */
    public void selectPlaque(@NonNull Placemark placemark) {
        selectedPlaqueLiveData.setValue(placemark);
    }

    /**
     * Search plaques by name.
     */
    public void searchPlaques(@NonNull String query) {
        if (query.isEmpty()) {
            loadPlaques();
            return;
        }

        Disposable disposable = plaquesRepository.searchPlaquesByName(query)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        plaquesLiveData::setValue,
                        error -> errorLiveData.setValue("Search error: " + error.getMessage())
                );

        disposables.add(disposable);
    }

    /**
     * Refresh plaque data from assets.
     */
    public void refreshPlaques() {
        loadingLiveData.setValue(true);

        Disposable disposable = plaquesRepository.refreshPlaques()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        () -> {
                            loadingLiveData.setValue(false);
                            loadPlaques(); // Reload after refresh
                        },
                        error -> {
                            errorLiveData.setValue("Refresh error: " + error.getMessage());
                            loadingLiveData.setValue(false);
                        }
                );

        disposables.add(disposable);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
