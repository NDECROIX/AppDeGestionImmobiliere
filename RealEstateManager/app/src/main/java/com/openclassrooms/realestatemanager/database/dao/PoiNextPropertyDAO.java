package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;

import java.util.List;

@Dao
public interface PoiNextPropertyDAO {

    @Query("SELECT * FROM PoiNextProperty WHERE property_id LIKE :propertyID")
    LiveData<List<PoiNextProperty>> getPoiNextProperty(String propertyID);

    @Query("SELECT * FROM PoiNextProperty")
    Cursor getAllWithCursor();

    @Insert
    void insertPoiNextProperty(PoiNextProperty poiNextProperty);

    @Delete
    void deletePoiNextProperty(PoiNextProperty poiNextProperty);
}
