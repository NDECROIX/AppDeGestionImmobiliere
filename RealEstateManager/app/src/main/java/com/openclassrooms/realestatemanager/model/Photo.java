package com.openclassrooms.realestatemanager.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Property.class,
        parentColumns = "id",
        childColumns = "propertyID"),
        indices = @Index(value = "projectID"))
public class Photo {
    @PrimaryKey private String uri;
    @PrimaryKey private long propertyID;
}
