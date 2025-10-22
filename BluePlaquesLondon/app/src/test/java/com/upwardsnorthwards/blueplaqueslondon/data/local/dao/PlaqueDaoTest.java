package com.upwardsnorthwards.blueplaqueslondon.data.local.dao;

import com.upwardsnorthwards.blueplaqueslondon.data.local.entity.PlaqueEntity;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PlaqueDao.
 * Since PlaqueDao is a Room interface, we test the interface contract and verify method signatures.
 */
public class PlaqueDaoTest {

    @Test
    public void testGetAllPlaques() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        List<PlaqueEntity> mockEntities = createMockEntities();
        when(dao.getAllPlaques()).thenReturn(Flowable.just(mockEntities));

        // When
        Flowable<List<PlaqueEntity>> result = dao.getAllPlaques();

        // Then
        assertNotNull(result);
        verify(dao).getAllPlaques();
    }

    @Test
    public void testGetAllPlaquesOnce() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        List<PlaqueEntity> mockEntities = createMockEntities();
        when(dao.getAllPlaquesOnce()).thenReturn(Single.just(mockEntities));

        // When
        Single<List<PlaqueEntity>> result = dao.getAllPlaquesOnce();

        // Then
        assertNotNull(result);
        verify(dao).getAllPlaquesOnce();
    }

    @Test
    public void testGetPlaqueById() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        PlaqueEntity mockEntity = createMockEntity();
        when(dao.getPlaqueById(anyString())).thenReturn(Single.just(mockEntity));

        // When
        Single<PlaqueEntity> result = dao.getPlaqueById("test-id");

        // Then
        assertNotNull(result);
        verify(dao).getPlaqueById("test-id");
    }

    @Test
    public void testSearchPlaquesByName() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        List<PlaqueEntity> mockEntities = createMockEntities();
        when(dao.searchPlaquesByName(anyString())).thenReturn(Flowable.just(mockEntities));

        // When
        Flowable<List<PlaqueEntity>> result = dao.searchPlaquesByName("Darwin");

        // Then
        assertNotNull(result);
        verify(dao).searchPlaquesByName("Darwin");
    }

    @Test
    public void testGetPlaquesInBounds() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        List<PlaqueEntity> mockEntities = createMockEntities();
        when(dao.getPlaquesInBounds(anyDouble(), anyDouble(), anyDouble(), anyDouble()))
                .thenReturn(Flowable.just(mockEntities));

        // When
        Flowable<List<PlaqueEntity>> result = dao.getPlaquesInBounds(51.0, 52.0, -1.0, 0.0);

        // Then
        assertNotNull(result);
        verify(dao).getPlaquesInBounds(51.0, 52.0, -1.0, 0.0);
    }

    @Test
    public void testInsertPlaque() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        PlaqueEntity mockEntity = createMockEntity();
        when(dao.insertPlaque(mockEntity)).thenReturn(Completable.complete());

        // When
        Completable result = dao.insertPlaque(mockEntity);

        // Then
        assertNotNull(result);
        verify(dao).insertPlaque(mockEntity);
    }

    @Test
    public void testInsertPlaques() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        List<PlaqueEntity> mockEntities = createMockEntities();
        when(dao.insertPlaques(anyList())).thenReturn(Completable.complete());

        // When
        Completable result = dao.insertPlaques(mockEntities);

        // Then
        assertNotNull(result);
        verify(dao).insertPlaques(mockEntities);
    }

    @Test
    public void testDeleteAllPlaques() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        when(dao.deleteAllPlaques()).thenReturn(Completable.complete());

        // When
        Completable result = dao.deleteAllPlaques();

        // Then
        assertNotNull(result);
        verify(dao).deleteAllPlaques();
    }

    @Test
    public void testGetPlaqueCount() {
        // Given
        PlaqueDao dao = mock(PlaqueDao.class);
        when(dao.getPlaqueCount()).thenReturn(Single.just(100));

        // When
        Single<Integer> result = dao.getPlaqueCount();

        // Then
        assertNotNull(result);
        verify(dao).getPlaqueCount();
    }

    private PlaqueEntity createMockEntity() {
        PlaqueEntity entity = new PlaqueEntity();
        entity.setId("test-id");
        entity.setName("Test Plaque");
        entity.setTitle("Test Plaque");
        entity.setFeatureDescription("Test Description");
        entity.setOccupation("Test Occupation");
        entity.setAddress("Test Address");
        entity.setNote("Test Note");
        entity.setCouncilAndYear("Test Council 2024");
        entity.setStyleUrl("#myDefaultStyles");
        entity.setLatitude(51.5074);
        entity.setLongitude(-0.1278);
        return entity;
    }

    private List<PlaqueEntity> createMockEntities() {
        return Arrays.asList(createMockEntity(), createMockEntity());
    }
}
