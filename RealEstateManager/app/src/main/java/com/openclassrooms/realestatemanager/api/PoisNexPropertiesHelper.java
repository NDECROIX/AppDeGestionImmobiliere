package com.openclassrooms.realestatemanager.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;

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

    public static Task<Void> addPoisNextProperties(PoiNextProperty poiNextProperty) {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().document(poiNextProperty.getHash()).set(poiNextProperty);
    }

    public static Task<DocumentSnapshot> getPoiNextProperty(String uid) {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().document(uid).get();
    }

    public static Task<Void> deletePoiNextProperty(String uid) {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().document(uid).delete();
    }

    public static Task<QuerySnapshot> getPoisNextProperties() {
        return PoisNexPropertiesHelper.getPoisNextPropertiesCollection().get();
    }
}