package com.openclassrooms.realestatemanager.api;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.utils.Utils;

/**
 * Manage photo storage on firebase storage
 */
public class StoragePhotoHelper {

    /**
     * Retrieves the storage reference
     *
     * @param photoDeviceUri Device photo uri
     * @return Storage reference of the image
     */
    public static StorageReference getStorageReference(String photoDeviceUri) {
        return FirebaseStorage.getInstance().getReference(photoDeviceUri);
    }

    /**
     * Add a file on firebase storage
     *
     * @param propertyId Property id
     * @param imageUid   Image uid
     * @return Upload task
     */
    public static UploadTask putFileOnFirebaseStorage(String propertyId, String imageUid) {
        return getStorageReference(propertyId).child(Utils.convertStringMd5(imageUid)).putFile(Uri.parse("file:" + imageUid));
    }

    /**
     * Delete files from firebase storage
     */
    public static void deleteFileFromFirebaseStorage(String path) {
        getStorageReference(path).delete();
    }

    /**
     * Retrieve the url from firebase storage
     *
     * @param photoDeviceUri Device uri
     * @param uid            Image uid
     * @return Uri task
     */
    public static Task<Uri> getUrlPicture(String photoDeviceUri, String uid) {
        return getStorageReference(photoDeviceUri).child(Utils.convertStringMd5(uid)).getDownloadUrl();
    }

    /**
     * Retrieve photo from database and saves it in a path
     * @param photo Photo to retrieve
     * @param path Path where save the photo
     * @return File download task
     */
    public static FileDownloadTask savePictureToFile(Photo photo, String path){
        return getStorageReference(photo.getPropertyID()).child(photo.getHash()).getFile(Uri.parse(path));
    }
}
