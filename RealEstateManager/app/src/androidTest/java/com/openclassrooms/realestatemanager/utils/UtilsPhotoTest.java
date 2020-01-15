package com.openclassrooms.realestatemanager.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;

/**
 * Test utils photo class
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class UtilsPhotoTest {

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private Context context;

    @Before
    public void setup() {
        context = ApplicationProvider.getApplicationContext();
        assertNotNull(context);
    }

    /**
     * Create uri to perform test
     *
     * @return Image path
     */
    @DataPoint
    public String getImageUri() {
        String newPath = "";
        try {
            // Create a bitmap
            Bitmap inImage = Bitmap.createBitmap(100, 100, Bitmap.Config.RGB_565);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            // Save it in public device
            String uri = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "imageTest", null);
            // Move it in app file
            newPath = UtilsPhoto.saveBitmapToThePath(context, Uri.parse(uri));
            // Delete from public device
            Files.deleteIfExists(Paths.get(uri));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Fail to generate path");
        }
        assertFalse("Fail to generate path", newPath.isEmpty());
        return newPath;
    }

    /**
     * Delete image after test
     */
    @DataPoint
    public void deleteImageFromUri(String uri) {
        try {
            Files.deleteIfExists(Paths.get(uri));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Fail to delete the photo from the path");
        }
    }

    /**
     * Test to create a file
     *
     * @throws Exception Create file
     */
    @Test
    public void createImageFile() throws Exception {
        Context context = ApplicationProvider.getApplicationContext();
        File expected = UtilsPhoto.createImageFile(context);
        assertTrue(expected.exists());
    }

    /**
     * Test to move bitmap in the application package
     */
    @Test
    public void saveBitmapToThePath() {
        Context context = ApplicationProvider.getApplicationContext();
        String uriTest = getImageUri();
        String newPath = UtilsPhoto.saveBitmapToThePath(context, Uri.parse(uriTest));
        assertTrue(Files.exists(Paths.get(newPath)));
        deleteImageFromUri(uriTest);
    }
}