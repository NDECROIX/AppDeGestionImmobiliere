package com.openclassrooms.realestatemanager.database;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.Type;
import com.openclassrooms.realestatemanager.utils.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import static org.junit.Assert.assertFalse;

/**
 * Test the application database
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class AppDatabaseTest {

    private AppDatabase database;

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Init the database to perform test
     */
    @Before
    public void initDb() {
        this.database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase.class)
                .allowMainThreadQueries()
                .addCallback(AppDatabase.prePopulateDatabase())
                .build();
    }

    @After
    public void closeDb() {
        database.close();
    }

    /**
     * Check if the database is pre-populated with types
     *
     * @throws Exception LiveData
     */
    @Test
    public void databaseIsPrepopulateWithType() throws Exception {
        List<Type> types = LiveDataTestUtil.getValue(database.typeDAO().getAll());
        assertFalse("Database prepopulate with type", types.isEmpty());
    }

    /**
     * Check if the database is pre-populated with points if interest
     *
     * @throws Exception LiveData
     */
    @Test
    public void databaseIsPrepopulateWithPoi() throws Exception {
        List<Poi> poisNextProperties = LiveDataTestUtil.getValue(database.poiDAO().getAll());
        assertFalse("Database prepopulate with point of interest", poisNextProperties.isEmpty());
    }
}