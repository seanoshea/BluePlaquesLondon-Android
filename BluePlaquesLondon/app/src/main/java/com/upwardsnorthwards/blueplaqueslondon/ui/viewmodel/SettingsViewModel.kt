package com.upwardsnorthwards.blueplaqueslondon.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.ViewModel
import com.upwardsnorthwards.blueplaqueslondon.data.preferences.AppPreferencesDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject

/**
 * ViewModel for Settings screen
 * Manages user preferences and app settings
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val preferencesDataStore: AppPreferencesDataStore
) : ViewModel() {

    private val disposables = CompositeDisposable()

    fun getAnalyticsEnabled(): LiveData<Boolean> {
        return LiveDataReactiveStreams.fromPublisher(
            preferencesDataStore.getAnalyticsEnabled()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    fun setAnalyticsEnabled(enabled: Boolean) {
        val disposable = preferencesDataStore.saveAnalyticsEnabled(enabled)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { /* Success */ },
                { error -> error.printStackTrace() }
            )
        disposables.add(disposable)
    }

    fun getLaunchCount(): LiveData<Int> {
        return LiveDataReactiveStreams.fromPublisher(
            preferencesDataStore.getLaunchCount()
                .map { it as Int }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
        )
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
