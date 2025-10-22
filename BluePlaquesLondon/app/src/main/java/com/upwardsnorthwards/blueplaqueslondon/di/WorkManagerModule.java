package com.upwardsnorthwards.blueplaqueslondon.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.work.Configuration;
import androidx.work.WorkManager;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module providing WorkManager dependencies with Hilt worker factory integration.
 */
@Module
@InstallIn(SingletonComponent.class)
public class WorkManagerModule {

    @Provides
    @Singleton
    public WorkManager provideWorkManager(@NonNull @ApplicationContext Context context) {
        return WorkManager.getInstance(context);
    }

    /**
     * Configuration provider for WorkManager with Hilt-enabled worker factory.
     */
    @Provides
    @Singleton
    public Configuration.Provider provideWorkerConfiguration(@NonNull HiltWorkerFactory hiltWorkerFactory) {
        return () -> new Configuration.Builder()
                .setWorkerFactory(hiltWorkerFactory)
                .build();
    }
}
