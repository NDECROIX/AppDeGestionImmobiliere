package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Entity
public class Property {

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
    @ColumnInfo(name = "status")
    private boolean status;

    /**
     * The date on which the property entered the market.
     */
    @ColumnInfo(name = "entry_date")
    private long entryDate;

    /**
     * Date on which the property was sold.
     */
    @ColumnInfo(name = "sale_date")
    private String saleDate;

    /**
     * The real estate agent in charge of this property.
     */
    @ColumnInfo(name = "agent")
    private String agent;

    public Property(@NonNull String id, String type, String borough, Double price, Double surface, int rooms, int bathrooms,
                    int bedrooms, String description, int streetNumber, String streetName,
                    String addressSupplement, String city, int zip, String country, long entryDate, String agent) {
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
        this.status = false;
        this.entryDate = entryDate;
        this.saleDate = null;
        this.agent = agent;
    }

    @Ignore
    public Property() {
    }

    // --- UTILS ---
    public static Property fromContentValues(ContentValues values) {
        final Property property = new Property();
        if (values.containsKey("id")) property.setId(values.getAsString("id"));
        if (values.containsKey("type")) property.setType(values.getAsString("type"));
        if (values.containsKey("borough")) property.setBorough(values.getAsString("borough"));
        if (values.containsKey("price")) property.setPrice(values.getAsDouble("price"));
        if (values.containsKey("surface")) property.setSurface(values.getAsDouble("surface"));
        if (values.containsKey("rooms")) property.setRooms(values.getAsInteger("rooms"));
        if (values.containsKey("bathrooms"))
            property.setBathrooms(values.getAsInteger("bathrooms"));
        if (values.containsKey("bedrooms")) property.setBedrooms(values.getAsInteger("bedrooms"));
        if (values.containsKey("description"))
            property.setDescription(values.getAsString("description"));
        if (values.containsKey("streetNumber"))
            property.setStreetNumber(values.getAsInteger("streetNumber"));
        if (values.containsKey("streetName"))
            property.setStreetName(values.getAsString("streetName"));
        if (values.containsKey("addressSupplement"))
            property.setAddressSupplement(values.getAsString("addressSupplement"));
        if (values.containsKey("city")) property.setCity(values.getAsString("city"));
        if (values.containsKey("zip")) property.setZip(values.getAsInteger("zip"));
        if (values.containsKey("country")) property.setCountry(values.getAsString("country"));
        if (values.containsKey("status")) property.setStatus(values.getAsBoolean("status"));
        if (values.containsKey("entryDate")) property.setEntryDate(values.getAsLong("entryDate"));
        if (values.containsKey("saleDate")) property.setSaleDate(values.getAsString("saleDate"));
        if (values.containsKey("agent")) property.setAgent(values.getAsString("agent"));
        return property;
    }

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
     * @return String of all data notnull
     */
    @Ignore
    public String getStringToHash(){
        return this.getType() +
                this.getRooms() +
                this.getPrice() +
                this.getStreetNumber() +
                this.getStreetName() +
                this.getZip() +
                this.getCity() +
                this.getEntryDate() +
                this.getAgent();
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

    public boolean isStatus() {
        return status;
    }

    public long getEntryDate() {
        return entryDate;
    }

    public String getSaleDate() {
        return saleDate;
    }

    public String getAgent() {
        return agent;
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

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setEntryDate(long entryDate) {
        this.entryDate = entryDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
