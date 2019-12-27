package com.openclassrooms.realestatemanager.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.realestatemanager.model.Agent;

public class AgentHelper {

    /**
     * Agent collection name;
     */
    private static final String COLLECTION_NAME_AGENT = "agents";

    /**
     * Get the collection reference
     *
     * @return The collection reference
     */
    private static CollectionReference getAgentCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_AGENT);
    }

    /**
     * Add an Agent in the database
     * @param agent Agent to add to the database
     * @return Void task
     */
    public static Task<Void> updateAgent(Agent agent){
        return AgentHelper.getAgentCollection().document(agent.getId()).set(agent);
    }

    /**
     * Get an Agent from the database
     * @param id Id of the agent to be recovered
     * @return DocumentSnapshot agent
     */
    public static Task<DocumentSnapshot> getAgent(String id){
        return AgentHelper.getAgentCollection().document(id).get();
    }

    /**
     * Get all Agent from the database
     * @return DocumentSnapshot agents
     */
    public static Task<QuerySnapshot> getAgents(){
        return AgentHelper.getAgentCollection().get();
    }
}