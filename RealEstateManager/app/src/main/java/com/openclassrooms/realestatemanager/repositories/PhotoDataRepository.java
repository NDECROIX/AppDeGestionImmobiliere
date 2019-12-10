package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.PhotoDAO;
import com.openclassrooms.realestatemanager.model.Photo;

import java.util.List;

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
        return this.photoDAO.getAll();
    }

    /**
     * Insert a Property photo
     *
     * @param photo New Photo
     */
    public void insertPropertyPhoto(Photo photo) {
        this.photoDAO.insertPhotos(photo);
    }
}
