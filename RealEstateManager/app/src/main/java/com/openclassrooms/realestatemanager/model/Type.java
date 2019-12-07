package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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

    // --- UTILS ---

    public static Type fromContentValues(ContentValues values) {
        final Type type = new Type();
        if (values.containsKey("name")) type.setName(values.getAsString("name"));
        return type;
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
