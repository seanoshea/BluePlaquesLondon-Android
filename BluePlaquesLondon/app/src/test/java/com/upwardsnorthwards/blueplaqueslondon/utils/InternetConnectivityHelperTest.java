package com.upwardsnorthwards.blueplaqueslondon.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.test.core.app.ApplicationProvider;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.shadows.ShadowConnectivityManager;
import org.robolectric.shadows.ShadowNetworkInfo;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.robolectric.Shadows.shadowOf;

@RunWith(RobolectricTestRunner.class)
public class InternetConnectivityHelperTest {

    @Mock
    private InternetConnectivityHelperDelegate delegate;

    private Context context;
    private ConnectivityManager connectivityManager;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        context = ApplicationProvider.getApplicationContext();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Test
    public void hasInternetConnectivity_withConnectedNetwork_shouldReturnTrue() {
        ShadowConnectivityManager shadowConnectivityManager = shadowOf(connectivityManager);
        shadowConnectivityManager.setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, ConnectivityManager.TYPE_WIFI, 0, true, NetworkInfo.State.CONNECTED));

        InternetConnectivityHelper helper = new InternetConnectivityHelper(context);
        helper.setDelegate(delegate);
        helper.onResume();

        verify(delegate).internetConnectivityUpdated(true);
    }

    @Test
    public void hasInternetConnectivity_withDisconnectedNetwork_shouldReturnFalse() {
        ShadowConnectivityManager shadowConnectivityManager = shadowOf(connectivityManager);
        shadowConnectivityManager.setActiveNetworkInfo(ShadowNetworkInfo.newInstance(null, ConnectivityManager.TYPE_WIFI, 0, true, NetworkInfo.State.DISCONNECTED));

        InternetConnectivityHelper helper = new InternetConnectivityHelper(context);
        helper.setDelegate(delegate);
        helper.onResume();

        verify(delegate).internetConnectivityUpdated(false);
    }
}
