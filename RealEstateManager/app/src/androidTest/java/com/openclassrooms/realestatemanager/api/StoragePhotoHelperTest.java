package com.openclassrooms.realestatemanager.api;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.utils.UtilsPhoto;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static androidx.test.espresso.Espresso.onIdle;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test StoragePhotoHelper
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class StoragePhotoHelperTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, false, true);

    /**
     * Required for asynchronous task
     */
    private final CountingIdlingResource callOnApiIdl = new CountingIdlingResource("Call on API IdlingResource");

    private boolean taskIsSuccessful;
    private MainActivity activity;
    private String uriTest;

    @Before
    public void setup() {
        activity = rule.getActivity();
        assertNotNull(activity);
        uriTest = getImageUri();
        IdlingRegistry.getInstance().register(callOnApiIdl);
    }

    /**
     * Unregister on the IdlingResource
     */
    @After
    public void setDown() {
        IdlingRegistry.getInstance().unregister(callOnApiIdl);
        deleteImageFromUri();
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
            String uri = MediaStore.Images.Media.insertImage(activity.getContentResolver(), inImage, "imageTest", null);
            // Move it in app file
            newPath = UtilsPhoto.saveBitmapToThePath(activity, Uri.parse(uri));
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
    public void deleteImageFromUri() {
        try {
            Files.deleteIfExists(Paths.get(uriTest));
        } catch (IOException e) {
            e.printStackTrace();
            fail("Fail to delete the photo from the path");
        }
    }

    /**
     * Add a image to firebase storage
     */
    @DataPoint
    public void putFileOnFirebaseStorage() {
        callOnApiIdl.increment();
        StoragePhotoHelper.putFileOnFirebaseStorage("test", "test", uriTest).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Remove a image from firebase storage
     */
    @DataPoint
    public void removeFileFromFirebaseStorage() {
        callOnApiIdl.increment();
        StoragePhotoHelper.deleteFileFromFirebaseStorage("test", "test")
                .addOnCompleteListener(task -> {
                    taskIsSuccessful = task.isSuccessful();
                    callOnApiIdl.decrement();
                });
        onIdle();
    }

    /**
     * Test putFileOnFirebaseStorage()
     * Add a file in firebase storage
     */
    @Test
    public void putFileOnFirebaseStorageTest() {
        putFileOnFirebaseStorage();
        assertTrue("Add file to FirebaseStorage ", taskIsSuccessful);
        removeFileFromFirebaseStorage();
    }

    /**
     * Test removeFileFromFirebaseStorage()
     * Remove a file from firebase storage
     */
    @Test
    public void removeFileFromFirebaseStorageTest() {
        putFileOnFirebaseStorage();
        removeFileFromFirebaseStorage();
        assertTrue("Remove file from FirebaseStorage ", taskIsSuccessful);
    }

    /**
     * Test save file from an url.
     */
    @Test
    public void savePictureFromUrlTest() throws IOException {
        putFileOnFirebaseStorage();
        callOnApiIdl.increment();
        String path = UtilsPhoto.createImageFile(activity).getAbsolutePath();
        StoragePhotoHelper.savePictureToFile("test", "test", path).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            if (taskIsSuccessful) {
                try {
                    Files.deleteIfExists(Paths.get(path));
                } catch (IOException e) {
                    e.printStackTrace();
                    fail("Fail to delete the photo");
                }
            }
            callOnApiIdl.decrement();
        });
        onIdle();
        assertTrue("Fail to get an url from file in Firebase database", taskIsSuccessful);
        removeFileFromFirebaseStorage();
    }
}