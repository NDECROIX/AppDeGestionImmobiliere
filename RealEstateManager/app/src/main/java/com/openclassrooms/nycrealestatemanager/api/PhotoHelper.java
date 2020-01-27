package com.openclassrooms.nycrealestatemanager.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.nycrealestatemanager.model.Photo;

/**
 * Manage calls on the Firebase database photo collection
 */
public class PhotoHelper {

    /**
     * Photo collection name;
     */
    private static final String COLLECTION_NAME_PHOTO = "photos";

    /**
     * Get the collection reference
     *
     * @return The collection reference
     */
    private static CollectionReference getPhotoCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME_PHOTO);
    }

    /**
     * Add a Photo in the database
     *
     * @param uid   Hash uri
     * @param photo Photo to add to the database
     * @return Void task
     */
    public static Task<Void> updatePhoto(String uid, Photo photo) {
        return PhotoHelper.getPhotoCollection().document(uid).set(photo);
    }

    /**
     * Get a Photo from the database
     *
     * @param uid Hash uri
     * @return DocumentSnapshot photo
     */
    static Task<DocumentSnapshot> getPhoto(String uid) {
        return PhotoHelper.getPhotoCollection().document(uid).get();
    }

    /**
     * Delete a Photo from the database
     *
     * @param uid uri of the photo
     * @return Task void
     */
    public static Task<Void> deletePhoto(String uid) {
        return PhotoHelper.getPhotoCollection().document(uid).delete();
    }

    /**
     * Get Photos from the database
     *
     * @return DocumentSnapshot photos
     */
    public static Task<QuerySnapshot> getPhotos() {
        return PhotoHelper.getPhotoCollection().get();
    }
}