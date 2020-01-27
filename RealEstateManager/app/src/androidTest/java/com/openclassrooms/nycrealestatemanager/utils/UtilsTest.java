package com.openclassrooms.nycrealestatemanager.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.google.android.gms.maps.model.LatLng;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;

/**
 * Test utils class
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class UtilsTest {

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Test to get a LatLng from an address.
     */
    @Test
    public void getLatLngFromAddress(){
        Context context = ApplicationProvider.getApplicationContext();
        LatLng expected = new LatLng(38.8976633, -77.0365739);
        LatLng latLng = Utils.getLocationFromAddress(context, "1600, Pennsylvania Avenue NW\n" +
                "Washington DC 20500");
        assertEquals(expected, latLng);
    }

    /**
     * Test if internet is available
     */
    @Test
    public void checkInternet(){
        Context context = ApplicationProvider.getApplicationContext();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        boolean expected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        boolean result = Utils.isInternetAvailable(context);
        assertEquals(expected, result);
    }
}