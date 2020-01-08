package com.openclassrooms.realestatemanager.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.realestatemanager.database.AppDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Test the point of interest content provider
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PoiNextPropertyContentProviderTest {

    private ContentResolver contentResolver;

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Init the database to perform test
     */
    @Before
    public void initDb() {
        Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .addCallback(AppDatabase.prePopulateDatabase())
                .build();
        contentResolver = ApplicationProvider.getApplicationContext().getContentResolver();
    }

    /**
     * Test to get points of interest next properties from the content provider
     */
    @Test
    public void getPoisNextPropertiesTest() {
        final Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(PoiNextPropertyContentProvider.URI_POI_NEXT_PROPERTY, 0),
                null, null, null);
        assertNotNull("Get points of interest next properties return a null cursor", cursor);
        if (cursor.getCount() <= 0){
            assertEquals(0, cursor.getCount());
        } else {
            assertTrue(cursor.getCount() >= 1);
            assertTrue(cursor.moveToFirst());
            assertNotNull(cursor.getString(cursor.getColumnIndexOrThrow("property_id")));
        }
        cursor.close();
    }
}