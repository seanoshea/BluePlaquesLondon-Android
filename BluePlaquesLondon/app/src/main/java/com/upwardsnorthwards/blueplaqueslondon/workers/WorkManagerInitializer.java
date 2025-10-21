package com.upwardsnorthwards.blueplaqueslondon.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;

/**
 * Initializes and schedules WorkManager periodic tasks.
 */
@Singleton
public class WorkManagerInitializer {

    private static final String TAG = "WorkManagerInitializer";
    private static final String PLAQUE_SYNC_WORK_NAME = "plaque_sync_periodic";

    // Sync plaques once per day
    private static final long SYNC_INTERVAL_HOURS = 24;

    private final Context context;
    private final WorkManager workManager;

    @Inject
    public WorkManagerInitializer(
            @NonNull @ApplicationContext Context context,
            @NonNull WorkManager workManager) {
        this.context = context;
        this.workManager = workManager;
    }

    /**
     * Schedule periodic plaque sync work.
     * This will sync plaque data once per day when WiFi is available.
     */
    public void schedulePeriodicPlaquesSync() {
        Log.d(TAG, "Scheduling periodic plaque sync");

        // Define constraints: require network connection (preferably WiFi)
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.UNMETERED) // WiFi only
                .setRequiresBatteryNotLow(true) // Don't sync when battery is low
                .build();

        // Create periodic work request
        PeriodicWorkRequest syncWorkRequest = new PeriodicWorkRequest.Builder(
                PlaqueSyncWorker.class,
                SYNC_INTERVAL_HOURS,
                TimeUnit.HOURS)
                .setConstraints(constraints)
                .addTag("plaque_sync")
                .build();

        // Enqueue with KEEP policy to avoid duplicates
        workManager.enqueueUniquePeriodicWork(
                PLAQUE_SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncWorkRequest
        );

        Log.d(TAG, "Periodic plaque sync scheduled successfully");
    }

    /**
     * Cancel all scheduled work.
     */
    public void cancelAllWork() {
        workManager.cancelUniqueWork(PLAQUE_SYNC_WORK_NAME);
        Log.d(TAG, "Cancelled all scheduled work");
    }
}
