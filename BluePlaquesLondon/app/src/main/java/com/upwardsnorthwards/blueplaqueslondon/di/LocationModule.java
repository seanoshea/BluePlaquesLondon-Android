package com.upwardsnorthwards.blueplaqueslondon.di;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Hilt module providing location services dependencies
 */
@Module
@InstallIn(SingletonComponent.class)
public class LocationModule {

    @Provides
    @Singleton
    public FusedLocationProviderClient provideFusedLocationProviderClient(@NonNull @ApplicationContext Context context) {
        return LocationServices.getFusedLocationProviderClient(context);
    }
}
