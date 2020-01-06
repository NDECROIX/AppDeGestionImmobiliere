package com.openclassrooms.realestatemanager.api;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;

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
 * Test PropertyHelper
 */
@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class PropertyHelperTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, false, true);

    /**
     * Required for asynchronous task
     */
    private final CountingIdlingResource callOnApiIdl = new CountingIdlingResource("Call on API IdlingResource");

    private Property property;
    private boolean taskIsSuccessful;

    @Before
    public void setup() {
        MainActivity activity = rule.getActivity();
        assertNotNull(activity);
        property = createProperty();
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
    public Property createProperty() {
        Property property = new Property();
        property.setType("test");
        property.setRooms(10);
        property.setPrice(10d);
        property.setStreetNumber(10);
        property.setStreetName("test");
        property.setZip(10);
        property.setCity("test");
        property.setEntryDate(10);
        property.setId(Utils.convertStringMd5(property.getStringToHash()));
        return property;
    }

    /**
     * Add a property to firebase database
     */
    @DataPoint
    public void addPropertyInFirebase() {
        callOnApiIdl.increment();
        PropertyHelper.updateProperty(property).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Delete a property from firebase database
     */
    @DataPoint
    public void deletePropertyFromFirebase() {
        callOnApiIdl.increment();
        PropertyHelper.deleteProperty(property).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Test to add a property to firebase database
     */
    @Test
    public void addPropertyTest() {
        addPropertyInFirebase();
        assertTrue("Add property in Firebase database", taskIsSuccessful);
        deletePropertyFromFirebase();
    }

    /**
     * Test to delete a property from firebase database
     */
    @Test
    public void deletePropertyTest() {
        addPropertyInFirebase();
        deletePropertyFromFirebase();
        assertTrue("Delete an property from the Firebase database", taskIsSuccessful);
    }

    /**
     * Retrieve a property from firebase database
     */
    @Test
    public void getPropertyFromFirebase() {
        addPropertyInFirebase();
        callOnApiIdl.increment();
        PropertyHelper.getProperty(property.getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Property property = task.getResult().toObject(Property.class);
                if (property != null && property.equals(this.property)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get a property from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deletePropertyFromFirebase();
    }

    /**
     * Get properties from firebase database
     */
    @Test
    public void getPropertiesFromFirebase() {
        addPropertyInFirebase();
        callOnApiIdl.increment();
        PropertyHelper.getProperties().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Property> agents = task.getResult().toObjects(Property.class);
                if (!agents.isEmpty() && agents.contains(this.property)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get properties from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deletePropertyFromFirebase();
    }
}