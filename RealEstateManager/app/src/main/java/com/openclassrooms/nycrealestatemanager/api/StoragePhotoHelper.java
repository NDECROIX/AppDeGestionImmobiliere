package com.openclassrooms.nycrealestatemanager.api;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

/**
 * Manage photo storage on firebase storage
 */
public class StoragePhotoHelper {

    /**
     * Retrieves the storage reference
     *
     * @param propertyId Device photo uri
     * @return Storage reference of the image
     */
    private static StorageReference getStorageReference(String propertyId) {
        return FirebaseStorage.getInstance().getReference(propertyId);
    }

    /**
     * Add a file on firebase storage
     *
     * @param propertyId Property id
     * @param uri        Image uid
     * @return Upload task
     */
    public static UploadTask putFileOnFirebaseStorage(String propertyId, String uid, String uri) {
        return getStorageReference(propertyId).child(uid).putFile(Uri.parse("file://" + uri));
    }

    /**
     * Delete files from firebase storage
     *
     * @return Void task
     */
    public static Task<Void> deleteFileFromFirebaseStorage(String propertyId, String uid) {
        return getStorageReference(propertyId).child(uid).delete();
    }

    /**
     * Retrieve photo from database and saves it in a path
     *
     * @param propertyId Property id
     * @param photoHash  Photo hash
     * @param path       Path where save the photo
     * @return File download task
     */
    public static FileDownloadTask savePictureToFile(String propertyId, String photoHash, String path) {
        return getStorageReference(propertyId).child(photoHash).getFile(Uri.parse(path));
    }
}