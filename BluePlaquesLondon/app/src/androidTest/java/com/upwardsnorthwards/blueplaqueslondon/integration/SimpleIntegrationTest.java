package com.upwardsnorthwards.blueplaqueslondon.integration;

import android.content.Context;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.upwardsnorthwards.blueplaqueslondon.data.local.PlaqueDatabase;
import com.upwardsnorthwards.blueplaqueslondon.data.local.dao.PlaqueDao;
import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;
import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Simple integration tests verifying the basic data flow:
 * DAO → Database and Repository → DAO
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class SimpleIntegrationTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private PlaqueDatabase database;
    private PlaqueDao plaqueDao;
    private PlaquesRepository repository;
    private Context context;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();

        // Configure RxJava to use trampoline scheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setNewThreadSchedulerHandler(scheduler -> Schedulers.trampoline());

        // Create in-memory database for testing
        database = Room.inMemoryDatabaseBuilder(context, PlaqueDatabase.class)
                .allowMainThreadQueries()
                .build();
        plaqueDao = database.plaqueDao();

        // Create repository with real DAO
        repository = new PlaquesRepository(plaqueDao, context);
    }

    @After
    public void tearDown() {
        database.close();
        RxJavaPlugins.reset();
    }

    @Test
    public void testDAO_insertAndRetrieve() {
        // Given - create test data
        List<PlaqueEntity> testPlaques = createTestEntities(3);

        // When - insert through DAO
        plaqueDao.insertPlaques(testPlaques).blockingAwait();

        // Then - retrieve through DAO
        List<PlaqueEntity> retrieved = plaqueDao.getAllPlaquesOnce().blockingGet();

        assertNotNull("Retrieved list should not be null", retrieved);
        assertEquals("Should retrieve all inserted plaques", 3, retrieved.size());
    }

    @Test
    public void testRepository_getAllPlaques() {
        // Given - insert test data through DAO
        List<PlaqueEntity> testEntities = createTestEntities(5);
        plaqueDao.insertPlaques(testEntities).blockingAwait();

        // When - retrieve through repository
        List<Placemark> placemarks = repository.getAllPlaques().blockingFirst();

        // Then
        assertNotNull("Placemarks should not be null", placemarks);
        assertEquals("Should get 5 placemarks", 5, placemarks.size());
    }

    @Test
    public void testRepository_searchPlaques() {
        // Given - insert test data
        List<PlaqueEntity> testEntities = new ArrayList<>();
        testEntities.add(createEntityWithName("1", "Charles Darwin"));
        testEntities.add(createEntityWithName("2", "Charles Dickens"));
        testEntities.add(createEntityWithName("3", "Alan Turing"));
        plaqueDao.insertPlaques(testEntities).blockingAwait();

        // When - search through repository
        List<Placemark> results = repository.searchPlaquesByName("Charles").blockingFirst();

        // Then
        assertNotNull("Results should not be null", results);
        assertEquals("Should find 2 Charles matches", 2, results.size());
    }

    @Test
    public void testRepository_refreshPlaques() {
        // Given - repository
        assertNotNull("Repository should exist", repository);

        // When - trigger refresh
        repository.refreshPlaques().blockingAwait();

        // Then - should complete without error
        int count = plaqueDao.getPlaqueCount().blockingGet();
        assertTrue("Count should be non-negative after refresh", count >= 0);
    }

    @Test
    public void testDAO_deleteAllPlaques() {
        // Given - database with data
        List<PlaqueEntity> testEntities = createTestEntities(5);
        plaqueDao.insertPlaques(testEntities).blockingAwait();
        assertEquals(5, plaqueDao.getPlaqueCount().blockingGet().intValue());

        // When - delete all
        plaqueDao.deleteAllPlaques().blockingAwait();

        // Then
        assertEquals(0, plaqueDao.getPlaqueCount().blockingGet().intValue());
    }

    @Test
    public void testDAO_searchByName() {
        // Given
        List<PlaqueEntity> testEntities = new ArrayList<>();
        testEntities.add(createEntityWithName("1", "Test Person One"));
        testEntities.add(createEntityWithName("2", "Test Person Two"));
        testEntities.add(createEntityWithName("3", "Different Name"));
        plaqueDao.insertPlaques(testEntities).blockingAwait();

        // When
        List<PlaqueEntity> results = plaqueDao.searchPlaquesByName("Person").blockingFirst();

        // Then
        assertNotNull(results);
        assertEquals(2, results.size());
    }

    @Test
    public void testDAO_getPlaqueById() {
        // Given
        List<PlaqueEntity> testEntities = createTestEntities(3);
        plaqueDao.insertPlaques(testEntities).blockingAwait();

        // When
        PlaqueEntity result = plaqueDao.getPlaqueById("test-1").blockingGet();

        // Then
        assertNotNull("Should find plaque by ID", result);
        assertEquals("Should find correct plaque", "test-1", result.getId());
    }

    @Test
    public void testDAO_getPlaquesInBounds() {
        // Given - plaques at different locations
        List<PlaqueEntity> testEntities = new ArrayList<>();
        testEntities.add(createEntityAtLocation("1", "London", 51.5, -0.1));
        testEntities.add(createEntityAtLocation("2", "Far Away", 55.0, -3.0));
        testEntities.add(createEntityAtLocation("3", "Also London", 51.52, -0.12));
        plaqueDao.insertPlaques(testEntities).blockingAwait();

        // When - get London bounds
        List<PlaqueEntity> results = plaqueDao.getPlaquesInBounds(51.4, 51.6, -0.2, 0.0)
                .blockingFirst();

        // Then
        assertNotNull(results);
        assertEquals("Should find 2 London plaques", 2, results.size());
    }

    @Test
    public void testDAO_replaceStrategy() {
        // Given - insert original
        PlaqueEntity original = createEntityWithName("1", "Original Name");
        List<PlaqueEntity> list = new ArrayList<>();
        list.add(original);
        plaqueDao.insertPlaques(list).blockingAwait();

        // When - insert with same ID
        PlaqueEntity updated = createEntityWithName("1", "Updated Name");
        List<PlaqueEntity> updateList = new ArrayList<>();
        updateList.add(updated);
        plaqueDao.insertPlaques(updateList).blockingAwait();

        // Then - should replace, not duplicate
        assertEquals(1, plaqueDao.getPlaqueCount().blockingGet().intValue());
        PlaqueEntity result = plaqueDao.getPlaqueById("1").blockingGet();
        assertEquals("Updated Name", result.getName());
    }

    @Test
    public void testDataPersistence_acrossOperations() {
        // Given
        List<PlaqueEntity> testEntities = createTestEntities(3);
        plaqueDao.insertPlaques(testEntities).blockingAwait();

        // When - perform multiple operations
        int count1 = plaqueDao.getPlaqueCount().blockingGet();
        List<PlaqueEntity> all = plaqueDao.getAllPlaquesOnce().blockingGet();
        PlaqueEntity byId = plaqueDao.getPlaqueById("test-0").blockingGet();

        // Then - all should work
        assertEquals(3, count1);
        assertEquals(3, all.size());
        assertNotNull(byId);
    }

    // Helper methods

    private List<PlaqueEntity> createTestEntities(int count) {
        List<PlaqueEntity> entities = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            entities.add(createEntityAtLocation(
                    "test-" + i,
                    "Test Person " + i,
                    51.5 + (i * 0.01),
                    -0.1 + (i * 0.01)
            ));
        }
        return entities;
    }

    private PlaqueEntity createEntityAtLocation(String id, String name, double lat, double lng) {
        PlaqueEntity entity = new PlaqueEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setTitle(name + " (1800-1900)");
        entity.setFeatureDescription(name + "<br>Test<br>Address");
        entity.setLatitude(lat);
        entity.setLongitude(lng);
        entity.setAddress("Test Address");
        entity.setOccupation("Test Occupation");
        entity.setNote("Test Note");
        return entity;
    }

    private PlaqueEntity createEntityWithName(String id, String name) {
        return createEntityAtLocation(id, name, 51.5, -0.1);
    }
}
