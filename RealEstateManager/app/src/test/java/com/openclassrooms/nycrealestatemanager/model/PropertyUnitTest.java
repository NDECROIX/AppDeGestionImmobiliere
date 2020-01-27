package com.openclassrooms.nycrealestatemanager.model;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import static org.junit.Assert.assertEquals;

/**
 * Local unit test on the Property class
 */
public class PropertyUnitTest {

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
     * Test the property hash
     */
    @Test
    public void getHash(){
        Property property = createTestProperty();
        String result = property.getHash();
        String expected = "ef0cceb82d46ca93e1b3df6cc0c66741";
        assertEquals(expected, result);
    }

}