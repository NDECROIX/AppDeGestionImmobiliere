package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.POINextProperty;

import java.util.List;

public interface PoiNextPropertyDAO {

    @Query("SELECT * FROM POINextProperty WHERE property_id LIKE :propertyID")
    LiveData<List<POINextProperty>> getPoiNextProperty(long propertyID);

    @Query("SELECT * FROM POINextProperty")
    Cursor getAllWithCursor();

    @Insert
    void insertPoiNextProperty(POINextProperty poiNextProperty);
}
