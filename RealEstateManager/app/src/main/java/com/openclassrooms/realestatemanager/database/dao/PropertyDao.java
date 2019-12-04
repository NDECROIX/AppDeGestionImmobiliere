package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.openclassrooms.realestatemanager.model.Property;

import java.util.List;

public interface PropertyDao {

    @Query("SELECT * FROM property")
    LiveData<List<Property>> getAll();

    @Query("SELECT * FROM property")
    Cursor getAllWithCursor();

    @Insert
    void insertProperty(Property property);

    @Update
    void updateProperty(Property property);

}
