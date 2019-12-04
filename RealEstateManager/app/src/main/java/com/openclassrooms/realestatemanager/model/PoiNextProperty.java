package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

@Entity(primaryKeys = {"property_id", "poi_name"},
        foreignKeys = @ForeignKey(entity = Property.class,
                parentColumns = "id",
                childColumns = "property_id"),
        indices = @Index(value = "property_id"))
public class PoiNextProperty {

    /**
     * Refers to a real estate property
     */
    @ColumnInfo(name = "property_id")
    private long propertyID;

    /**
     * Name of the point of interest next to the property
     */
    @NonNull
    @ColumnInfo(name = "poi_name")
    private String poiName;

    public PoiNextProperty(long propertyID,@NonNull String poiName) {
        this.propertyID = propertyID;
        this.poiName = poiName;
    }

    @Ignore
    public PoiNextProperty() {
    }

    // --- UTILS ---

    public static PoiNextProperty fromContentValues(ContentValues values) {
        final PoiNextProperty poiNextProperty = new PoiNextProperty();
        if (values.containsKey("propertyID")) poiNextProperty.setPropertyID(values.getAsLong("propertyID"));
        if (values.containsKey("poiName")) poiNextProperty.setPoiName(values.getAsString("poiName"));
        return poiNextProperty;
    }

    // --- GETTER ---

    public long getPropertyID() {
        return propertyID;
    }

    public String getPoiName() {
        return poiName;
    }

    // --- SETTER ---

    public void setPropertyID(long propertyID) {
        this.propertyID = propertyID;
    }

    public void setPoiName(String poiName) {
        this.poiName = poiName;
    }
}
