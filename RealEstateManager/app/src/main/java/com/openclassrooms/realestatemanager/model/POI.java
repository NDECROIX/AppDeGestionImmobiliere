package com.openclassrooms.realestatemanager.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class POI {
    @PrimaryKey private String name;
}
