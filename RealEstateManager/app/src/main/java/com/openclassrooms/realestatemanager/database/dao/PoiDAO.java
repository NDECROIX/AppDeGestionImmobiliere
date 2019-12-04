package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.POI;

import java.util.List;

public interface PoiDAO {

    @Query("SELECT * FROM poi")
    LiveData<List<POI>> getAll();

    @Query("SELECT * FROM poi")
    Cursor getAllWithCursor();

    @Insert
    void insertPOI(POI poi);
}
