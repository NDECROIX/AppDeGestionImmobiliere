package com.openclassrooms.realestatemanager.model;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

/**
 * Type of property
 */
@SuppressWarnings("NullableProblems")
@Entity
public class Type {

    // Name
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "name")
    private String name;

    public Type(String name) {
        this.name = name;
    }

    @Ignore
    public Type() {

    }

    /**
     * Get all types of property
     *
     * @return List of property types
     */
    @Ignore
    public static Type[] getAllTypes() {
        return new Type[]{
                new Type("Apartment"),
                new Type("House"),
                new Type("Manor"),
                new Type("Penthouse"),
                new Type("Loft"),
                new Type("Duplex"),
                new Type("HOA"),
                new Type("COA")
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Type type = (Type) o;
        return name.toLowerCase().equals(type.name.toLowerCase());
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