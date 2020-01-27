package com.openclassrooms.nycrealestatemanager.database.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.nycrealestatemanager.database.AppDatabase;
import com.openclassrooms.nycrealestatemanager.model.PoiNextProperty;
import com.openclassrooms.nycrealestatemanager.model.Property;
import com.openclassrooms.nycrealestatemanager.utils.LiveDataTestUtil;

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
 * Test the point of interest data access object
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PoiNextPropertyDaoTest {

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
    private Property createProperty() {
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
     * Create a PoiNextProperty to perform test
     *
     * @return PoiNextProperty test
     */
    @DataPoint
    public static PoiNextProperty createPoiNextProperty(String propertyID) {
        return new PoiNextProperty(propertyID, "Gym");
    }

    /**
     * Test to insert a PoiNextProperty in the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void insertPoiNextPropertyTest() throws Exception {
        Property propertyTest = createProperty();
        PoiNextProperty poiNextPropertyTest = createPoiNextProperty(propertyTest.getId());
        this.database.propertyDAO().insertProperty(propertyTest);
        this.database.poiNextPropertyDAO().insertPoiNextProperty(poiNextPropertyTest);
        List<PoiNextProperty> PoisNextProperties = LiveDataTestUtil
                .getValue(this.database.poiNextPropertyDAO().getPoisNextProperty(poiNextPropertyTest.getPropertyID()));
        assertEquals("Insert point of interest in the local database", PoisNextProperties.get(0), poiNextPropertyTest);
    }

    /**
     * Test to get points of interest nex properties from the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void getPoisNextPropertiesTest() throws Exception {
        Property propertyTest = createProperty();
        PoiNextProperty poiNextPropertyTest = createPoiNextProperty(propertyTest.getId());
        this.database.propertyDAO().insertProperty(propertyTest);
        this.database.poiNextPropertyDAO().insertPoiNextProperty(poiNextPropertyTest);
        List<PoiNextProperty> PoisNextProperties = LiveDataTestUtil
                .getValue(this.database.poiNextPropertyDAO().getPoisNextProperties());
        assertFalse("Get points of interest next properties from the local database", PoisNextProperties.isEmpty());
    }
}