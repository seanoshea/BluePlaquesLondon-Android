package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

/**
 * ViewModel for MapDetailFragment.
 * Manages individual plaque details.
 */
@HiltViewModel
public class MapDetailViewModel extends ViewModel {

    private final PlaquesRepository plaquesRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private final MutableLiveData<Placemark> plaqueLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> loadingLiveData = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorLiveData = new MutableLiveData<>();

    @Inject
    public MapDetailViewModel(@NonNull PlaquesRepository plaquesRepository) {
        this.plaquesRepository = plaquesRepository;
    }

    /**
     * Get the plaque details.
     */
    public LiveData<Placemark> getPlaque() {
        return plaqueLiveData;
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
     * Load a plaque by ID.
     */
    public void loadPlaque(@NonNull String plaqueId) {
        loadingLiveData.setValue(true);

        Disposable disposable = plaquesRepository.getPlaqueById(plaqueId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        plaque -> {
                            plaqueLiveData.setValue(plaque);
                            loadingLiveData.setValue(false);
                        },
                        error -> {
                            errorLiveData.setValue("Error loading plaque: " + error.getMessage());
                            loadingLiveData.setValue(false);
                        }
                );

        disposables.add(disposable);
    }

    /**
     * Set plaque directly (when passed from previous screen).
     */
    public void setPlaque(@NonNull Placemark placemark) {
        plaqueLiveData.setValue(placemark);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}
