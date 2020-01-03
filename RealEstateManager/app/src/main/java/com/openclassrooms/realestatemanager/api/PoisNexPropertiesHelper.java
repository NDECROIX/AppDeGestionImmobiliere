package com.openclassrooms.realestatemanager.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;

/**
 * Manage calls on the Firebase database PoiNextProperties collection.
 */
public class PoisNexPropertiesHelper {

    /**
     * Points of interest next to properties collection name;
     */
    private static final String COLLECTION_NAME_POIS_NEXT_PROPERTIES = "poisNextProperties";

    /**
     * Get the collection reference
     *
     * @return The collection reference
     */
    private static CollectionReference getPoisNextPropertiesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_POIS_NEXT_PROPERTIES);
    }

    /**
     * Add a PoiNextProperty in firebase database
     * @param poiNextProperty PoiNextProperty to add
     * @return Void task
     */
    public static Task<Void> addPoisNextProperties(PoiNextProperty poiNextProperty) {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().document(poiNextProperty.getHash()).set(poiNextProperty);
    }

    /**
     * Retrieves a poiNextProperty from firebase database
     * @param uid PoiNextProperty unique ID
     * @return PoiNextProperty
     */
    public static Task<DocumentSnapshot> getPoiNextProperty(String uid) {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().document(uid).get();
    }

    /**
     * Delete a PoiNextProperty from firebase database
     * @param uid PoiNextProperty unique id
     * @return Void task
     */
    public static Task<Void> deletePoiNextProperty(String uid) {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().document(uid).delete();
    }

    /**
     * Get getPoisNextProperties from firebase database
     * @return List of PoiNetProperty
     */
    public static Task<QuerySnapshot> getPoisNextProperties() {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().get();
    }
}