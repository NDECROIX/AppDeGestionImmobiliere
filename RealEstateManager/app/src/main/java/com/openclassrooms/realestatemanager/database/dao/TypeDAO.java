package com.openclassrooms.realestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.openclassrooms.realestatemanager.model.Type;

import java.util.List;

@Dao
public interface TypeDAO {

    @Query("SELECT * FROM Type")
    LiveData<List<Type>> getAll();

    @Query("SELECT * FROM Type")
    Cursor getAllWithCursor();

}
