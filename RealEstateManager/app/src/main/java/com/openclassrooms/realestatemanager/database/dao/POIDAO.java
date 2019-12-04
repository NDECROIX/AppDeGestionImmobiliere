package com.openclassrooms.realestatemanager.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.POI;

import java.util.List;

public interface POIDAO {

    @Query("SELECT * FROM poi")
    LiveData<List<POI>> getAll();

    @Insert
    void insertPOI(POI poi);
}
