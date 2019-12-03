package com.openclassrooms.realestatemanager;

import java.util.Date;
import java.util.List;

public class Property {

    /**
     * The unique identifier of property
     */
    private long id;

    /**
     * Type of property.
     */
    private String type;

    /**
     * Real estate price.
     */
    private Double price;

    /**
     * Surface in square meters m2.
     */
    private Double surface;

    /**
     * The number of rooms.
     */
    private int rooms;

    /**
     * Description of the property.
     */
    private String description;

    /**
     * Uri photos of the property.
     */
    private List<String> photosUri;

    /**
     * Address of the property.
     */
    private String address;

    /**
     * Points of interest.
     */
    private List<String> POI;

    /**
     * Status of the property. True if the property has been sold.
     */
    private boolean status;

    /**
     * The date on which the property entered the market.
     */
    private Date entryDate;

    /**
     * Date on which the property was sold.
     */
    private Date saleDate;

    /**
     * The real estate agent in charge of this property.
     */
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
