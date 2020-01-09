package com.openclassrooms.realestatemanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;

import com.google.firebase.firestore.Exclude;
import com.openclassrooms.realestatemanager.utils.Utils;

import java.util.Objects;

/**
 * Link point of interest with a property
 */
@SuppressWarnings("NullableProblems")
@Entity(primaryKeys = {"property_id", "poi_name"},
        foreignKeys = @ForeignKey(entity = Property.class,
                parentColumns = "id",
                childColumns = "property_id"),
        indices = @Index(value = "property_id"))
public class PoiNextProperty implements Parcelable {

    /**
     * Refers to a real estate property
     */
    @NonNull
    @ColumnInfo(name = "property_id")
    private String propertyID;

    /**
     * Name of the point of interest next to the property
     */
    @NonNull
    @ColumnInfo(name = "poi_name")
    private String poiName;

    public PoiNextProperty(@NonNull String propertyID, @NonNull String poiName) {
        this.propertyID = propertyID;
        this.poiName = poiName;
    }

    @Ignore
    public PoiNextProperty() {
    }

    /**
     * Create hash with propertyId and poi name to create an unique id for firebase database
     *
     * @return Unique id
     */
    @Ignore
    @Exclude
    public String getHash() {
        String value = this.propertyID + this.poiName;
        return Utils.convertStringMd5(value);
    }

    @SuppressWarnings("ConstantConditions")
    protected PoiNextProperty(Parcel in) {
        propertyID = in.readString();
        poiName = in.readString();
    }

    public static final Creator<PoiNextProperty> CREATOR = new Creator<PoiNextProperty>() {
        @Override
        public PoiNextProperty createFromParcel(Parcel in) {
            return new PoiNextProperty(in);
        }

        @Override
        public PoiNextProperty[] newArray(int size) {
            return new PoiNextProperty[size];
        }
    };

    // --- GETTER ---

    public @NonNull
    String getPropertyID() {
        return propertyID;
    }

    public @NonNull
    String getPoiName() {
        return poiName;
    }

    // --- SETTER ---

    public void setPropertyID(@NonNull String propertyID) {
        this.propertyID = propertyID;
    }

    public void setPoiName(@NonNull String poiName) {
        this.poiName = poiName;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(propertyID);
        dest.writeString(poiName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PoiNextProperty that = (PoiNextProperty) o;
        return propertyID.equals(that.propertyID) &&
                poiName.equals(that.poiName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyID, poiName);
    }
}
