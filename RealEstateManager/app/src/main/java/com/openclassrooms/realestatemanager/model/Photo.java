package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(primaryKeys = {"uri", "property_id"},
        foreignKeys = @ForeignKey(
                entity = Property.class,
                parentColumns = "id",
                childColumns = "property_id"),
        indices = @Index(value = "property_id"))
public class Photo {

    @ColumnInfo(name = "uri")
    @NonNull
    private String uri;

    @ColumnInfo(name = "property_id")
    private long propertyID;

    @ColumnInfo(name = "description")
    private String description;

    public Photo(@NonNull String uri, @NonNull long propertyID, String description) {
        this.uri = uri;
        this.propertyID = propertyID;
        this.description =description;
    }

    @Ignore
    public Photo() {

    }

    // --- UTILS ---

    public static Photo fromContentValues(ContentValues values) {
        final Photo photo = new Photo();
        if (values.containsKey("uri")) photo.setUri(values.getAsString("uri"));
        if (values.containsKey("propertyID")) photo.setPropertyID(values.getAsLong("propertyID"));
        if (values.containsKey("description")) photo.setDescription(values.getAsString("description"));
        return photo;
    }

    // --- GETTER ---

    public String getUri() {
        return uri;
    }

    public long getPropertyID() {
        return propertyID;
    }

    public String getDescription() {
        return description;
    }

    // --- SETTER ---

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPropertyID(long propertyID) {
        this.propertyID = propertyID;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
