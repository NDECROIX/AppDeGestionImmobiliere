package com.openclassrooms.realestatemanager.api;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;

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
 * Test PoisNextPropertiesHelper
 */
@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class PoisNextPropertiesHelperTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, false, true);

    /**
     * Required for asynchronous task
     */
    private final CountingIdlingResource callOnApiIdl = new CountingIdlingResource("Call on API IdlingResource");

    private PoiNextProperty poiNextProperty;
    private boolean taskIsSuccessful;

    @Before
    public void setup() {
        MainActivity activity = rule.getActivity();
        assertNotNull(activity);
        poiNextProperty = createPoiNextProperty();
        IdlingRegistry.getInstance().register(callOnApiIdl);
    }

    /**
     * Unregister on the IdlingResource
     */
    @After
    public void setDown() {
        IdlingRegistry.getInstance().unregister(callOnApiIdl);
    }

    /**
     * Create a PoiNextProperty to perform tests on it
     *
     * @return PoiNextProperty
     */
    @DataPoint
    public PoiNextProperty createPoiNextProperty() {
        return new PoiNextProperty("test", "test");
    }

    /**
     * Add point of interest next property to the firebase database
     */
    @DataPoint
    public void addPoiNextPropertyInFirebase() {
        callOnApiIdl.increment();
        PoisNexPropertiesHelper.addPoisNextProperties(poiNextProperty).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Delete a point of interest next property from firebase database
     */
    @DataPoint
    public void deletePoiNextPropertyFromFirebase() {
        callOnApiIdl.increment();
        PoisNexPropertiesHelper.deletePoiNextProperty(poiNextProperty.getHash()).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Test to add a point of interest next a property to Firebase database
     */
    @Test
    public void addPoiNextPropertyTest() {
        addPoiNextPropertyInFirebase();
        assertTrue("Add point of interest in Firebase database", taskIsSuccessful);
        deletePoiNextPropertyFromFirebase();
    }

    /**
     * Test to delete a PoiNextProperty from firebase database
     */
    @Test
    public void deletePoiNextPropertyTest() {
        addPoiNextPropertyInFirebase();
        deletePoiNextPropertyFromFirebase();
        assertTrue("Delete a point of interest from the Firebase database", taskIsSuccessful);
    }

    /**
     * Retrieve a points of interest next a property from the firebase database
     */
    @Test
    public void getPoiNextPropertyFromFirebase() {
        addPoiNextPropertyInFirebase();
        callOnApiIdl.increment();
        PoisNexPropertiesHelper.getPoiNextProperty(poiNextProperty.getHash()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                PoiNextProperty poiNextProperty = task.getResult().toObject(PoiNextProperty.class);
                if (poiNextProperty != null && poiNextProperty.equals(this.poiNextProperty)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get a point of interest from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deletePoiNextPropertyFromFirebase();
    }

    /**
     * Get points of interest next properties from the firebase database
     */
    @Test
    public void getPoisNextPropertiesFromFirebase() {
        addPoiNextPropertyInFirebase();
        callOnApiIdl.increment();
        PoisNexPropertiesHelper.getPoisNextProperties().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<PoiNextProperty> poiNextProperties = task.getResult().toObjects(PoiNextProperty.class);
                if (!poiNextProperties.isEmpty() && poiNextProperties.contains(this.poiNextProperty)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get points of interest from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deletePoiNextPropertyFromFirebase();
    }
}