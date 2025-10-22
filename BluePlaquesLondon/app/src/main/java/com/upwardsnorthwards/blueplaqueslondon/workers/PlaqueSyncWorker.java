package com.upwardsnorthwards.blueplaqueslondon.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorker;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;

/**
 * WorkManager worker that periodically syncs plaque data.
 * Refreshes plaques from the KML file to ensure data is up to date.
 */
@HiltWorker
public class PlaqueSyncWorker extends Worker {

    private static final String TAG = "PlaqueSyncWorker";
    private final PlaquesRepository plaquesRepository;

    @AssistedInject
    public PlaqueSyncWorker(
            @Assisted @NonNull Context context,
            @Assisted @NonNull WorkerParameters workerParams,
            @NonNull PlaquesRepository plaquesRepository) {
        super(context, workerParams);
        this.plaquesRepository = plaquesRepository;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Starting plaque sync...");

        try {
            // Execute the refresh synchronously (blocking)
            plaquesRepository.refreshPlaques().blockingAwait();
            Log.d(TAG, "Plaque sync completed successfully");
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, "Plaque sync failed: " + e.getMessage());
            return Result.retry();
        }
    }
}
