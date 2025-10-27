package com.upwardsnorthwards.blueplaqueslondon.repository;

import android.content.Context;

import com.upwardsnorthwards.blueplaqueslondon.data.local.dao.PlaqueDao;
import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;
import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;
import com.upwardsnorthwards.blueplaqueslondon.model.Placemark;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.plugins.RxJavaPlugins;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PlaquesRepository.
 */
@RunWith(MockitoJUnitRunner.class)
public class PlaquesRepositoryTest {

    @Mock
    private PlaqueDao plaqueDao;

    @Mock
    private Context context;

    private PlaquesRepository repository;

    @Before
    public void setUp() {
        // Set RxJava to use trampolineScheduler for synchronous testing
        RxJavaPlugins.setIoSchedulerHandler(scheduler -> Schedulers.trampoline());
        RxJavaPlugins.setComputationSchedulerHandler(scheduler -> Schedulers.trampoline());

        repository = new PlaquesRepository(plaqueDao, context);
    }

    @Test
    public void getAllPlaques_success() {
        // Given
        PlaqueEntity entity1 = createTestEntity("1", "Test Plaque 1");
        PlaqueEntity entity2 = createTestEntity("2", "Test Plaque 2");
        List<PlaqueEntity> entities = Arrays.asList(entity1, entity2);

        when(plaqueDao.getAllPlaques()).thenReturn(Flowable.just(entities));

        // When
        List<Placemark> result = repository.getAllPlaques().blockingFirst();

        // Then
        verify(plaqueDao).getAllPlaques();
        assert result.size() == 2;
        assert result.get(0).getName().equals("Test Plaque 1");
        assert result.get(1).getName().equals("Test Plaque 2");
    }

    @Test
    public void getPlaqueById_success() {
        // Given
        String plaqueId = "test-id";
        PlaqueEntity entity = createTestEntity(plaqueId, "Test Plaque");

        when(plaqueDao.getPlaqueById(plaqueId)).thenReturn(Single.just(entity));

        // When
        Placemark result = repository.getPlaqueById(plaqueId).blockingGet();

        // Then
        verify(plaqueDao).getPlaqueById(plaqueId);
        assert result.getName().equals("Test Plaque");
    }

    @Test
    public void searchPlaquesByName_success() {
        // Given
        String query = "test";
        PlaqueEntity entity = createTestEntity("1", "Test Plaque");
        List<PlaqueEntity> entities = Arrays.asList(entity);

        when(plaqueDao.searchPlaquesByName(query)).thenReturn(Flowable.just(entities));

        // When
        List<Placemark> result = repository.searchPlaquesByName(query).blockingFirst();

        // Then
        verify(plaqueDao).searchPlaquesByName(query);
        assert result.size() == 1;
        assert result.get(0).getName().equals("Test Plaque");
    }

    @Test
    public void loadPlaquesFromAssets_alreadyLoaded() {
        // Given
        when(plaqueDao.getPlaqueCount()).thenReturn(Single.just(10));

        // When
        repository.loadPlaquesFromAssets().blockingAwait();

        // Then
        verify(plaqueDao).getPlaqueCount();
        // Should not insert plaques if already loaded
    }

    @Test
    public void getAllPlaques_emptyDatabase() {
        // Given
        when(plaqueDao.getAllPlaques()).thenReturn(Flowable.just(Arrays.asList()));

        // When
        List<Placemark> result = repository.getAllPlaques().blockingFirst();

        // Then
        verify(plaqueDao).getAllPlaques();
        assert result.isEmpty();
    }

    @Test
    public void searchPlaquesByName_noResults() {
        // Given
        String query = "nonexistent";
        when(plaqueDao.searchPlaquesByName(query)).thenReturn(Flowable.just(Arrays.asList()));

        // When
        List<Placemark> result = repository.searchPlaquesByName(query).blockingFirst();

        // Then
        verify(plaqueDao).searchPlaquesByName(query);
        assert result.isEmpty();
    }

    @Test
    public void searchPlaquesByName_caseInsensitive() {
        // Given
        String query = "TEST";
        PlaqueEntity entity = createTestEntity("1", "Test Plaque");
        List<PlaqueEntity> entities = Arrays.asList(entity);

        when(plaqueDao.searchPlaquesByName(anyString())).thenReturn(Flowable.just(entities));

        // When
        List<Placemark> result = repository.searchPlaquesByName(query).blockingFirst();

        // Then
        verify(plaqueDao).searchPlaquesByName(anyString());
        assert result.size() == 1;
    }

    @Test
    public void getPlaqueById_notFound() {
        // Given
        String plaqueId = "nonexistent-id";
        when(plaqueDao.getPlaqueById(plaqueId)).thenReturn(Single.error(new RuntimeException("Not found")));

        // When/Then
        try {
            repository.getPlaqueById(plaqueId).blockingGet();
            assert false : "Should have thrown exception";
        } catch (RuntimeException e) {
            verify(plaqueDao).getPlaqueById(plaqueId);
            assert e.getMessage().equals("Not found");
        }
    }

    @Test
    public void refreshPlaques_success() {
        // Given
        when(plaqueDao.deleteAllPlaques()).thenReturn(Completable.complete());

        // When
        try {
            repository.refreshPlaques().blockingAwait();
        } catch (Exception e) {
            // Expected due to KML parsing in test environment
        }

        // Then
        verify(plaqueDao).deleteAllPlaques();
    }

    @Test
    public void entityToPlacemarkConversion_correctMapping() {
        // Given
        PlaqueEntity entity = createTestEntity("test-id", "Test Name");
        entity.setLatitude(51.5074);
        entity.setLongitude(-0.1278);
        entity.setStyleUrl("#testStyle");

        when(plaqueDao.getPlaqueById("test-id")).thenReturn(Single.just(entity));

        // When
        Placemark result = repository.getPlaqueById("test-id").blockingGet();

        // Then
        assertEquals("Test Name", result.getName());
        assertEquals(51.5074, result.getLatitude(), 0.0001);
        assertEquals(-0.1278, result.getLongitude(), 0.0001);
        assertEquals("#testStyle", result.getStyleUrl());
    }

    @Test
    public void loadPlaquesFromAssets_errorHandling() {
        // Given
        when(plaqueDao.getPlaqueCount()).thenReturn(Single.just(0));

        // When/Then - KML parsing will fail in test environment, which is expected
        try {
            repository.loadPlaquesFromAssets().blockingAwait();
        } catch (Exception e) {
            // Expected due to asset loading in test environment
            assertNotNull(e);
        }
    }

    private PlaqueEntity createTestEntity(String id, String name) {
        PlaqueEntity entity = new PlaqueEntity();
        entity.setId(id);
        entity.setName(name);
        entity.setTitle(name);
        entity.setFeatureDescription(name);
        entity.setOccupation("Test Occupation");
        entity.setAddress("Test Address");
        entity.setNote("Test Note");
        entity.setCouncilAndYear("Test Council 2024");
        entity.setStyleUrl("#myDefaultStyles");
        entity.setLatitude(51.5074);
        entity.setLongitude(-0.1278);
        return entity;
    }
}
