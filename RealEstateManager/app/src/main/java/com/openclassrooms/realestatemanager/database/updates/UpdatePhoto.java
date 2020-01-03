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
    // Call on the api one after one to respect the unique constraint
    private int count = 0;
    // Activity context
    private Context context;

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
                    if (!photosRoom.isEmpty()) {
                        updatePhotos();
                    } else {
                        getNewPhotosFromFirebase();
                    }
                }
                propertyViewModel.getPhotos().removeObserver(this);
            }
        });
    }

    /**
     * Compare local photos with distance photos.
     * If photo does not exist on the firebase database and the property has been updated from
     * the firebase database, delete the photo from the local database, otherwise upload it on
     * the firebase database.
     */
    private void updatePhotos() {
        if (count >= photosRoom.size()) return;
        final int count = this.count;
        PhotoHelper.getPhoto(photosRoom.get(count).getHash()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Photo photoFirebase = task.getResult().toObject(Photo.class);
                if (photoFirebase == null) {
                    if (propertiesDown.contains(photosRoom.get(count).getPropertyID())) {
                        deletePhotoFromRoom(photosRoom.get(count));
                    } else {
                        addPhotoToFirebase(photosRoom.get(count));
                    }
                }
            } else if (task.isSuccessful() && task.getResult() == null) {
                if (propertiesDown.contains(photosRoom.get(count).getPropertyID())) {
                    deletePhotoFromRoom(photosRoom.get(count));
                } else {
                    addPhotoToFirebase(photosRoom.get(count));
                }
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            this.count++;
            if (this.count < photosRoom.size()) {
                updatePhotos();
            } else {
                getNewPhotosFromFirebase();
            }
        });
    }

    /**
     * Delete the photo from the locale database
     *
     * @param photo Photo to delete
     */
    private void deletePhotoFromRoom(Photo photo) {
        propertyViewModel.deletePhoto(photo);
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
        StoragePhotoHelper.putFileOnFirebaseStorage(photo.getPropertyID(), photo.getUri())
                .addOnFailureListener(callback::error);
    }

    /**
     * Get photos from the firebase database that does not exist in the local database
     */
    private void getNewPhotosFromFirebase() {
        PhotoHelper.getPhotos().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Photo> photos = new ArrayList<>(task.getResult().toObjects(Photo.class));
                for (Photo photo : photos) {
                    if (!photosRoom.contains(photo)) {
                        if (propertiesDown.contains(photo.getPropertyID())) {
                            addPhotoInRoom(photo);
                        } else {
                            deletePhotoFromFirebase(photo);
                        }

                    }
                }
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            callback.photosSynchronized();
        });
    }

    /**
     * Delete photo from the firebase database
     *
     * @param photo Photo to delete
     */
    private void deletePhotoFromFirebase(Photo photo) {
        StoragePhotoHelper.getStorageReference(photo.getPropertyID()).listAll().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                for (StorageReference reference : task.getResult().getItems()) {
                    if (reference.getPath().contains(photo.getHash())) {
                        StoragePhotoHelper.deleteFileFromFirebaseStorage(reference.getPath());
                        break;
                    }
                }
            }
        }).addOnFailureListener(callback::error);

        PhotoHelper.deletePhoto(photo.getHash()).addOnFailureListener(callback::error);
    }

    /**
     * Add photo from the firebase database in the local database
     *
     * @param photo Photo
     */
    private void addPhotoInRoom(Photo photo) {
        StoragePhotoHelper.getUrlPicture(photo.getPropertyID(), photo.getUri()).addOnCompleteListener(task -> {
            Uri downloadUri = task.getResult();
            if (downloadUri != null) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    Bitmap bitmap = UtilsPhoto.getBitmapFromURL(downloadUri.toString());
                    if (bitmap != null) {
                        String path = photo.getUri(context);
                        StoragePhotoHelper.savePictureToFile(photo, path).addOnSuccessListener(voidTask ->
                                propertyViewModel.insertPropertyPhoto(photo)
                        ).addOnFailureListener(callback::error);
                    }
                });
            }
        }).addOnFailureListener(callback::error);
    }
}