package com.openclassrooms.nycrealestatemanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.nycrealestatemanager.model.Agent;

import java.util.List;

@Dao
public interface AgentDao {

    @Query("SELECT * FROM Agent")
    LiveData<List<Agent>> getAgents();

    @Query("SELECT * FROM Agent WHERE id LIKE :agentID")
    LiveData<Agent> getAgent(String agentID);

    @Insert
    void insertAgent(Agent agent);

    @Update
    void updateAgent(Agent agent);
}