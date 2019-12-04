package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Date;
import java.util.List;

@Entity
public class Property {

    /**
     * The unique identifier of property
     */
    @PrimaryKey(autoGenerate = true)
    private long id;

    /**
     * Type of property.
     */
    @ColumnInfo(name = "type")
    private String type;

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
     * Description of the property.
     */
    @ColumnInfo(name = "description")
    private String description;

    /**
     * Uri photos of the property.
     */
    @ColumnInfo(name = "photos_uri")
    private List<String> photosUri;

    /**
     * Address of the property.
     */
    @ColumnInfo(name = "address")
    private String address;

    /**
     * Points of interest.
     */
    @ColumnInfo(name = "poi")
    private List<String> POI;

    /**
     * Status of the property. True if the property has been sold.
     */
    @ColumnInfo(name = "status")
    private boolean status;

    /**
     * The date on which the property entered the market.
     */
    @ColumnInfo(name = "entry_date")
    private Date entryDate;

    /**
     * Date on which the property was sold.
     */
    @ColumnInfo(name = "sale_date")
    private Date saleDate;

    /**
     * The real estate agent in charge of this property.
     */
    @ColumnInfo(name = "agent")
    private String agent;

    public Property(String type, Double price, Double surface, int rooms, String description, List<String> photosUri, String address, List<String> POI, Date entryDate, String agent) {
        this.type = type;
        this.price = price;
        this.surface = surface;
        this.rooms = rooms;
        this.description = description;
        this.photosUri = photosUri;
        this.address = address;
        this.POI = POI;
        this.status = false;
        this.entryDate = entryDate;
        this.saleDate = null;
        this.agent = agent;
    }

    public Property() {

    }

    // --- UTILS ---
    public static Property fromContentValues(ContentValues values) {
        final Property property = new Property();
        if (values.containsKey("type")) property.setType(values.getAsString("type"));
        if (values.containsKey("price")) property.setPrice(values.getAsDouble("price"));
        if (values.containsKey("surface")) property.setSurface(values.getAsDouble("surface"));
        if (values.containsKey("rooms")) property.setRooms(values.getAsInteger("rooms"));
        if (values.containsKey("description")) property.setDescription(values.getAsString("description"));
        if (values.containsKey("photosUri")) property.setPhotosUri((List<String>) values.get("photosUri"));
        if (values.containsKey("POI")) property.setPOI((List<String>) values.get("POI"));
        if (values.containsKey("status")) property.setStatus(values.getAsBoolean("status"));
        if (values.containsKey("entryDate")) property.setEntryDate((Date) values.get("entryDate"));
        if (values.containsKey("saleDate")) property.setSaleDate((Date) values.get("saleDate"));
        if (values.containsKey("agent")) property.setAgent(values.getAsString("agent"));
        return property;
    }

    // GETTER

    public long getId() {
        return id;
    }

    public String getType() {
        return type;
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

    public String getDescription() {
        return description;
    }

    public List<String> getPhotosUri() {
        return photosUri;
    }

    public String getAddress() {
        return address;
    }

    public List<String> getPOI() {
        return POI;
    }

    public boolean isStatus() {
        return status;
    }

    public Date getEntryDate() {
        return entryDate;
    }

    public Date getSaleDate() {
        return saleDate;
    }

    public String getAgent() {
        return agent;
    }

    // SETTER

    public void setId(long id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotosUri(List<String> photosUri) {
        this.photosUri = photosUri;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPOI(List<String> POI) {
        this.POI = POI;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setEntryDate(Date entryDate) {
        this.entryDate = entryDate;
    }

    public void setSaleDate(Date saleDate) {
        this.saleDate = saleDate;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
