package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Dao;

import com.openclassrooms.realestatemanager.model.Photo;

import java.util.List;

@Dao
public interface PhotoDAO {

    @Query("SELECT * FROM Photo WHERE property_id LIKE :propertyID")
    LiveData<List<Photo>> getPropertyPhotos(long propertyID);

    @Query("SELECT * FROM photo")
    Cursor getAllWithCursor();

    @Insert
    void insertPhotos(Photo photo);
}
