package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.AgentHelper;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

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
    private int count = 0;

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
                    if (!agentsRoom.isEmpty()) {
                        updateAgents();
                    } else {
                        getNewAgentsFromFirebase();
                    }
                }
                propertyViewModel.getAgents().removeObserver(this);
            }
        });
    }

    /**
     * Compare local agents with distance agent.
     * If update date is more recent on firebase, download data otherwise upload data.
     */
    private void updateAgents() {
        if (this.count >= agentsRoom.size()) return;
        final int count = this.count;
        AgentHelper.getAgent(agentsRoom.get(count).getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Agent agentFirebase = task.getResult().toObject(Agent.class);
                if (agentFirebase != null && agentFirebase.getUpdateDate() != agentsRoom.get(count).getUpdateDate()) {
                    if (agentFirebase.getUpdateDate() > agentsRoom.get(count).getUpdateDate()) {
                        updateAgentInRooms(agentFirebase);
                    } else {
                        updateAgentInFirebase(agentsRoom.get(count));
                    }
                } else if (agentFirebase == null) {
                    updateAgentInFirebase(agentsRoom.get(count));
                }
            } else if (task.isSuccessful() && task.getResult() == null) {
                updateAgentInFirebase(agentsRoom.get(count));
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            this.count++;
            if (this.count < agentsRoom.size()) {
                updateAgents();
            } else {
                getNewAgentsFromFirebase();
            }
        });
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
     * Download agents from firebase database not present in the local database
     */
    private void getNewAgentsFromFirebase() {
        AgentHelper.getAgents().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Agent> agents = new ArrayList<>(task.getResult().toObjects(Agent.class));
                for (Agent agent : agents) {
                    if (!agentsRoom.contains(agent)) {
                        addAgentInRoom(agent);
                    }
                }
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            callback.agentsSynchronized();
        });
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