package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.Agent;

import java.util.List;

@Dao
public interface AgentDao {

    @Query("SELECT * FROM Agent")
    LiveData<List<Agent>> getAgents();

    @Query("SELECT * FROM Agent")
    Cursor getAgentsCursor();

    @Query("SELECT * FROM Agent WHERE id LIKE :agentID")
    LiveData<Agent> getAgent(String agentID);

    @Insert
    void insertAgent(Agent agent);
}
