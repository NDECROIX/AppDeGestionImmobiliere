package com.openclassrooms.realestatemanager.api;

import android.net.Uri;

import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.utils.Utils;

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
     * Retrieves the url from firebase storage
     *
     * @param photoDeviceUri Device uri
     * @param uid            Image uid
     * @return Uri task
     */
    public static Task<Uri> getUrlPicture(String photoDeviceUri, String uid) {
        return getStorageReference(photoDeviceUri).child(Utils.convertStringMd5(uid)).getDownloadUrl();
    }

    public static FileDownloadTask savePictureToFile(Photo photo){
        return getStorageReference(photo.getPropertyID()).child(photo.getHash()).getFile(Uri.parse(photo.getUri()));
    }
}
