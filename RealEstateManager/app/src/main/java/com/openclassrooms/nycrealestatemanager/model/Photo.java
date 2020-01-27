package com.openclassrooms.nycrealestatemanager.model;

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
import com.openclassrooms.nycrealestatemanager.utils.Utils;

import java.io.File;
import java.util.Objects;

/**
 * Representation of a photo
 */
@SuppressWarnings("unused, NullableProblems")
@Entity(primaryKeys = {"name", "property_id"},
        foreignKeys = @ForeignKey(
                entity = Property.class,
                parentColumns = "id",
                childColumns = "property_id"),
        indices = @Index(value = "property_id"))
public class Photo implements Parcelable {

    // Photo name
    @ColumnInfo(name = "name")
    @NonNull
    private String name;

    // Property owner
    @NonNull
    @ColumnInfo(name = "property_id")
    private String propertyID;

    // Short photo description
    @ColumnInfo(name = "description")
    private String description;

    /**
     * Constructor
     *
     * @param name        Path where the photo is located
     * @param propertyID  Id of the property owner
     * @param description Short photo description
     */
    public Photo(@NonNull String name, @NonNull String propertyID, String description) {
        this.name = name;
        this.propertyID = propertyID;
        this.description = description;
    }

    @Ignore
    public Photo() {

    }

    // --- UTILS ---

    /**
     * Hash to identify the photo in the firebase database
     *
     * @return String hash
     */
    @Ignore
    @Exclude
    public String getHash() {
        String value = this.name;
        return Utils.convertStringMd5(value);
    }

    /**
     * Return the name of the photo
     *
     * @return Photo name
     */
    @Ignore
    @Exclude
    public static String getName(String path) {
        String[] splitUri = path.split("/");
        return splitUri[splitUri.length - 1];
    }

    // --- GETTER ---

    /**
     * Get the path of the photo
     *
     * @param context Context
     * @return Path of the photo
     */
    public String getUri(Context context) {
        String uri = "";
        File file = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (file != null) {
            uri = file.getAbsolutePath();
            uri += "/" + this.getName();
        }
        return uri;
    }

    public @NonNull
    String getName() {
        return name;
    }

    public @NonNull
    String getPropertyID() {
        return propertyID;
    }

    public String getDescription() {
        return description;
    }

    // --- SETTER ---

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public void setPropertyID(@NonNull String propertyID) {
        this.propertyID = propertyID;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return name.equals(photo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    // ----------
    // PARCELABLE
    // ----------

    @SuppressWarnings("ConstantConditions")
    protected Photo(Parcel in) {
        name = in.readString();
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
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(propertyID);
        parcel.writeString(description);
    }
}
