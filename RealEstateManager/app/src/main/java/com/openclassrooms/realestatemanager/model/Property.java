package com.openclassrooms.realestatemanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.firebase.firestore.Exclude;
import com.openclassrooms.realestatemanager.utils.Utils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * representation of a property
 */
@SuppressWarnings("NullableProblems")
@Entity
public class Property implements Parcelable {

    /**
     * The unique identifier of property
     */
    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "id")
    private String id;

    /**
     * Type of property.
     */
    @ForeignKey(entity = Type.class, parentColumns = "name", childColumns = "type")
    @ColumnInfo(name = "type")
    private String type;

    /**
     * Borough of the property
     */
    @ColumnInfo(name = "borough")
    private String borough;

    /**
     * Real estate price.
     */
    @ColumnInfo(name = "price")
    private Double price;

    /**
     * Surface in square meters m2.
     */
    @ColumnInfo(name = "surface")
    private Double surface;

    /**
     * The number of rooms.
     */
    @ColumnInfo(name = "rooms")
    private int rooms;

    /**
     * The number of bathrooms.
     */
    @ColumnInfo(name = "bathrooms")
    private int bathrooms;

    /**
     * The number of bedrooms.
     */
    @ColumnInfo(name = "bedrooms")
    private int bedrooms;

    /**
     * Description of the property.
     */
    @ColumnInfo(name = "description")
    private String description;

    /**
     * Street number of the property.
     */
    @ColumnInfo(name = "street_number")
    private int streetNumber;

    /**
     * Street name of the property.
     */
    @ColumnInfo(name = "street_name")
    private String streetName;

    /**
     * Address supplement of the property.
     */
    @ColumnInfo(name = "address_supplement")
    private String addressSupplement;

    /**
     * City of the property.
     */
    @ColumnInfo(name = "city")
    private String city;

    /**
     * Zip code of the property.
     */
    @ColumnInfo(name = "zip")
    private int zip;

    /**
     * Country of the property.
     */
    @ColumnInfo(name = "country")
    private String country;

    /**
     * Status of the property. True if the property has been sold.
     */
    @ColumnInfo(name = "sold")
    private boolean sold;

    /**
     * The date on which the property entered the market.
     */
    @ColumnInfo(name = "entry_date")
    private long entryDate;

    /**
     * Date on which the property was sold.
     */
    @ColumnInfo(name = "sale_date")
    private long saleDate;

    /**
     * Property update date
     */
    @ColumnInfo(name = "update_date")
    private long updateDate;

    /**
     * Latitude address
     */
    @ColumnInfo(name = "latitude")
    private Double latitude;

    /**
     * Longitude address
     */
    @ColumnInfo(name = "longitude")
    private Double longitude;

    /**
     * The real estate agent in charge of this property.
     */
    @ColumnInfo(name = "agent_id")
    @ForeignKey(entity = Agent.class, parentColumns = "id", childColumns = "agentID")
    private String agentID;

    public Property(@NonNull String id, String type, String borough, Double price, Double surface, int rooms, int bathrooms,
                    int bedrooms, String description, int streetNumber, String streetName,
                    String addressSupplement, String city, int zip, String country, boolean sold, long entryDate, long saleDate, long updateDate, Double latitude,
                    Double longitude, String agentID) {
        this.id = id;
        this.type = type;
        this.borough = borough;
        this.price = price;
        this.surface = surface;
        this.rooms = rooms;
        this.bathrooms = bathrooms;
        this.bedrooms = bedrooms;
        this.description = description;
        this.streetNumber = streetNumber;
        this.streetName = streetName;
        this.addressSupplement = addressSupplement;
        this.zip = zip;
        this.city = city;
        this.country = country;
        this.sold = sold;
        this.entryDate = entryDate;
        this.saleDate = saleDate;
        this.updateDate = updateDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.agentID = agentID;
    }

    @Ignore
    public Property() {
    }


    @SuppressWarnings("ConstantConditions")
    protected Property(Parcel in) {
        id = in.readString();
        type = in.readString();
        borough = in.readString();
        if (in.readByte() == 0) {
            price = null;
        } else {
            price = in.readDouble();
        }
        if (in.readByte() == 0) {
            surface = null;
        } else {
            surface = in.readDouble();
        }
        rooms = in.readInt();
        bathrooms = in.readInt();
        bedrooms = in.readInt();
        description = in.readString();
        streetNumber = in.readInt();
        streetName = in.readString();
        addressSupplement = in.readString();
        city = in.readString();
        zip = in.readInt();
        country = in.readString();
        sold = in.readByte() != 0;
        entryDate = in.readLong();
        saleDate = in.readLong();
        updateDate = in.readLong();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
        agentID = in.readString();
    }

    public static final Creator<Property> CREATOR = new Creator<Property>() {
        @Override
        public Property createFromParcel(Parcel in) {
            return new Property(in);
        }

        @Override
        public Property[] newArray(int size) {
            return new Property[size];
        }
    };

    /**
     * New york city borough
     *
     * @return New york city borough
     */
    @Ignore
    public static List<String> getBoroughs() {
        return Arrays.asList("The Bronx", "Brooklyn", "Manhattan",
                "Queens", "Staten Island", "Outside NYC");
    }

    /**
     * Base to create the Property id
     *
     * @return String of all data notnull
     */
    @Ignore
    @Exclude
    private String getStringToHash() {
        return this.getType() +
                this.getRooms() +
                this.getPrice() +
                this.getStreetNumber() +
                this.getStreetName() +
                this.getZip() +
                this.getCity() +
                this.getEntryDate();
    }

    /**
     * Create the id
     *
     * @return Property id
     */
    @Ignore
    @Exclude
    public String getHash() {
        return Utils.convertStringMd5(getStringToHash());
    }

    /**
     * Check if data has been changed
     *
     * @param property Property to compare
     * @return True if property has been changed
     */
    public boolean notEquals(Property property) {
        return !((Objects.equals(this.getAgentID(), property.getAgentID())
                && Objects.equals(this.getSaleDate(), property.getSaleDate())
                && Objects.equals(this.getEntryDate(), property.getEntryDate())
                && Objects.equals(this.getBorough(), property.getBorough())
                && Objects.equals(this.getDescription(), property.getDescription()))
                && Objects.equals(this.getZip(), property.getZip())
                && Objects.equals(this.getCountry(), property.getCountry())
                && Objects.equals(this.getCity(), property.getCity())
                && Objects.equals(this.getAddressSupplement(), property.getAddressSupplement())
                && Objects.equals(this.getStreetName(), property.getStreetName())
                && Objects.equals(this.getStreetNumber(), property.getStreetNumber())
                && Objects.equals(this.getBathrooms(), property.getBathrooms())
                && Objects.equals(this.getBedrooms(), property.getBedrooms())
                && Objects.equals(this.getRooms(), property.getRooms())
                && Objects.equals(this.getSurface(), property.getSurface())
                && Objects.equals(this.getPrice(), property.getPrice())
                && Objects.equals(this.getType(), property.getType())
                && Objects.equals(this.getId(), property.getId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Property property = (Property) o;
        return id.equals(property.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // GETTER

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    public String getBorough() {
        return borough;
    }

    public Double getPrice() {
        return price;
    }

    public Double getSurface() {
        return surface;
    }

    public int getRooms() {
        return rooms;
    }

    public int getBathrooms() {
        return bathrooms;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public String getDescription() {
        return description;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getAddressSupplement() {
        return addressSupplement;
    }

    public String getCity() {
        return city;
    }

    public int getZip() {
        return zip;
    }

    public String getCountry() {
        return country;
    }

    public boolean isSold() {
        return sold;
    }

    public long getEntryDate() {
        return entryDate;
    }

    public long getSaleDate() {
        return saleDate;
    }

    public long getUpdateDate() {
        return updateDate;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public String getAgentID() {
        return agentID;
    }

    // SETTER

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setBorough(String borough) {
        this.borough = borough;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setSurface(Double surface) {
        this.surface = surface;
    }

    public void setRooms(int rooms) {
        this.rooms = rooms;
    }

    public void setBathrooms(int bathrooms) {
        this.bathrooms = bathrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStreetNumber(int streetNumber) {
        this.streetNumber = streetNumber;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public void setAddressSupplement(String addressSupplement) {
        this.addressSupplement = addressSupplement;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZip(int zip) {
        this.zip = zip;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }

    public void setEntryDate(long entryDate) {
        this.entryDate = entryDate;
    }

    public void setSaleDate(long saleDate) {
        this.saleDate = saleDate;
    }

    public void setUpdateDate(long updateDate) {
        this.updateDate = updateDate;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public void setAgentID(String agentID) {
        this.agentID = agentID;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(type);
        parcel.writeString(borough);
        parcel.writeDouble(price);
        parcel.writeDouble(surface);
        parcel.writeInt(rooms);
        parcel.writeInt(bathrooms);
        parcel.writeInt(bedrooms);
        parcel.writeString(description);
        parcel.writeInt(streetNumber);
        parcel.writeString(streetName);
        parcel.writeString(addressSupplement);
        parcel.writeInt(zip);
        parcel.writeString(city);
        parcel.writeString(country);
        parcel.writeValue(sold);
        parcel.writeLong(entryDate);
        parcel.writeLong(saleDate);
        parcel.writeLong(updateDate);
        parcel.writeDouble(latitude);
        parcel.writeDouble(longitude);
        parcel.writeString(agentID);
    }
}