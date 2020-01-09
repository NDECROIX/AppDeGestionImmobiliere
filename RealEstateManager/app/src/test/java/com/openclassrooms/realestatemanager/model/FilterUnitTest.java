package com.openclassrooms.realestatemanager.model;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import java.util.ArrayList;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Local unit test on the Filter class
 */
public class FilterUnitTest {

    /**
     * Create a filter test
     *
     * @return Filter
     */
    @DataPoint
    private Filter createTestFilter() {
        Filter filter = new Filter();
        filter.setStatus(1);
        filter.setEntryDateFrom(1577836800000L);
        filter.setEntryDateTo(1602288000000L);
        filter.setType("House");
        filter.setMinPrice(1000000d);
        filter.setMaxPrice(5000000d);
        filter.setMinSurface(150d);
        filter.setMaxSurface(350d);
        filter.setNbrPhotos(3);
        filter.setBorough("Brooklyn");
        return filter;
    }

    /**
     * Create a property test
     *
     * @return Property
     */
    @DataPoint
    private Property createProperty() {
        Property property = new Property();
        property.setEntryDate(1599999999999L);
        property.setType("House");
        property.setPrice(2500000d);
        property.setSurface(250d);
        property.setBorough("Brooklyn");
        return property;
    }

    /**
     * Test the filter
     */
    @Test
    public void testFilter() {
        Filter filter = createTestFilter();
        Property property = createProperty();
        assertTrue(filter.meetsCriteria(property, 3, new ArrayList<>()));
        property.setPrice(10000000D);
        assertFalse(filter.meetsCriteria(property, 3, new ArrayList<>()));
    }


}