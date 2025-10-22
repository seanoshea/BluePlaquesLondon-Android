package com.upwardsnorthwards.blueplaqueslondon.data.local;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.upwardsnorthwards.blueplaqueslondon.data.local.dao.PlaqueDao;
import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;

/**
 * Room database for Blue Plaques London.
 * Stores plaque information locally for offline access.
 */
@Database(
        entities = {PlaqueEntity.class},
        version = 1,
        exportSchema = true
)
public abstract class PlaqueDatabase extends RoomDatabase {

    /**
     * Get the DAO for plaque operations.
     */
    public abstract PlaqueDao plaqueDao();
}
