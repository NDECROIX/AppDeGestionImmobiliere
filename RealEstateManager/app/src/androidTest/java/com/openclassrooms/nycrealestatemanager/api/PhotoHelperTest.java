package com.openclassrooms.nycrealestatemanager.api;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.nycrealestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.nycrealestatemanager.model.Photo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onIdle;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test PhotoHelper
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PhotoHelperTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, false, true);

    /**
     * Required for asynchronous task
     */
    private final CountingIdlingResource callOnApiIdl = new CountingIdlingResource("Call on API IdlingResource");

    private Photo photo;
    private boolean taskIsSuccessful;

    @Before
    public void setup() {
        MainActivity activity = rule.getActivity();
        assertNotNull(activity);
        photo = createPhoto();
        IdlingRegistry.getInstance().register(callOnApiIdl);
    }

    /**
     * Unregister on the IdlingResource
     */
    @After
    public void setDown() {
        IdlingRegistry.getInstance().unregister(callOnApiIdl);
    }

    @DataPoint
    public Photo createPhoto() {
        return new Photo("test", "test", "test");
    }

    /**
     * Add a photo in Firebase database
     */
    @DataPoint
    public void addPhotoInFirebase() {
        callOnApiIdl.increment();
        PhotoHelper.updatePhoto(photo.getHash(), photo).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Delete a photo from firebase database
     */
    @DataPoint
    public void deletePhotoFromFirebase() {
        callOnApiIdl.increment();
        PhotoHelper.deletePhoto(photo.getHash()).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Test to add a photo to the firebase database
     */
    @Test
    public void addPhotoTest() {
        addPhotoInFirebase();
        assertTrue("Add photo in Firebase database", taskIsSuccessful);
        deletePhotoFromFirebase();
    }

    /**
     * Test to delete a photo from firebase database
     */
    @Test
    public void deletePhotoTest() {
        addPhotoInFirebase();
        deletePhotoFromFirebase();
        assertTrue("Delete a photo from the Firebase database", taskIsSuccessful);
    }

    /**
     * Retrieves à photo from firebase database
     */
    @Test
    public void getPhotoFromFirebase() {
        addPhotoInFirebase();
        callOnApiIdl.increment();
        PhotoHelper.getPhoto(photo.getHash()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Photo photo = task.getResult().toObject(Photo.class);
                if (photo != null && photo.equals(this.photo)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get a photo from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deletePhotoFromFirebase();
    }

    /**
     * Get photos from firebase database
     */
    @Test
    public void getPhotosFromFirebase() {
        addPhotoInFirebase();
        callOnApiIdl.increment();
        PhotoHelper.getPhotos().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Photo> photos = task.getResult().toObjects(Photo.class);
                if (!photos.isEmpty() && photos.contains(this.photo)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get photos from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deletePhotoFromFirebase();
    }
}