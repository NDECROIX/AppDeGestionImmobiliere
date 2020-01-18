package com.openclassrooms.realestatemanager.viewmodel;


import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.ViewModelProviders;
import androidx.test.annotation.UiThreadTest;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.model.Filter;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collections;
import java.util.List;

import static androidx.test.espresso.Espresso.onIdle;
import static com.openclassrooms.realestatemanager.database.dao.AgentDaoTest.createTestAgent;
import static com.openclassrooms.realestatemanager.database.dao.PhotoDaoTest.createPhoto;
import static com.openclassrooms.realestatemanager.database.dao.PhotoDaoTest.createProperty;
import static com.openclassrooms.realestatemanager.database.dao.PoiNextPropertyDaoTest.createPoiNextProperty;
import static com.openclassrooms.realestatemanager.database.dao.PropertyDaoTest.createTestProperty;
import static junit.framework.Assert.fail;
import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test property view model
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PropertyViewModelTest {
    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, true, true);

    /**
     * Required for asynchronous task
     */
    private final static CountingIdlingResource callOnApiIdl = new CountingIdlingResource(PropertyViewModelTest.class.getName());

    private PropertyViewModel viewModel;

    /**
     * Set up the view model and the local database
     * Init idlingResource to wait asyncTask
     */
    @Before
    public void setUp() {
        IdlingRegistry.getInstance().register(callOnApiIdl);
        assertFalse("Stop internet before perform test", Utils.isInternetAvailable(rule.getActivity()));
        this.rule.getActivity().deleteDatabase("DatabaseREM.db");
        this.viewModel = ViewModelProviders.of(this.rule.getActivity()).get(PropertyViewModel.class);
    }

    /**
     * Close database for memory leaks.
     * Unregister IdlingResource after tests
     */
    @After
    public void closeDb() {
        AppDatabase.getInstance(this.rule.getActivity()).close();
        IdlingRegistry.getInstance().unregister(callOnApiIdl);
    }

    /**
     * Test set and get current property
     */
    @Test
    @UiThreadTest
    public void setGetCurrentProperty() {
        Property property = createTestProperty();
        this.viewModel.setCurrentProperty(property);
        Property propertyResult = this.viewModel.getCurrentProperty(rule.getActivity()).getValue();
        assertEquals(property, propertyResult);
    }

    /**
     * Test set and get current filter
     */
    @Test
    @UiThreadTest
    public void setGetCurrentFilter() {
        Filter filter = new Filter();
        filter.setType("test");
        this.viewModel.setCurrentFilter(filter);
        Filter filterResult = this.viewModel.getCurrentFilter().getValue();
        assertEquals(filter, filterResult);
    }

    /**
     * Insert property in the local database from the view model
     */
    @Test
    public void insertProperty() {
        Property propertyTest = createTestProperty();
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getProperties().observe(rule.getActivity(), properties -> {
            assertTrue("Insert properties in the local database", properties.contains(propertyTest));
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Retrieve properties from the local database by the view model
     */
    @Test
    public void getProperties() {
        Property propertyTest = createTestProperty();
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getProperties().observe(rule.getActivity(), properties -> {
            assertNotNull("Get properties from the local database", properties);
            assertFalse(properties.isEmpty());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Update the property in the local database from the view model
     */
    @Test
    public void updateProperty() {
        Property propertyTest = createTestProperty();
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        propertyTest.setPrice(666D);
        this.viewModel.updateProperty(propertyTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getProperties().observe(rule.getActivity(), properties -> {
            assertEquals("Update properties from the local database", properties.get(0).getPrice(), propertyTest.getPrice());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Insert a property photo in the local database from the view model
     */
    @Test
    public void insertPropertyPhoto() {
        Property propertyTest = createProperty();
        Photo photoTest = createPhoto(propertyTest.getId());
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        this.viewModel.insertPropertyPhoto(photoTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getPropertyPhotos(propertyTest.getId()).observe(rule.getActivity(), photos -> {
            assertEquals("Insert photo in the local database", photos.get(0), photoTest);
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Get the property photos from the local database by the view model
     */
    @Test
    public void getPropertyPhotos() {
        Property propertyTest = createProperty();
        Photo photoTest = createPhoto(propertyTest.getId());
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        this.viewModel.insertPropertyPhoto(photoTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getPropertyPhotos(propertyTest.getId()).observe(rule.getActivity(), photos -> {
            assertNotNull("Get photos from the local database", photos);
            assertFalse(photos.isEmpty());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Delete a photo from the local database by the view model
     */
    @Test
    public void deletePhoto() {
        Property propertyTest = createProperty();
        Photo photoTest = createPhoto(propertyTest.getId());
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        this.viewModel.insertPropertyPhoto(photoTest);
        onIdle();
        this.viewModel.deletePhoto(photoTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getPropertyPhotos(propertyTest.getId()).observe(rule.getActivity(), photos -> {
            assertTrue("Delete a photo from the local database", photos.isEmpty());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * insert a point of interest next a property in the local database from the view model
     */
    @Test
    public void insertPoiNextPropertyTest() {
        Property propertyTest = createProperty();
        PoiNextProperty poiNextPropertyTest = createPoiNextProperty(propertyTest.getId());
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        this.viewModel.insertPoiNextProperty(poiNextPropertyTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getPoisNextProperty(propertyTest.getId()).observe(rule.getActivity(), pois -> {
            assertEquals("Insert a point of interest next a property in the local database", pois.get(0),
                    poiNextPropertyTest);
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Retrieves points of interest next a property from the local database by the view model
     */
    @Test
    public void getPoisNextPropertiesTest() {
        Property propertyTest = createProperty();
        PoiNextProperty poiNextPropertyTest = createPoiNextProperty(propertyTest.getId());
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        this.viewModel.insertPoiNextProperty(poiNextPropertyTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getPoisNextProperty(propertyTest.getId()).observe(rule.getActivity(), pois -> {
            assertFalse("Get points of interest next a properties in the local database", pois.isEmpty());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Delete a point of interest from the local database by the view model
     */
    @Test
    public void deletePoiNextProperty() {
        Property propertyTest = createProperty();
        PoiNextProperty poiNextPropertyTest = createPoiNextProperty(propertyTest.getId());
        this.viewModel.insertProperty(propertyTest);
        onIdle();
        this.viewModel.insertPoiNextProperty(poiNextPropertyTest);
        onIdle();
        this.viewModel.deletePoiNextProperty(poiNextPropertyTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getPoisNextProperty(propertyTest.getId()).observe(rule.getActivity(), pois -> {
            assertTrue("Delete points of interest next a properties in the local database", pois.isEmpty());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Insert an agent in the database from the view model
     */
    @Test
    public void insertAgent() {
        Agent agentTest = createTestAgent();
        this.viewModel.insertAgent(agentTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getAgent(agentTest.getId()).observe(rule.getActivity(), agent -> {
            assertEquals("Insert agent in the local database", agentTest, agent);
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Retrieve an agent from the local database by the view model
     */
    @Test
    public void getAgent() {
        Agent agentTest = createTestAgent();
        this.viewModel.insertAgent(agentTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getAgents().observe(rule.getActivity(), agent -> {
            assertFalse("Get agents from the local database", agent.isEmpty());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }

    /**
     * Update an agent from the local database by the view model
     */
    @Test
    public void updateAgent() {
        Agent agentTest = createTestAgent();
        this.viewModel.insertAgent(agentTest);
        onIdle();
        agentTest.setFirstName("update");
        this.viewModel.updateAgent(agentTest);
        onIdle();
        callOnApiIdl.increment();
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(() -> this.viewModel.getAgent(agentTest.getId()).observe(rule.getActivity(), agent -> {
            assertEquals("Update agent from the local database", agentTest.getFirstName(), agent.getFirstName());
            if (!callOnApiIdl.isIdleNow()) {
                callOnApiIdl.decrement();
            }
        }));
        onIdle();
    }
}