package com.openclassrooms.realestatemanager.database.updates;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.firebase.storage.StorageReference;
import com.openclassrooms.realestatemanager.api.PhotoHelper;
import com.openclassrooms.realestatemanager.api.StoragePhotoHelper;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.utils.UtilsPhoto;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Synchronize local database photos with the firebase database photos
 */
class UpdatePhoto {

    public interface UpdatePhotoListener {
        void photosSynchronized();

        void error(Exception exception);
    }

    // Photos from the local database
    private List<Photo> photosRoom;
    // Activity property view model
    private final PropertyViewModel propertyViewModel;
    // Notify errors and synchronization complete
    private UpdatePhotoListener callback;
    // Activity lifecycle
    private LifecycleOwner lifecycleOwner;
    // Properties updated in room from firebase database
    private List<String> propertiesDown;
    // Activity context
    private Context context;

    private List<Photo> photosFirebase;

    /**
     * Constructor
     *
     * @param lifecycleOwner    Activity lifecycle
     * @param propertyViewModel Activity view model
     * @param callback          UpdatePhotoListener
     * @param propertiesDown    Properties updated in room from firebase database
     * @param context           Activity context
     */
    UpdatePhoto(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel,
                UpdatePhotoListener callback, List<String> propertiesDown, Context context) {
        this.callback = callback;
        this.context = context;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
        this.propertiesDown = new ArrayList<>(propertiesDown);
    }

    /**
     * Get photos from the local database
     */
    void updateData() {
        this.propertyViewModel.getPhotos().observe(lifecycleOwner, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                if (photosRoom == null) {
                    photosRoom = new ArrayList<>(photos);
                    getPhotosFromFirebase();
                }
                propertyViewModel.getPhotos().removeObserver(this);
            }
        });
    }

    /**
     * Get photos from Firebase database
     */
    private void getPhotosFromFirebase(){
        PhotoHelper.getPhotos().addOnCompleteListener( task -> {
            photosFirebase = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null){
                photosFirebase.addAll(task.getResult().toObjects(Photo.class));
            }
            syncData();
        });
    }

    /**
     * Synchronize data between databases
     */
    private void syncData() {
        // Download photos from Firebase database
        for (Photo photoFirebase : photosFirebase){
            final int index = photosRoom.indexOf(photoFirebase);
            if (index < 0){
                if (propertiesDown.contains(photoFirebase.getPropertyID())) {
                    addPhotoInRoom(photoFirebase);
                } else {
                    deletePhotoFromFirebase(photoFirebase);
                }
            } else {
                photosRoom.remove(index);
            }
        }
        // Upload photos in Firebase database
        for (Photo photoRoom : photosRoom){
            if (propertiesDown.contains(photoRoom.getPropertyID())){
                deletePhotoFromRoom(photoRoom);
            } else {
                addPhotoToFirebase(photoRoom);
            }
        }
        callback.photosSynchronized();
    }

    /**
     * Delete the photo from the locale database
     *
     * @param photo Photo to delete
     */
    private void deletePhotoFromRoom(Photo photo) {
        propertyViewModel.deletePhoto(photo);
        new File(photo.getUri(context)).deleteOnExit();
    }

    /**
     * Upload the photo on the firebase database
     *
     * @param photo Photo to upload
     */
    private void addPhotoToFirebase(Photo photo) {
        // Add photo to database
        PhotoHelper.updatePhoto(photo.getHash(), photo).addOnFailureListener(callback::error);
        // Add photo to storage
        StoragePhotoHelper.putFileOnFirebaseStorage(photo.getPropertyID(), photo.getHash(), photo.getUri(context))
                .addOnFailureListener(callback::error);
    }

    /**
     * Delete photo from the firebase database
     *
     * @param photo Photo to delete
     */
    private void deletePhotoFromFirebase(Photo photo) {
        StoragePhotoHelper.deleteFileFromFirebaseStorage(photo.getPropertyID(), photo.getHash())
                .addOnFailureListener(callback::error);

        PhotoHelper.deletePhoto(photo.getHash()).addOnFailureListener(callback::error);
    }

    /**
     * Add photo from the firebase database in the local database
     *
     * @param photo Photo
     */
    private void addPhotoInRoom(Photo photo) {
        StoragePhotoHelper.savePictureToFile(photo.getPropertyID(), photo.getHash(), photo.getUri(context)).addOnSuccessListener(voidTask ->
                propertyViewModel.insertPropertyPhoto(photo)
        ).addOnFailureListener(callback::error);
    }
}