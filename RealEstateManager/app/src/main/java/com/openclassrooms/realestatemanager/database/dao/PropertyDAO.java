package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.model.Property;

import java.util.List;

@Dao
public interface PropertyDAO {

    @Query("SELECT * FROM Property")
    LiveData<List<Property>> getAll();

    @Query("SELECT * FROM Property WHERE id LIKE :propertyID")
    LiveData<Property> getProperty(String propertyID);

    @Query("SELECT * FROM property")
    Cursor getAllWithCursor();

    @Insert
    void insertProperty(Property property);

    @Update
    void updateProperty(Property property);
}
