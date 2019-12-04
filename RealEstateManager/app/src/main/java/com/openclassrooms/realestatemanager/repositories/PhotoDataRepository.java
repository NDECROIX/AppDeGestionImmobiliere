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
     * @return List of all photos
     */
    public LiveData<List<Photo>> getPropertyPhotos(long propertyID) {
        return this.photoDAO.getPropertyPhotos(propertyID);
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
