package com.openclassrooms.realestatemanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.POINextProperty;

import java.util.List;

public interface POINextPropertyDAO {

    @Query("SELECT * FROM POINextProperty WHERE propertyID LIKE :propertyID")
    LiveData<List<POINextProperty>> getPoiNextProperty(long propertyID);

    @Insert
    void insertPoiNextProperty(POINextProperty poiNextProperty);
}
