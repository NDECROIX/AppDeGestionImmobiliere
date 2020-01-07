package com.openclassrooms.realestatemanager.database;

import android.content.ContentValues;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.room.Database;
import androidx.room.OnConflictStrategy;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.openclassrooms.realestatemanager.database.dao.AgentDao;
import com.openclassrooms.realestatemanager.database.dao.PhotoDAO;
import com.openclassrooms.realestatemanager.database.dao.PoiDAO;
import com.openclassrooms.realestatemanager.database.dao.PoiNextPropertyDAO;
import com.openclassrooms.realestatemanager.database.dao.PropertyDAO;
import com.openclassrooms.realestatemanager.database.dao.TypeDAO;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.Type;

/**
 * Application database
 */
@Database(entities = {Property.class, Photo.class, Poi.class, PoiNextProperty.class, Type.class, Agent.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    //--- SINGLETON ---
    private static volatile AppDatabase INSTANCE;

    //--- DAO ---
    public abstract PhotoDAO photoDAO();

    public abstract PoiDAO poiDAO();

    public abstract PoiNextPropertyDAO poiNextPropertyDAO();

    public abstract PropertyDAO propertyDAO();

    public abstract TypeDAO typeDAO();

    public abstract AgentDao agentDAO();

    /**
     * get a singleton instance of the database
     *
     * @param context Application context
     * @return AppDatabase
     */
    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "DatabaseREM.db")
                            .addCallback(prePopulateDatabase())
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Prepopulate database with points of interest and types
     *
     * @return Callback
     */
    @VisibleForTesting
    public static Callback prePopulateDatabase() {
        return new Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);
                for (Poi poi : Poi.getAllPoi()) {
                    db.insert("Poi", OnConflictStrategy.IGNORE, insertPoi(poi));
                }
                for (Type type : Type.getAllTypes()) {
                    db.insert("Type", OnConflictStrategy.IGNORE, insertType(type));
                }
            }
        };
    }

    /**
     * Prepopulate the database with the points of interest
     *
     * @param poi poi to add
     * @return ContentValues
     */
    private static ContentValues insertPoi(Poi poi) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", poi.getName());
        return contentValues;
    }

    /**
     * Prepopulate the database with type
     *
     * @param type type to add
     * @return ContentValues
     */
    private static ContentValues insertType(Type type) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", type.getName());
        return contentValues;
    }
}
