package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
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

    @Ignore
    public Photo(){

    }

    // --- UTILS ---

    public static Photo fromContentValues(ContentValues values) {
        final Photo photo = new Photo();
        if (values.containsKey("uri")) photo.setUri(values.getAsString("uri"));
        if (values.containsKey("propertyID")) photo.setPropertyID(values.getAsLong("propertyID"));
        return photo;
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
