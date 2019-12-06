package com.openclassrooms.realestatemanager;

import android.content.Context;

import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.utils.Utils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Instrumented test, which will execute on an Android device.
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class UtilsInstrumentedTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class);

    private Context context;

    /**
     * Setting up the necessary elements for the tests
     */
    @Before
    public void setUp() {
        MainActivity activity = rule.getActivity();
        assertNotNull(activity);
        context = activity.getApplicationContext();
        assertNotNull(context);
    }

    /**
     * Check if the device has an Internet connection
     */
    @Test
    public void checkInternetAvailable() {
        assertTrue(Utils.isInternetAvailable(context));
    }
}
