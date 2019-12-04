package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.PoiNextPropertyDAO;
import com.openclassrooms.realestatemanager.model.POINextProperty;

import java.util.List;

public class PoiNextPropertyDataRepository {

    private final PoiNextPropertyDAO poiNextPropertyDAO;

    public PoiNextPropertyDataRepository(PoiNextPropertyDAO poiNextPropertyDAO) {
        this.poiNextPropertyDAO = poiNextPropertyDAO;
    }

    /**
     * Get all POI next a property from the application database
     *
     * @param propertyID Property id
     * @return List of all property
     */
    public LiveData<List<POINextProperty>> getProperties(long propertyID) {
        return this.poiNextPropertyDAO.getPoiNextProperty(propertyID);
    }

    /**
     * Insert a POI next to a property in the database
     *
     * @param poiNextProperty New property
     */
    public void insertPoiNextProperty(POINextProperty poiNextProperty) {
        this.poiNextPropertyDAO.insertPoiNextProperty(poiNextProperty);
    }

}
