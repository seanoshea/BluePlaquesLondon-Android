package com.upwardsnorthwards.blueplaqueslondon.workers;

import android.content.Context;

import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;

import com.upwardsnorthwards.blueplaqueslondon.data.repository.PlaquesRepository;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import io.reactivex.rxjava3.core.Completable;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PlaqueSyncWorker.
 */
@RunWith(RobolectricTestRunner.class)
public class PlaqueSyncWorkerTest {

    private Context context;

    @Mock
    private WorkerParameters mockWorkerParams;

    private PlaquesRepository mockPlaquesRepository;

    private PlaqueSyncWorker worker;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        mockWorkerParams = mock(WorkerParameters.class);
        mockPlaquesRepository = mock(PlaquesRepository.class);
        worker = new PlaqueSyncWorker(context, mockWorkerParams, mockPlaquesRepository);
    }

    @Test
    public void doWork_success() {
        // Given
        when(mockPlaquesRepository.refreshPlaques()).thenReturn(Completable.complete());

        // When
        ListenableWorker.Result result = worker.doWork();

        // Then
        verify(mockPlaquesRepository).refreshPlaques();
        assertEquals(ListenableWorker.Result.success(), result);
    }

    @Test
    public void doWork_failure_shouldRetry() {
        // Given
        when(mockPlaquesRepository.refreshPlaques())
                .thenReturn(Completable.error(new Exception("Network error")));

        // When
        ListenableWorker.Result result = worker.doWork();

        // Then
        verify(mockPlaquesRepository).refreshPlaques();
        assertEquals(ListenableWorker.Result.retry(), result);
    }

    @Test
    public void doWork_runtimeException_shouldRetry() {
        // Given
        when(mockPlaquesRepository.refreshPlaques())
                .thenReturn(Completable.error(new RuntimeException("Unexpected error")));

        // When
        ListenableWorker.Result result = worker.doWork();

        // Then
        verify(mockPlaquesRepository).refreshPlaques();
        assertEquals(ListenableWorker.Result.retry(), result);
    }
}
