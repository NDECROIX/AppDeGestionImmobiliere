package com.openclassrooms.realestatemanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class POI {

    @PrimaryKey
    @ColumnInfo(name = "name")
    private String name;

    public POI(String name) {
        this.name = name;
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
