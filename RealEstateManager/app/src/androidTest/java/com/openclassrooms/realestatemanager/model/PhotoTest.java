package com.openclassrooms.realestatemanager.model;

import android.content.Context;
import android.os.Environment;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Perform tests on the photo class
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PhotoTest {

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Test to get the uri from a photo
     */
    @Test
    public void getUri(){
        Context context = ApplicationProvider.getApplicationContext();
        String expected = "";
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null) {
            expected = file.getAbsolutePath();
            expected += "/name.jpg";
        }
        Photo photo = new Photo("name.jpg", "propertyId", "desc");
        String result = photo.getUri(context);
        assertEquals(expected, result);
    }

}
