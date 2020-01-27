package com.openclassrooms.nycrealestatemanager.model;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import static org.junit.Assert.assertEquals;

/**
 * Local unit test on the PoiNextProperty class
 */
public class PoiNextPropertyUnitTest {

    /**
     * Create a point of interest next a property to perform test
     * @return PoiNextProperty
     */
    @DataPoint
    private PoiNextProperty createPoiNextProperty(){
        return new PoiNextProperty("propertyId", "name");
    }

    @Test
    public void getHash(){
        PoiNextProperty poiNextProperty = createPoiNextProperty();
        String result = poiNextProperty.getHash();
        String expected = "3bb4116b36f109f638eb9ea961e492aa";
        assertEquals(expected, result);
    }
}