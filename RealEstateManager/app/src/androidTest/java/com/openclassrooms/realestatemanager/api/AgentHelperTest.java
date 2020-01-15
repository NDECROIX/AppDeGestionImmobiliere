package com.openclassrooms.realestatemanager.api;

import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.idling.CountingIdlingResource;
import androidx.test.filters.LargeTest;
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner;
import androidx.test.rule.ActivityTestRule;

import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Agent;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.theories.DataPoint;
import org.junit.runner.RunWith;

import java.util.List;

import static androidx.test.espresso.Espresso.onIdle;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Test AgentHelper
 */
@RunWith(AndroidJUnit4ClassRunner.class)
public class AgentHelperTest {

    @Rule
    public final ActivityTestRule<MainActivity> rule = new ActivityTestRule<>(MainActivity.class, false, true);

    /**
     * Required for asynchronous task
     */
    private final CountingIdlingResource callOnApiIdl = new CountingIdlingResource("Call on API IdlingResource");

    private Agent agent;
    private boolean taskIsSuccessful;

    @Before
    public void setup() {
        MainActivity activity = rule.getActivity();
        assertNotNull(activity);
        agent = createAgent();
        IdlingRegistry.getInstance().register(callOnApiIdl);
    }

    /**
     * Unregister on the IdlingResource
     */
    @After
    public void setDown() {
        IdlingRegistry.getInstance().unregister(callOnApiIdl);
    }

    @DataPoint
    public Agent createAgent() {
        return new Agent("test", "test", "test", "test");
    }

    /**
     * Add an agent to the Firebase database
     */
    @DataPoint
    public void addAgentInFirebase() {
        callOnApiIdl.increment();
        AgentHelper.updateAgent(agent).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Delete an agent from Firebase database
     */
    @DataPoint
    public void deleteAgentFromFirebase() {
        callOnApiIdl.increment();
        AgentHelper.deleteAgent(agent).addOnCompleteListener(task -> {
            taskIsSuccessful = task.isSuccessful();
            callOnApiIdl.decrement();
        });
        onIdle();
    }

    /**
     * Test to add an agent in the Firebase database
     */
    @Test
    public void addAgentTest() {
        addAgentInFirebase();
        assertTrue("Add agent in Firebase database", taskIsSuccessful);
        deleteAgentFromFirebase();
    }

    /**
     * Test to delete an agent from Firebase database
     */
    @Test
    public void deleteAgentTest() {
        addAgentInFirebase();
        deleteAgentFromFirebase();
        assertTrue("Delete an agent from the Firebase database", taskIsSuccessful);
    }

    /**
     * Test to retrieves an agent from firebase database
     */
    @Test
    public void getAgentFromFirebase() {
        addAgentInFirebase();
        callOnApiIdl.increment();
        AgentHelper.getAgent(agent.getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Agent agent = task.getResult().toObject(Agent.class);
                if (agent != null && agent.equals(this.agent)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get an agent from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deleteAgentFromFirebase();
    }

    /**
     * Test to get agents from Firebase database
     */
    @Test
    public void getAgentsFromFirebase() {
        addAgentInFirebase();
        callOnApiIdl.increment();
        AgentHelper.getAgents().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Agent> agents = task.getResult().toObjects(Agent.class);
                if (!agents.isEmpty() && agents.contains(this.agent)) {
                    taskIsSuccessful = task.isSuccessful();
                } else {
                    taskIsSuccessful = false;
                }
            } else {
                taskIsSuccessful = false;
            }
            assertTrue("Get agents from Firebase database", taskIsSuccessful);
            callOnApiIdl.decrement();
        });
        onIdle();
        deleteAgentFromFirebase();
    }
}