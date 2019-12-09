package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity
public class Type {

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

    @Ignore
    public static Type[] getAllTypes(){
        return new Type[]{
                new Type("Apartment"),
                new Type("House"),
                new Type("Penthouse"),
                new Type("Duplex"),
                new Type("HOA"),
                new Type("COA")
        };
    }

    // --- UTILS ---

    public static Type fromContentValues(ContentValues values) {
        final Type type = new Type();
        if (values.containsKey("name")) type.setName(values.getAsString("name"));
        return type;
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
