package com.openclassrooms.realestatemanager.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

@Entity(primaryKeys = {"propertyID", "poiName"},
        foreignKeys = @ForeignKey(entity = Property.class,
                parentColumns = "id",
                childColumns = "propertyID"),
        indices = @Index(value = "propertyID"))
public class POINextProperty {
    private long propertyID;
    private String poiName;
}
