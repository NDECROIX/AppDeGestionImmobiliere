package com.openclassrooms.nycrealestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.nycrealestatemanager.database.dao.PhotoDAO;
import com.openclassrooms.nycrealestatemanager.model.Photo;

import java.util.List;

/**
 * Repository of photos
 */
public class PhotoDataRepository {

    private final PhotoDAO photoDAO;

    public PhotoDataRepository(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    /**
     * Get all Property photos from the AppDatabase
     *
     * @param propertyID Property ID
     * @return List of all photos
     */
    public LiveData<List<Photo>> getPropertyPhotos(String propertyID) {
        return this.photoDAO.getPropertyPhotos(propertyID);
    }

    /**
     * Get all photos from the AppDatabase
     *
     * @return All photos
     */
    public LiveData<List<Photo>> getPhotos() {
        return this.photoDAO.getPhotos();
    }

    /**
     * Insert a Property photo
     *
     * @param photo New Photo
     */
    public void insertPropertyPhoto(Photo photo) {
        this.photoDAO.insertPhoto(photo);
    }

    /**
     * Delete a photo from the database
     * @param photo Photo to delete
     */
    public void deletePhoto(Photo photo){
        this.photoDAO.deletePhoto(photo);
    }
}