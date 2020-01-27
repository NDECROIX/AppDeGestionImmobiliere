package com.openclassrooms.nycrealestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Dao;

import com.openclassrooms.nycrealestatemanager.model.Photo;

import java.util.List;

@Dao
public interface PhotoDAO {

    @Query("SELECT * FROM Photo WHERE property_id LIKE :propertyID")
    LiveData<List<Photo>> getPropertyPhotos(String propertyID);

    @Query("SELECT * FROM photo")
    LiveData<List<Photo>> getPhotos();

    @Query("SELECT * FROM photo")
    Cursor getAllWithCursor();

    @Insert
    void insertPhoto(Photo photo);

    @Delete
    void deletePhoto(Photo photo);
}
