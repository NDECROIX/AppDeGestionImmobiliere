package com.openclassrooms.nycrealestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.nycrealestatemanager.database.dao.AgentDao;
import com.openclassrooms.nycrealestatemanager.model.Agent;

import java.util.List;

/**
 * Repository of agents
 */
public class AgentDataRepository {

    private final AgentDao agentDao;

    public AgentDataRepository(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    /**
     * Get Agents from the AppDatabase
     *
     * @return List of all agent
     */
    public LiveData<List<Agent>> getAgents() {
        return this.agentDao.getAgents();
    }

    /**
     * Get an agent from the database
     *
     * @param agentID Id of the agent
     * @return agent
     */
    public LiveData<Agent> getAgent(String agentID) {
        return this.agentDao.getAgent(agentID);
    }

    /**
     * Insert an agent in the database
     *
     * @param agent New Agent
     */
    public void insertAgent(Agent agent) {
        this.agentDao.insertAgent(agent);
    }

    /**
     * Update an agent in the database
     *
     * @param agent Agent to update
     */
    public void updateAgent(Agent agent) {
        this.agentDao.updateAgent(agent);
    }
}