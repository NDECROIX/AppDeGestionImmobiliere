package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.PropertyDao;
import com.openclassrooms.realestatemanager.model.Property;

import java.util.List;

public class PropertyDataRepository {

    private final PropertyDao propertyDao;

    public PropertyDataRepository(PropertyDao propertyDao){
        this.propertyDao = propertyDao;
    }

    /**
     * Get all property from the application database
     * @return List of all property
     */
    public LiveData<List<Property>> getProperties() {
        return this.propertyDao.getAll();
    }

    /**
     * Insert a property in the database
     * @param property New property
     */
    public void insertProperty(Property property){
        this.propertyDao.insertProperty(property);
    }

    /**
     * Update a property from the application database
     * @param property Property to update
     */
    public void updateProperty(Property property){
        this.propertyDao.updateProperty(property);
    }



}
