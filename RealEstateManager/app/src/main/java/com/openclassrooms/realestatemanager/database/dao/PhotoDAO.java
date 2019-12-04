package com.openclassrooms.realestatemanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Dao;

import com.openclassrooms.realestatemanager.model.Photo;

import java.util.List;

@Dao
public interface PhotoDAO {

    @Query("SELECT * FROM photo WHERE property_id LIKE :propertyID")
    LiveData<List<Photo>> getPropertyPhotos(long propertyID);

    @Insert
    void insertPhotos(Photo photo);
}
