package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;

import com.openclassrooms.realestatemanager.api.AgentHelper;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

public class UpdateAgent {

    public interface UpdateAgentListener {
        void notification(String agent);
        void updateComplete();
        void error(Exception exception);
    }

    private List<Agent> agentsRoom;
    private PropertyViewModel propertyViewModel;
    private UpdateAgentListener callback;
    private LifecycleOwner lifecycleOwner;
    private int count = 0;

    public UpdateAgent(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdateAgentListener callback) {
        this.callback = callback;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    public void updateData() {
        this.propertyViewModel.getAgents().observe(lifecycleOwner, agents -> {
            agentsRoom = new ArrayList<>(agents);
            if (!agentsRoom.isEmpty()) {
                updateAgents();
            } else {
                getNewAgentsFromFirebase();
            }
        });
    }

    private void updateAgents() {
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
            count++;
            if (count < agentsRoom.size()){
                updateAgents();
            } else {
                getNewAgentsFromFirebase();
            }
        });
    }

    private void updateAgentInFirebase(Agent agent) {
        AgentHelper.updateAgent(agent).addOnFailureListener(callback::error);
        callback.notification(String.format("Agent %s %s uploaded", agent.getFirstName(), agent.getLastName()));
    }

    private void updateAgentInRooms(Agent agent) {
        propertyViewModel.updateAgent(agent);
        callback.notification(String.format("Agent %s %s updated", agent.getFirstName(), agent.getLastName()));
    }

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
            callback.updateComplete();
        });
    }

    private void addAgentInRoom(Agent agent) {
        propertyViewModel.insertAgent(agent);
        callback.notification(String.format("Agent %s %s added", agent.getFirstName(), agent.getLastName()));
    }
}