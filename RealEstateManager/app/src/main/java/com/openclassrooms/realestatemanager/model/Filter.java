package com.openclassrooms.realestatemanager.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Filter implements Serializable {

    private String type;
    private Double minPrice;
    private Double maxPrice;
    private Double minSurface;
    private Double maxSurface;
    private int nbrPhotos;
    private String borough;
    private List<Poi> pois;

    public Filter() {
        pois = new ArrayList<>();
    }

    // UTILS

    public boolean meetsCriteria(Property property, int nbrPhotos, List<Poi> pois) {
        if (type != null && !type.isEmpty() && !property.getType().equals(type)){
            return false;
        }
        else if (minPrice != null && property.getPrice() < minPrice){
            return false;
        }
        else if (maxPrice != null && property.getPrice() > maxPrice){
            return false;
        }
        else if (minSurface != null && property.getSurface() < minSurface){
            return false;
        }
        else if (maxSurface != null && property.getSurface() > maxSurface){
            return false;
        }
        else if (this.nbrPhotos != 1 && nbrPhotos < this.nbrPhotos){
            return false;
        }
        else if (this.borough != null && !property.getBorough().equals(this.borough)){
            return false;
        }
        else if (!this.pois.isEmpty()){
            if (pois.isEmpty()) return false;
            for (Poi poi : this.pois){
                if (!pois.contains(poi)) return false;
            }
        }
        return true;
    }

    // GETTER

    public String getType() {
        return type;
    }

    public Double getMinPrice() {
        return minPrice;
    }

    public Double getMaxPrice() {
        return maxPrice;
    }

    public Double getMinSurface() {
        return minSurface;
    }

    public Double getMaxSurface() {
        return maxSurface;
    }

    public int getNbrPhotos() {
        return nbrPhotos;
    }

    public String getBorough() {
        return borough;
    }

    public List<Poi> getPois() {
        return pois;
    }

    // SETTER

    public void setType(String type) {
        this.type = type;
    }

    public void setMinPrice(Double minPrice) {
        this.minPrice = minPrice;
    }

    public void setMaxPrice(Double maxPrice) {
        this.maxPrice = maxPrice;
    }

    public void setMinSurface(Double minSurface) {
        this.minSurface = minSurface;
    }

    public void setMaxSurface(Double maxSurface) {
        this.maxSurface = maxSurface;
    }

    public void setNbrPhotos(int nbrPhotos) {
        this.nbrPhotos = nbrPhotos;
    }

    public void setBorough(String borough) {
        this.borough = borough;
    }

    public void setPois(List<Poi> pois) {
        this.pois = pois;
    }
}
