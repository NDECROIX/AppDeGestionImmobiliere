package com.openclassrooms.nycrealestatemanager.database.dao;

import android.database.Cursor;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.openclassrooms.nycrealestatemanager.model.Type;

import java.util.List;

@Dao
public interface TypeDAO {

    @Query("SELECT * FROM Type")
    LiveData<List<Type>> getAll();

    @Query("SELECT * FROM Type")
    Cursor getAllWithCursor();

}
