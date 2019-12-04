package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.PoiNextProperty;

import java.util.List;

public interface PoiNextPropertyDAO {

    @Query("SELECT * FROM PoiNextProperty WHERE property_id LIKE :propertyID")
    LiveData<List<PoiNextProperty>> getPoiNextProperty(long propertyID);

    @Query("SELECT * FROM PoiNextProperty")
    Cursor getAllWithCursor();

    @Insert
    void insertPoiNextProperty(PoiNextProperty poiNextProperty);
}
