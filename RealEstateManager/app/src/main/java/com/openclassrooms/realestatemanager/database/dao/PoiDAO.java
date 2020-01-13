package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.Poi;

import java.util.List;

@Dao
public interface PoiDAO {

    @Query("SELECT * FROM Poi")
    LiveData<List<Poi>> getAll();

    @Query("SELECT * FROM Poi")
    Cursor getAllWithCursor();

}
