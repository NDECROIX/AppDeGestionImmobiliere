package com.openclassrooms.realestatemanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class PropertyViewModel extends ViewModel {

    // REPOSITORY
    private final PropertyDataRepository propertyDataRepository;
    private final Executor executor;

    // CURRENT PROPERTY
    private Property currentProperty;

    public PropertyViewModel(PropertyDataRepository propertyDataRepository, Executor executor) {
        this.propertyDataRepository = propertyDataRepository;
        this.executor = executor;
    }

    /**
     * Update the current property selected
     *
     * @param property Current property
     */
    public void setCurrentProperty(Property property) {
        this.currentProperty = property;
    }

    /**
     * Get the last selected property
     *
     * @return Last selected property
     */
    public Property getCurrentProperty() {
        return currentProperty;
    }

    /**
     * Get all property from the application database
     *
     * @return List of all property
     */
    public LiveData<List<Property>> getProperties() {
        return propertyDataRepository.getProperties();
    }

    /**
     * Insert a property in the database
     *
     * @param property New property
     */
    public void insertProperty(Property property) {
        executor.execute(() -> propertyDataRepository.insertProperty(property));
    }

    /**
     * Update a property from the application database
     *
     * @param property Property to update
     */
    public void updateProperty(Property property) {
        executor.execute(() -> propertyDataRepository.updateProperty(property));
    }
}
