package com.openclassrooms.realestatemanager.database.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.realestatemanager.database.AppDatabase;
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
 * Test the property data access object
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PropertyDaoTest {

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
    public static Property createTestProperty() {
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
     * Test to add a property in the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void insertPropertyTest() throws Exception {
        Property propertyTest = createTestProperty();
        this.database.propertyDAO().insertProperty(propertyTest);
        Property property = LiveDataTestUtil.getValue(this.database.propertyDAO().getProperty(propertyTest.getId()));
        assertEquals("Insert property in the local database", propertyTest, property);
    }

    /**
     * Test to get properties from the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void getPropertiesTest() throws Exception {
        Property propertyTest = createTestProperty();
        this.database.propertyDAO().insertProperty(propertyTest);
        List<Property> properties = LiveDataTestUtil.getValue(this.database.propertyDAO().getProperties());
        assertFalse("Get properties from the local database", properties.isEmpty());
    }
}