package com.upwardsnorthwards.blueplaqueslondon.data.repository;

import android.content.Context;

import androidx.annotation.NonNull;

import com.upwardsnorthwards.blueplaqueslondon.data.local.dao.PlaqueDao;
import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;
import com.upwardsnorthwards.blueplaqueslondon.utils.BluePlaquesKMLParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.hilt.android.qualifiers.ApplicationContext;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * Repository for Blue Plaques data.
 * Single source of truth - handles both local (Room) and remote (KML file) data.
 */
@Singleton
public class PlaquesRepository {

    private final PlaqueDao plaqueDao;
    private final Context context;

    @Inject
    public PlaquesRepository(@NonNull PlaqueDao plaqueDao, @NonNull @ApplicationContext Context context) {
        this.plaqueDao = plaqueDao;
        this.context = context;
    }

    /**
     * Get all plaques as a Flowable (updates automatically).
     */
    public Flowable<List<Placemark>> getAllPlaques() {
        return plaqueDao.getAllPlaques()
                .map(this::entitiesToPlacemarks)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Get plaque by ID.
     */
    public Single<Placemark> getPlaqueById(@NonNull String id) {
        return plaqueDao.getPlaqueById(id)
                .map(this::entityToPlacemark)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Search plaques by name.
     */
    public Flowable<List<Placemark>> searchPlaquesByName(@NonNull String query) {
        return plaqueDao.searchPlaquesByName(query)
                .map(this::entitiesToPlacemarks)
                .subscribeOn(Schedulers.io());
    }

    /**
     * Load plaques from KML file and store in database.
     * Should be called once on app startup.
     */
    public Completable loadPlaquesFromAssets() {
        return Single.fromCallable(() -> {
            // Check if database is already populated
            int count = plaqueDao.getPlaqueCount().blockingGet();
            if (count > 0) {
                return new ArrayList<Placemark>(); // Already loaded
            }

            // Parse KML file
            BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
            parser.loadMapData(context);
            return parser.getPlacemarks();
        })
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(placemarks -> {
                    if (placemarks.isEmpty()) {
                        return Completable.complete();
                    }
                    List<PlaqueEntity> entities = placemarksToEntities(placemarks);
                    return plaqueDao.insertPlaques(entities);
                });
    }

    /**
     * Refresh plaque data (delete and reload from KML).
     */
    public Completable refreshPlaques() {
        return plaqueDao.deleteAllPlaques()
                .andThen(Single.fromCallable(() -> {
                    BluePlaquesKMLParser parser = new BluePlaquesKMLParser();
                    parser.loadMapData(context);
                    return parser.getPlacemarks();
                }))
                .subscribeOn(Schedulers.io())
                .flatMapCompletable(placemarks -> {
                    List<PlaqueEntity> entities = placemarksToEntities(placemarks);
                    return plaqueDao.insertPlaques(entities);
                });
    }

    // Mapping methods

    private Placemark entityToPlacemark(@NonNull PlaqueEntity entity) {
        Placemark placemark = new Placemark();
        placemark.setFeatureDescription(entity.getFeatureDescription());
        placemark.setTitle(entity.getTitle());
        placemark.setName(entity.getName());
        placemark.setLatitude(entity.getLatitude());
        placemark.setLongitude(entity.getLongitude());
        placemark.setStyleUrl(entity.getStyleUrl());
        // Digest ancillary information (address, note, etc.)
        placemark.digestAnciliaryInformation();
        return placemark;
    }

    private List<Placemark> entitiesToPlacemarks(@NonNull List<PlaqueEntity> entities) {
        List<Placemark> placemarks = new ArrayList<>();
        for (PlaqueEntity entity : entities) {
            placemarks.add(entityToPlacemark(entity));
        }
        return placemarks;
    }

    private PlaqueEntity placemarkToEntity(@NonNull Placemark placemark) {
        PlaqueEntity entity = new PlaqueEntity();
        entity.setId(placemark.key());
        entity.setFeatureDescription(placemark.getTitle()); // Store full description
        entity.setTitle(placemark.getTitle());
        entity.setName(placemark.getName());
        entity.setOccupation(placemark.getOccupation());
        entity.setAddress(placemark.getAddress());
        entity.setNote(placemark.getNote());
        entity.setCouncilAndYear(placemark.getCouncilAndYear());
        entity.setStyleUrl(placemark.getStyleUrl());
        entity.setLatitude(placemark.getLatitude());
        entity.setLongitude(placemark.getLongitude());
        return entity;
    }

    private List<PlaqueEntity> placemarksToEntities(@NonNull List<Placemark> placemarks) {
        List<PlaqueEntity> entities = new ArrayList<>();
        for (Placemark placemark : placemarks) {
            entities.add(placemarkToEntity(placemark));
        }
        return entities;
    }
}
