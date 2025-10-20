package com.upwardsnorthwards.blueplaqueslondon.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

/**
 * Data Access Object for Blue Plaques.
 * Uses RxJava3 for reactive queries.
 */
@Dao
public interface PlaqueDao {

    /**
     * Get all plaques as a Flowable (observable stream).
     * Updates automatically when data changes.
     */
    @Query("SELECT * FROM plaques ORDER BY name ASC")
    Flowable<List<PlaqueEntity>> getAllPlaques();

    /**
     * Get all plaques as a Single (one-time query).
     */
    @Query("SELECT * FROM plaques ORDER BY name ASC")
    Single<List<PlaqueEntity>> getAllPlaquesOnce();

    /**
     * Get a plaque by its ID.
     */
    @Query("SELECT * FROM plaques WHERE id = :id LIMIT 1")
    Single<PlaqueEntity> getPlaqueById(String id);

    /**
     * Search plaques by name (case-insensitive).
     */
    @Query("SELECT * FROM plaques WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    Flowable<List<PlaqueEntity>> searchPlaquesByName(String query);

    /**
     * Get plaques within a bounding box (for map viewport).
     */
    @Query("SELECT * FROM plaques WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    Flowable<List<PlaqueEntity>> getPlaquesInBounds(double minLat, double maxLat, double minLng, double maxLng);

    /**
     * Insert a single plaque.
     * Replace on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlaque(PlaqueEntity plaque);

    /**
     * Insert multiple plaques.
     * Replace on conflict.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertPlaques(List<PlaqueEntity> plaques);

    /**
     * Delete all plaques (for refreshing data).
     */
    @Query("DELETE FROM plaques")
    Completable deleteAllPlaques();

    /**
     * Get count of plaques in database.
     */
    @Query("SELECT COUNT(*) FROM plaques")
    Single<Integer> getPlaqueCount();
}
