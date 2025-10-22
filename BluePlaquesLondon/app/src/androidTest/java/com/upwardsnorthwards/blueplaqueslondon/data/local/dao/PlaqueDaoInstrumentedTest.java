package com.upwardsnorthwards.blueplaqueslondon.data.local.dao;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.SmallTest;

import com.upwardsnorthwards.blueplaqueslondon.data.local.PlaqueDatabase;
import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented tests for PlaqueDao.
 * These tests run on an Android device or emulator with a real Room database.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class PlaqueDaoInstrumentedTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PlaqueDatabase database;
    private PlaqueDao plaqueDao;

    @Before
    public void setUp() {
        Context context = ApplicationProvider.getApplicationContext();
        // Create an in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, PlaqueDatabase.class)
                .allowMainThreadQueries()
                .build();
        plaqueDao = database.plaqueDao();
    }

    @After
    public void tearDown() {
        database.close();
    }

    @Test
    public void testInsertAndGetPlaque() {
        // Given
        PlaqueEntity plaque = createTestPlaque("1", "Charles Darwin");

        // When
        plaqueDao.insertPlaque(plaque).blockingAwait();
        PlaqueEntity retrieved = plaqueDao.getPlaqueById("1").blockingGet();

        // Then
        assertNotNull(retrieved);
        assertEquals("1", retrieved.getId());
        assertEquals("Charles Darwin", retrieved.getName());
    }

    @Test
    public void testInsertMultiplePlaquesAndGetAll() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Charles Darwin"));
        plaques.add(createTestPlaque("2", "Alan Turing"));
        plaques.add(createTestPlaque("3", "Ada Lovelace"));

        // When
        plaqueDao.insertPlaques(plaques).blockingAwait();
        List<PlaqueEntity> retrieved = plaqueDao.getAllPlaquesOnce().blockingGet();

        // Then
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
    }

    @Test
    public void testSearchPlaquesByName() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Charles Darwin"));
        plaques.add(createTestPlaque("2", "Alan Turing"));
        plaques.add(createTestPlaque("3", "Charles Dickens"));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When
        List<PlaqueEntity> results = plaqueDao.searchPlaquesByName("Charles").blockingFirst();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
        assertTrue(results.get(0).getName().contains("Charles") ||
                   results.get(1).getName().contains("Charles"));
    }

    @Test
    public void testGetPlaquesInBounds() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaqueWithLocation("1", "London Plaque", 51.5, -0.1));
        plaques.add(createTestPlaqueWithLocation("2", "Far Away Plaque", 55.0, -3.0));
        plaques.add(createTestPlaqueWithLocation("3", "Another London Plaque", 51.52, -0.12));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When - get plaques in London bounds
        List<PlaqueEntity> results = plaqueDao.getPlaquesInBounds(51.4, 51.6, -0.2, 0.0)
                .blockingFirst();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void testDeleteAllPlaques() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Charles Darwin"));
        plaques.add(createTestPlaque("2", "Alan Turing"));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When
        plaqueDao.deleteAllPlaques().blockingAwait();
        List<PlaqueEntity> retrieved = plaqueDao.getAllPlaquesOnce().blockingGet();

        // Then
        assertNotNull(retrieved);
        assertEquals(0, retrieved.size());
    }

    @Test
    public void testGetPlaqueCount() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Charles Darwin"));
        plaques.add(createTestPlaque("2", "Alan Turing"));
        plaques.add(createTestPlaque("3", "Ada Lovelace"));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When
        int count = plaqueDao.getPlaqueCount().blockingGet();

        // Then
        assertEquals(3, count);
    }

    @Test
    public void testInsertWithReplaceStrategy() {
        // Given
        PlaqueEntity plaque1 = createTestPlaque("1", "Charles Darwin");
        plaqueDao.insertPlaque(plaque1).blockingAwait();

        // When - insert same ID with different data
        PlaqueEntity plaque2 = createTestPlaque("1", "Charles Darwin Updated");
        plaqueDao.insertPlaque(plaque2).blockingAwait();

        // Then - should replace, not create duplicate
        List<PlaqueEntity> retrieved = plaqueDao.getAllPlaquesOnce().blockingGet();
        assertEquals(1, retrieved.size());
        assertEquals("Charles Darwin Updated", retrieved.get(0).getName());
    }

    @Test
    public void testGetAllPlaquesOrderedByName() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Zebra Person"));
        plaques.add(createTestPlaque("2", "Alan Turing"));
        plaques.add(createTestPlaque("3", "Darwin, Charles"));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When
        List<PlaqueEntity> retrieved = plaqueDao.getAllPlaquesOnce().blockingGet();

        // Then - should be alphabetically ordered
        assertNotNull(retrieved);
        assertEquals(3, retrieved.size());
        assertEquals("Alan Turing", retrieved.get(0).getName());
    }

    @Test
    public void testGetAllPlaquesAsFlowable() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Charles Darwin"));

        // When
        plaqueDao.insertPlaques(plaques).blockingAwait();
        List<PlaqueEntity> retrieved = plaqueDao.getAllPlaques().blockingFirst();

        // Then
        assertNotNull(retrieved);
        assertEquals(1, retrieved.size());
    }

    @Test
    public void testSearchWithNoResults() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaque("1", "Charles Darwin"));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When
        List<PlaqueEntity> results = plaqueDao.searchPlaquesByName("NonExistent").blockingFirst();

        // Then
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    @Test
    public void testGetPlaquesInBoundsWithNoResults() {
        // Given
        List<PlaqueEntity> plaques = new ArrayList<>();
        plaques.add(createTestPlaqueWithLocation("1", "London Plaque", 51.5, -0.1));
        plaqueDao.insertPlaques(plaques).blockingAwait();

        // When - search far from London
        List<PlaqueEntity> results = plaqueDao.getPlaquesInBounds(0.0, 1.0, 0.0, 1.0)
                .blockingFirst();

        // Then
        assertNotNull(results);
        assertEquals(0, results.size());
    }

    private PlaqueEntity createTestPlaque(String id, String name) {
        return createTestPlaqueWithLocation(id, name, 51.5074, -0.1278);
    }

    private PlaqueEntity createTestPlaqueWithLocation(String id, String name, double lat, double lng) {
        PlaqueEntity plaque = new PlaqueEntity();
        plaque.setId(id);
        plaque.setName(name);
        plaque.setTitle(name + " (Test)");
        plaque.setFeatureDescription("Test description for " + name);
        plaque.setLatitude(lat);
        plaque.setLongitude(lng);
        plaque.setAddress("123 Test Street");
        plaque.setOccupation("Test Occupation");
        plaque.setNote("Test note");
        return plaque;
    }
}
