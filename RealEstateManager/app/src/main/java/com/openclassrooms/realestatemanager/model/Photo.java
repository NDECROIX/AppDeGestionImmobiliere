package com.openclassrooms.realestatemanager.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = @ForeignKey(
        entity = Property.class,
        parentColumns = "id",
        childColumns = "property_id"),
        indices = @Index(value = "property_id"))
public class Photo {
    @PrimaryKey
    @ColumnInfo(name = "uri")
    private String uri;

    @PrimaryKey
    @ColumnInfo (name = "property_id")
    private long propertyID;

    public Photo(String uri, long propertyID) {
        this.uri = uri;
        this.propertyID = propertyID;
    }

    // --- GETTER ---

    public String getUri() {
        return uri;
    }

    public long getPropertyID() {
        return propertyID;
    }

    // --- SETTER ---

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPropertyID(long propertyID) {
        this.propertyID = propertyID;
    }
}
