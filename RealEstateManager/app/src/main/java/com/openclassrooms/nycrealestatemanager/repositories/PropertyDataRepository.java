package com.openclassrooms.nycrealestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.nycrealestatemanager.database.dao.PropertyDAO;
import com.openclassrooms.nycrealestatemanager.model.Property;

import java.util.List;

/**
 * Repository of properties
 */
public class PropertyDataRepository {

    private final PropertyDAO propertyDao;

    public PropertyDataRepository(PropertyDAO propertyDao){
        this.propertyDao = propertyDao;
    }

    /**
     * Get all property from the application database
     * @return List of all property
     */
    public LiveData<List<Property>> getProperties() {
        return this.propertyDao.getProperties();
    }

    /**
     * Get a property from the database
     * @param propertyID propertyID
     * @return Property
     */
    public LiveData<Property> getProperty(String propertyID){
        return this.propertyDao.getProperty(propertyID);
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