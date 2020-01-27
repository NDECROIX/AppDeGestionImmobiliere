package com.openclassrooms.nycrealestatemanager.provider;


import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.nycrealestatemanager.database.AppDatabase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;

/**
 * Test the property content provider
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PropertyContentProviderTest {

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
     * Test get properties from the content provider
     */
    @Test
    public void getPropertiesTest() {
        final Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(PropertyContentProvider.URI_PROPERTY, 0),
                null, null, null);
        assertNotNull("Get properties return a null cursor", cursor);
        if (cursor.getCount() <= 0){
            assertEquals(0, cursor.getCount());
        } else {
            assertTrue(cursor.getCount() >= 1);
            assertTrue(cursor.moveToFirst());
            assertNotNull(cursor.getString(cursor.getColumnIndexOrThrow("id")));
        }
        cursor.close();
    }
}