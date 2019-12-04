package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Poi {

    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;

    public Poi(String name) {
        this.name = name;
    }

    @Ignore
    public Poi(){

    }

    // --- UTILS ---

    public static Poi fromContentValues(ContentValues values) {
        final Poi poi = new Poi();
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
