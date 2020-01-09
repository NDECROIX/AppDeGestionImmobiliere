package com.openclassrooms.realestatemanager.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Point of interest
 */
@SuppressWarnings("NullableProblems")
@Entity
public class Poi {

    // Name
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    public Poi(String name) {
        this.name = name;
    }

    @Ignore
    public Poi() {

    }

    /**
     * return all points of interest
     *
     * @return List of point of interest
     */
    @Ignore
    public static Poi[] getAllPoi() {
        return new Poi[]{
                new Poi("School"),
                new Poi("Police"),
                new Poi("Shops"),
                new Poi("Park"),
                new Poi("Gym"),
                new Poi("Restaurant"),
                new Poi("Garage"),
                new Poi("Church"),
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Poi poi = (Poi) o;
        return name.toLowerCase().equals(poi.name.toLowerCase());
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
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