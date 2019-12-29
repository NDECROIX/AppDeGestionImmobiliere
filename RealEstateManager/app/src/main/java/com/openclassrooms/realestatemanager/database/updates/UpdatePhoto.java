package com.openclassrooms.realestatemanager.database.updates;

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

class UpdatePhoto {

    public interface UpdatePhotoListener {
        void photosSynchronized();

        void error(Exception exception);
    }

    private List<Photo> photosRoom;
    private final PropertyViewModel propertyViewModel;
    private UpdatePhotoListener callback;
    private LifecycleOwner lifecycleOwner;
    private List<String> propertiesDown;
    private int count = 0;

    UpdatePhoto(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel,
                UpdatePhotoListener callback, List<String> propertiesDown) {
        this.callback = callback;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
        this.propertiesDown = new ArrayList<>(propertiesDown);
    }

    void updateData() {
        this.propertyViewModel.getPhotos().observe(lifecycleOwner, new Observer<List<Photo>>() {
            @Override
            public void onChanged(List<Photo> photos) {
                propertyViewModel.getPhotos().removeObserver(this);
                photosRoom = new ArrayList<>(photos);
                if (!photosRoom.isEmpty()) {
                    updatePhotos();
                } else {
                    getNewPhotosFromFirebase();
                }
            }
        });
    }

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

    private void deletePhotoFromRoom(Photo photo) {
        propertyViewModel.deletePhoto(photo);
    }

    private void addPhotoToFirebase(Photo photo) {
        // Add photo to database
        PhotoHelper.updatePhoto(photo.getHash(), photo).addOnFailureListener(callback::error);
        // Add photo to storage
        StoragePhotoHelper.putFileOnFirebaseStorage(photo.getPropertyID(), photo.getUri())
                .addOnFailureListener(callback::error);
    }

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

    private void addPhotoInRoom(Photo photo) {
        StoragePhotoHelper.getUrlPicture(photo.getPropertyID(), photo.getUri()).addOnCompleteListener(task -> {
            Uri downloadUri = task.getResult();
            if (downloadUri != null) {
                Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    Bitmap bitmap = UtilsPhoto.getBitmapFromURL(downloadUri.toString());
                    if (bitmap != null) {
                        StoragePhotoHelper.savePictureToFile(photo)
                                .addOnFailureListener(callback::error);
                        System.out.println("ERROR PATH : " + photo.getUri());
                    }
                });
            }
        }).addOnFailureListener(callback::error);
        propertyViewModel.insertPropertyPhoto(photo);
    }
}