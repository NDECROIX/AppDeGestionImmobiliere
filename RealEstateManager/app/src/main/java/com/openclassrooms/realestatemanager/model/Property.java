package com.openclassrooms.realestatemanager.model;

import android.content.ContentValues;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

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
     * Address of the property.
     */
    @ColumnInfo(name = "address")
    private String address;

    /**
     * Status of the property. True if the property has been sold.
     */
    @ColumnInfo(name = "status")
    private boolean status;

    /**
     * The date on which the property entered the market.
     */
    @ColumnInfo(name = "entry_date")
    private String entryDate;

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

    public Property(String type, Double price, Double surface, int rooms, String description, String address, String entryDate, String agent) {
        this.type = type;
        this.price = price;
        this.surface = surface;
        this.rooms = rooms;
        this.description = description;
        this.address = address;
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
        if (values.containsKey("type")) property.setType(values.getAsString("type"));
        if (values.containsKey("price")) property.setPrice(values.getAsDouble("price"));
        if (values.containsKey("surface")) property.setSurface(values.getAsDouble("surface"));
        if (values.containsKey("rooms")) property.setRooms(values.getAsInteger("rooms"));
        if (values.containsKey("description"))
            property.setDescription(values.getAsString("description"));
        if (values.containsKey("status")) property.setStatus(values.getAsBoolean("status"));
        if (values.containsKey("entryDate")) property.setEntryDate(values.getAsString("entryDate"));
        if (values.containsKey("saleDate")) property.setSaleDate(values.getAsString("saleDate"));
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

    public String getAddress() {
        return address;
    }

    public boolean isStatus() {
        return status;
    }

    public String getEntryDate() {
        return entryDate;
    }

    public String getSaleDate() {
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

    public void setAddress(String address) {
        this.address = address;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public void setEntryDate(String entryDate) {
        this.entryDate = entryDate;
    }

    public void setSaleDate(String saleDate) {
        this.saleDate = saleDate;
    }

    public void setAgent(String agent) {
        this.agent = agent;
    }
}
