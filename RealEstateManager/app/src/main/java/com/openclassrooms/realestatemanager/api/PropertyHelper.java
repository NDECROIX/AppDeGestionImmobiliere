package com.openclassrooms.realestatemanager.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.realestatemanager.model.Property;

public class PropertyHelper {

    /**
     * Property collection name;
     */
    private static final String COLLECTION_NAME_PROPERTY = "properties";

    /**
     * Get the collection reference
     *
     * @return The collection reference
     */
    private static CollectionReference getPropertyCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_PROPERTY);
    }

    /**
     * Add a property in the database
     * @param property property to add to the database
     * @return Void task
     */
    public static Task<Void> updateProperty(Property property){
        return PropertyHelper.getPropertyCollection().document(property.getId()).set(property);
    }

    /**
     * Get a property from the database
     * @param id Id of the property to be recovered
     * @return DocumentSnapshot agent
     */
    public static Task<DocumentSnapshot> getProperty(String id){
        return PropertyHelper.getPropertyCollection().document(id).get();
    }

    /**
     * Get properties from the database
     * @return DocumentSnapshot agents
     */
    public static Task<QuerySnapshot> getProperties(){
        return PropertyHelper.getPropertyCollection().get();
    }
}