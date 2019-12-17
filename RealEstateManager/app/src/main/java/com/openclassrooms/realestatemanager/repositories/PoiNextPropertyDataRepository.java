package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.PoiNextPropertyDAO;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;

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
     * @return List of all poi next the property
     */
    public LiveData<List<PoiNextProperty>> getPoisNextProperty(String propertyID) {
        return this.poiNextPropertyDAO.getPoisNextProperty(propertyID);
    }

    /**
     * Get all POI next to the properties from the application database
     *
     * @return List of all poi next to the properties
     */
    public LiveData<List<PoiNextProperty>> getPoisNextProperties() {
        return this.poiNextPropertyDAO.getPoisNextProperties();
    }

    /**
     * Insert a POI next to a property in the database
     *
     * @param poiNextProperty New property poi
     */
    public void insertPoiNextProperty(PoiNextProperty poiNextProperty) {
        this.poiNextPropertyDAO.insertPoiNextProperty(poiNextProperty);
    }

    /**
     * Delete a POI next a property in the database
     *
     * @param poiNextProperty Poi to delete
     */
    public void deletePoiNextProperty(PoiNextProperty poiNextProperty) {
        this.poiNextPropertyDAO.deletePoiNextProperty(poiNextProperty);
    }

}
