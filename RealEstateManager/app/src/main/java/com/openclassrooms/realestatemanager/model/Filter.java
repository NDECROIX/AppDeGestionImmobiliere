package com.openclassrooms.realestatemanager.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Filter for filtering properties
 */
public class Filter implements Parcelable {

    // 0 nothing, 1 On sale, 2 Sold.
    private int status;
    // Limit one of the entry date
    private long entryDateFrom;
    // Limit tow of the entry date
    private long entryDateTo;
    // Limit one of the sale date
    private long saleDateFrom;
    // Limit tow of the sale date
    private long saleDateTo;
    // Type of property
    private String type;
    // Limit one of the price
    private Double minPrice;
    // Limit tow of the price
    private Double maxPrice;
    // Limit one of the surface
    private Double minSurface;
    // Limit tow of the surface
    private Double maxSurface;
    // Number of photo
    private int nbrPhotos;
    // Property borough
    private String borough;
    // Points of interest
    private List<Poi> pois;

    public Filter() {
        pois = new ArrayList<>();
    }

    protected Filter(Parcel in) {
        status = in.readInt();
        entryDateFrom = in.readLong();
        entryDateTo = in.readLong();
        saleDateFrom = in.readLong();
        saleDateTo = in.readLong();
        type = in.readString();
        if (in.readByte() == 0) {
            minPrice = null;
        } else {
            minPrice = in.readDouble();
        }
        if (in.readByte() == 0) {
            maxPrice = null;
        } else {
            maxPrice = in.readDouble();
        }
        if (in.readByte() == 0) {
            minSurface = null;
        } else {
            minSurface = in.readDouble();
        }
        if (in.readByte() == 0) {
            maxSurface = null;
        } else {
            maxSurface = in.readDouble();
        }
        nbrPhotos = in.readInt();
        borough = in.readString();
    }

    public static final Creator<Filter> CREATOR = new Creator<Filter>() {
        @Override
        public Filter createFromParcel(Parcel in) {
            return new Filter(in);
        }

        @Override
        public Filter[] newArray(int size) {
            return new Filter[size];
        }
    };

    /**
     * Check if the criteria correlate with the property
     *
     * @param property  Property to check
     * @param nbrPhotos Number of photos it should have
     * @param pois      Points of interest next the property
     * @return True if property respect criteria
     */
    public boolean meetsCriteria(Property property, int nbrPhotos, List<Poi> pois) {
        if (status != 0) {
            if (status == 1 && property.getSaleDate() != 0) {
                return false;
            } else if (status == 2 && property.getSaleDate() == 0) {
                return false;
            }
        }
        if (entryDateFrom != 0 && property.getEntryDate() < entryDateFrom) {
            return false;
        }
        if (entryDateFrom != 0 && property.getEntryDate() > entryDateTo) {
            return false;
        }
        if (saleDateFrom != 0 && property.getSaleDate() < saleDateFrom) {
            return false;
        }
        if (saleDateTo != 0 && property.getSaleDate() > saleDateTo) {
            return false;
        }
        if (type != null && !type.isEmpty() && !property.getType().equals(type)) {
            return false;
        } else if (minPrice != null && property.getPrice() < minPrice) {
            return false;
        } else if (maxPrice != null && property.getPrice() > maxPrice) {
            return false;
        } else if (minSurface != null && property.getSurface() < minSurface) {
            return false;
        } else if (maxSurface != null && property.getSurface() > maxSurface) {
            return false;
        } else if (this.nbrPhotos != 1 && nbrPhotos < this.nbrPhotos) {
            return false;
        } else if (this.borough != null && !property.getBorough().equals(this.borough)) {
            return false;
        } else if (!this.pois.isEmpty()) {
            if (pois.isEmpty()) return false;
            for (Poi poi : this.pois) {
                if (!pois.contains(poi)) return false;
            }
        }
        return true;
    }

    // GETTER

    public String getType() {
        return type;
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

    public void setStatus(int status) {
        this.status = status;
    }

    public void setEntryDateFrom(long entryDateFrom) {
        this.entryDateFrom = entryDateFrom;
    }

    public void setEntryDateTo(long entryDateTo) {
        this.entryDateTo = entryDateTo;
    }

    public void setSaleDateFrom(long saleDateFrom) {
        this.saleDateFrom = saleDateFrom;
    }

    public void setSaleDateTo(long saleDateTo) {
        this.saleDateTo = saleDateTo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(status);            //int status;
        parcel.writeLong(entryDateFrom);            //long entryDateFrom;
        parcel.writeLong(entryDateTo);            //long entryDateTo;
        parcel.writeLong(saleDateFrom);            //long saleDateFrom;
        parcel.writeLong(saleDateTo);            //long saleDateTo;
        parcel.writeString(type);            //String type;
        parcel.writeDouble(minPrice);            //Double minPrice;
        parcel.writeDouble(maxPrice);            //Double maxPrice;
        parcel.writeDouble(minSurface);            //Double minSurface;
        parcel.writeDouble(maxSurface);            //Double maxSurface;
        parcel.writeInt(nbrPhotos);            //int nbrPhotos;
        parcel.writeString(borough);            //String borough;
        parcel.writeArray(new List[]{pois});            //List<Poi> pois;
    }
}
