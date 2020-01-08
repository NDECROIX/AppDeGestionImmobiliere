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
 * Test Photo content provider
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class PhotoContentProviderTest {

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
     * Test to get photos from the content provider
     */
    @Test
    public void getPhotosTest() {
        final Cursor cursor = contentResolver.query(
                ContentUris.withAppendedId(PhotoContentProvider.URI_PHOTO, 0),
                null, null, null);
        assertNotNull("Get photos return a null cursor", cursor);
        if (cursor.getCount() <= 0){
            assertEquals(0, cursor.getCount());
        } else {
            assertTrue(cursor.getCount() >= 1);
            assertTrue(cursor.moveToFirst());
            assertNotNull(cursor.getString(cursor.getColumnIndexOrThrow("name")));
        }
        cursor.close();
    }
}