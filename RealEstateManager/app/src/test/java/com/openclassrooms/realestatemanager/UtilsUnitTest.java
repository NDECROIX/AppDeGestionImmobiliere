package com.openclassrooms.realestatemanager;

import org.junit.Test;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.*;

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


}