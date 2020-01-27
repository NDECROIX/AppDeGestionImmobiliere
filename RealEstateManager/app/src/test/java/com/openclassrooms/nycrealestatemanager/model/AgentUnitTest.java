package com.openclassrooms.nycrealestatemanager.model;

import org.junit.Test;
import org.junit.experimental.theories.DataPoint;

import static org.junit.Assert.assertEquals;

/**
 * Local unit test on the Agent class
 */
public class AgentUnitTest {

    @DataPoint
    private Agent createTestAgent() {
        return new Agent("Test", "Test", "Test", "Test");
    }

    /**
     * Test to create an agent id
     */
    @Test
    public void createAnId() {
        Agent agent = new Agent();
        agent.setFirstName("Test");
        agent.setLastName("Test");
        agent.setEmail("Test");
        agent.setPhone("Test");
        agent.createId();
        assertEquals("1fb0e331c05a52d5eb847d6fc018320d", agent.getId());
    }

    /**
     * Test if two agents with different data but same id are equals
     */
    @Test
    public void equalsTest() {
        Agent agent = createTestAgent();
        agent.setPhone("0745566332");
        assertEquals(agent, createTestAgent());
    }
}