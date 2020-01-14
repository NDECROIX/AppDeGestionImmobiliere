package com.openclassrooms.realestatemanager.database.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test the photo data access object
 */
@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class PhotoDaoTest {

    private AppDatabase database;

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Init the database to perform test
     */
    @Before
    public void initDb() {
        this.database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .addCallback(AppDatabase.prePopulateDatabase())
                .build();
    }

    @After
    public void closeDb() {
        database.close();
    }

    /**
     * Create a property to perform test
     *
     * @return Property test
     */
    @DataPoint
    public static Property createProperty() {
        Property property = new Property();
        property.setType("test");
        property.setRooms(10);
        property.setPrice(10d);
        property.setStreetNumber(10);
        property.setStreetName("test");
        property.setZip(10);
        property.setCity("test");
        property.setEntryDate(10);
        property.setId(property.getHash());
        return property;
    }

    /**
     * Create a photo to perform test
     *
     * @return Photo test
     */
    @DataPoint
    public static Photo createPhoto(String propertyId) {
        return new Photo("test", propertyId, "test");
    }

    /**
     * Test to insert a photo in the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void insertPhotoTest() throws Exception {
        Property propertyTest = createProperty();
        Photo photoTest = createPhoto(propertyTest.getId());
        this.database.propertyDAO().insertProperty(propertyTest);
        this.database.photoDAO().insertPhoto(photoTest);
        List<Photo> photos = LiveDataTestUtil.getValue(this.database.photoDAO().getPropertyPhotos(photoTest.getPropertyID()));
        assertEquals("Insert photo in the local database", photos.get(0), photoTest);
    }

    /**
     * Test to get photos from the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void getPhotosTest() throws Exception {
        Property propertyTest = createProperty();
        Photo photoTest = createPhoto(propertyTest.getId());
        this.database.propertyDAO().insertProperty(propertyTest);
        this.database.photoDAO().insertPhoto(photoTest);
        List<Photo> photos = LiveDataTestUtil.getValue(this.database.photoDAO().getPhotos());
        assertFalse("Get photos from the local database", photos.isEmpty());
    }
}