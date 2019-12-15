package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

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
public class Photo implements Parcelable {

    @ColumnInfo(name = "uri")
    @NonNull
    private String uri;

    @NonNull
    @ColumnInfo(name = "property_id")
    private String propertyID;

    @ColumnInfo(name = "description")
    private String description;

    public Photo(@NonNull String uri, @NonNull String propertyID, String description) {
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
        if (values.containsKey("propertyID")) photo.setPropertyID(values.getAsString("propertyID"));
        if (values.containsKey("description")) photo.setDescription(values.getAsString("description"));
        return photo;
    }

    // --- GETTER ---

    public String getUri() {
        return uri;
    }

    public String getPropertyID() {
        return propertyID;
    }

    public String getDescription() {
        return description;
    }

    // --- SETTER ---

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPropertyID(String propertyID) {
        this.propertyID = propertyID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uri);
        dest.writeString(propertyID);
        dest.writeString(description);
    }

    protected Photo(Parcel in) {
        uri = in.readString();
        propertyID = in.readString();
        description = in.readString();
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };
}
