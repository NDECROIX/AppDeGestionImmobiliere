package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
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

import java.io.File;
import java.util.Objects;

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
        this.description = description;
    }

    @Ignore
    public Photo() {

    }

    // --- UTILS ---

    @Ignore
    @Exclude
    public String getHash() {
        String value = this.uri;
        return Utils.convertStringMd5(value);
    }

    @Ignore
    @Exclude
    public String getName() {
        String[] splitUri = this.uri.split("/");
        return splitUri[splitUri.length - 1];
    }

    public static Photo fromContentValues(ContentValues values) {
        final Photo photo = new Photo();
        if (values.containsKey("uri")) photo.setUri(values.getAsString("uri"));
        if (values.containsKey("propertyID")) photo.setPropertyID(values.getAsString("propertyID"));
        if (values.containsKey("description"))
            photo.setDescription(values.getAsString("description"));
        return photo;
    }

    // --- GETTER ---

    public String getUri(Context context) {
        String uri = "";
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null) {
            uri = file.getAbsolutePath();
            uri += "/" + this.getName();
        }
        return uri;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return uri.equals(photo.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uri);
    }
}
