package com.openclassrooms.realestatemanager.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.openclassrooms.realestatemanager.database.dao.PhotoDAO;
import com.openclassrooms.realestatemanager.database.dao.PoiDAO;
import com.openclassrooms.realestatemanager.database.dao.PoiNextPropertyDAO;
import com.openclassrooms.realestatemanager.database.dao.PropertyDAO;
import com.openclassrooms.realestatemanager.database.dao.TypeDAO;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;

@Database(entities = {Property.class, Photo.class, Poi.class, PoiNextProperty.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    //--- SINGLETON ---
    private static volatile AppDatabase INSTANCE;

    //--- DAO ---
    public abstract PhotoDAO photoDAO();

    public abstract PoiDAO poiDAO();

    public abstract PoiNextPropertyDAO poiNextPropertyDAO();

    public abstract PropertyDAO propertyDAO();

    public abstract TypeDAO typeDAO();

    //--- INSTANCE ---
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "DatabaseREM.db").build();
                }
            }
        }
        return INSTANCE;
    }
}
