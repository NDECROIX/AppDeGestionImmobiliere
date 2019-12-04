package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class POI {

    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;

    public POI(String name) {
        this.name = name;
    }

    @Ignore
    public POI(){

    }

    // --- UTILS ---

    public static POI fromContentValues(ContentValues values) {
        final POI poi = new POI();
        if (values.containsKey("name")) poi.setName(values.getAsString("name"));
        return poi;
    }

    // --- GETTER ---
    public String getName() {
        return name;
    }

    // --- SETTER ---
    public void setName(String name) {
        this.name = name;
    }
}
