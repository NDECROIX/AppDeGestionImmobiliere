package com.openclassrooms.nycrealestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.nycrealestatemanager.api.AgentHelper;
import com.openclassrooms.nycrealestatemanager.model.Agent;
import com.openclassrooms.nycrealestatemanager.viewmodel.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronize local database agents with the firebase database
 */
class UpdateAgent {

    /**
     * Notified when agents are synchronized
     * or when an error occurs
     */
    public interface UpdateAgentListener {
        void agentsSynchronized();

        void error(Exception exception);
    }

    private List<Agent> agentsRoom;
    private final PropertyViewModel propertyViewModel;
    private UpdateAgentListener callback;
    private LifecycleOwner lifecycleOwner;

    UpdateAgent(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdateAgentListener callback) {
        this.callback = callback;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    /**
     * Get agent from the local database
     */
    void updateData() {
        this.propertyViewModel.getAgents().observe(lifecycleOwner, new Observer<List<Agent>>() {
            @Override
            public void onChanged(List<Agent> agents) {
                if (agentsRoom == null) {
                    agentsRoom = new ArrayList<>(agents);
                    getAgentsFromFirebase();
                }
                propertyViewModel.getAgents().removeObserver(this);
            }
        });
    }

    private List<Agent> agentsFirebase;

    /**
     * Get agents from Firebase database
     */
    private void getAgentsFromFirebase(){
        AgentHelper.getAgents().addOnCompleteListener( task -> {
            agentsFirebase = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null){
                agentsFirebase.addAll(task.getResult().toObjects(Agent.class));
            }
            syncData();
        });
    }

    /**
     * Compare agents from the local database with agents from the firebase database
     */
    private void syncData() {
        for (Agent agentFirebase : agentsFirebase){
            final int index = agentsRoom.indexOf(agentFirebase);
            if (index >= 0){
                if (agentFirebase.getUpdateDate() > agentsRoom.get(index).getUpdateDate()) {
                    updateAgentInRooms(agentFirebase);
                } else if (agentFirebase.getUpdateDate() < agentsRoom.get(index).getUpdateDate()){
                    updateAgentInFirebase(agentsRoom.get(index));
                }
                agentsRoom.remove(index);
            } else {
                addAgentInRoom(agentFirebase);
            }
        }
        for (Agent agentRoom : agentsRoom){
            updateAgentInFirebase(agentRoom);
        }
        callback.agentsSynchronized();
    }

    /**
     * Update agent on firebase
     *
     * @param agent Agent to update
     */
    private void updateAgentInFirebase(Agent agent) {
        AgentHelper.updateAgent(agent).addOnFailureListener(callback::error);
    }

    /**
     * Update agent in the local database
     *
     * @param agent Agent to update
     */
    private void updateAgentInRooms(Agent agent) {
        propertyViewModel.updateAgent(agent);
    }

    /**
     * Add an agent in the local database
     *
     * @param agent Agent to update
     */
    private void addAgentInRoom(Agent agent) {
        propertyViewModel.insertAgent(agent);
    }
}