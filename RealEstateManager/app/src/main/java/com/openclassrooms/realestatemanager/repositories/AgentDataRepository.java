package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.AgentDao;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.model.Photo;

import java.util.List;

public class AgentDataRepository {

    private final AgentDao agentDao;

    public AgentDataRepository(AgentDao agentDao) {
        this.agentDao = agentDao;
    }

    /**
     * Get Agents from the AppDatabase
     * @return List of all agent
     */
    public LiveData<List<Agent>> getAgents() {
        return this.agentDao.getAgents();
    }

    /**
     * Get an agent from the database
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
}
