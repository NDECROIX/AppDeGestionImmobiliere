package com.openclassrooms.realestatemanager.database.dao;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;

import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.utils.LiveDataTestUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test the agent data access object
 */
@LargeTest
@RunWith(AndroidJUnit4ClassRunner.class)
public class AgentDaoTest {

    private AppDatabase database;

    // Perform tests on the main thread
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    /**
     * Init the database to perform test
     */
    @Before
    public void initDb() {
        ApplicationProvider.getApplicationContext().deleteDatabase("DatabaseREM.db");
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
     * Create an agent to perform test
     *
     * @return Agent test
     */
    @DataPoint
    public static Agent createTestAgent() {
        return new Agent("test", "test", "test", "test");
    }

    /**
     * Test to add an agent in the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void insertAgentTest() throws Exception {
        Agent agentTest = createTestAgent();
        this.database.agentDAO().insertAgent(agentTest);
        Agent agent = LiveDataTestUtil.getValue(database.agentDAO().getAgent(agentTest.getId()));
        assertEquals("Insert agent in the local database", agentTest, agent);
    }

    /**
     * Test to get agents from the local database
     *
     * @throws Exception LiveData
     */
    @Test
    public void getAgentsTest() throws Exception {
        Agent agentTest = createTestAgent();
        this.database.agentDAO().insertAgent(agentTest);
        List<Agent> agents = LiveDataTestUtil.getValue(database.agentDAO().getAgents());
        assertFalse("Get agents from the local database", agents.isEmpty());
    }
}