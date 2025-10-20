package com.upwardsnorthwards.blueplaqueslondon.di;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.upwardsnorthwards.blueplaqueslondon.data.local.PlaqueDatabase;
import com.upwardsnorthwards.blueplaqueslondon.data.local.dao.PlaqueDao;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module providing Room database dependencies.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    @Provides
    @Singleton
    public PlaqueDatabase providePlaqueDatabase(@NonNull @ApplicationContext Context context) {
        return Room.databaseBuilder(
                context.getApplicationContext(),
                PlaqueDatabase.class,
                "plaque_database"
        ).build();
    }

    @Provides
    @Singleton
    public PlaqueDao providePlaqueDao(@NonNull PlaqueDatabase database) {
        return database.plaqueDao();
    }
}
