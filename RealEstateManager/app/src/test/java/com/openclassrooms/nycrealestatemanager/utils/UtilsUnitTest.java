package com.openclassrooms.nycrealestatemanager.utils;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Local unit test on the Utils class
 */
public class UtilsUnitTest {

    /**
     * Check the conversion of a dollar into euro
     */
    @Test
    public void convertDollarToEuro() {
        assertEquals(8, Utils.convertDollarToEuro(10));
    }

    /**
     * Check the conversion of a euro into dollar
     */
    @Test
    public void convertEuroToDollar() {
        assertEquals(10, Utils.convertEuroToDollar(8));
    }

    /**
     * Get the current date in dd/MM/yyyy format
     */
    @Test
    public void getTodayDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        assertEquals(dateFormat.format(new Date()), Utils.getTodayDate());
    }

    /**
     * Test to convert string in MD5
     */
    @Test
    public void convertStringMd5() {
        String valueToHAsh = "Real Estate Manager";
        String expected = "5331cc5722b66cd3ba4e327028f72114";
        assertEquals(expected, Utils.convertStringMd5(valueToHAsh));
    }

    /**
     * Test to convert double to string dollar format
     */
    @Test
    public void getPrice() {
        Double valueToConvert = 2000000d;
        String expected = "2,000,000";
        assertEquals(expected, Utils.getPrice(valueToConvert));
    }
}