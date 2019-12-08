package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
public class Poi {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    public Poi(String name) {
        this.name = name;
    }

    @Ignore
    public Poi(){

    }

    @Ignore
    public static Poi[] getAllPoi(){
        return new Poi[]{
                new Poi("School"),
                new Poi("Police"),
                new Poi("Shops"),
                new Poi("Park"),
                new Poi("Gym"),
                new Poi("Restaurant"),
                new Poi("Garage"),
                new Poi("Churches"),
                new Poi("Bank"),
                new Poi("Cafe"),
                new Poi("Dentist"),
                new Poi("Doctors"),
                new Poi("Theatre"),
                new Poi("Cinema"),
                new Poi("Nightclub"),
                new Poi("Casino")
        };
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
