package com.openclassrooms.nycrealestatemanager.viewmodel;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.nycrealestatemanager.model.Agent;
import com.openclassrooms.nycrealestatemanager.model.Filter;
import com.openclassrooms.nycrealestatemanager.model.Photo;
import com.openclassrooms.nycrealestatemanager.model.PoiNextProperty;
import com.openclassrooms.nycrealestatemanager.model.Property;
import com.openclassrooms.nycrealestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.nycrealestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.nycrealestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.nycrealestatemanager.repositories.PropertyDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

/**
 * View model that manages the local database
 */
public class PropertyViewModel extends ViewModel {

    // REPOSITORY
    private final PhotoDataRepository photoDataRepository;
    private final PoiNextPropertyDataRepository poiNextPropertyDataRepository;
    private final PropertyDataRepository propertyDataRepository;
    private final AgentDataRepository agentDataRepository;
    private final Executor executor;

    // CURRENT PROPERTY
    private MutableLiveData<Property> currentProperty;
    private MutableLiveData<List<Photo>> currentPhoto;
    private MutableLiveData<List<PoiNextProperty>> currentPoisNextProperty;
    private MutableLiveData<Filter> currentFilter;

    public PropertyViewModel(PhotoDataRepository photoDataRepository,
                             PoiNextPropertyDataRepository poiNextPropertyDataRepository,
                             PropertyDataRepository propertyDataRepository, AgentDataRepository agentDataRepository, Executor executor) {
        this.photoDataRepository = photoDataRepository;
        this.poiNextPropertyDataRepository = poiNextPropertyDataRepository;
        this.propertyDataRepository = propertyDataRepository;
        this.agentDataRepository = agentDataRepository;
        this.executor = executor;
        this.currentProperty = new MutableLiveData<>();
        this.currentPhoto = new MutableLiveData<>();
        this.currentPoisNextProperty = new MutableLiveData<>();
        this.currentFilter = new MutableLiveData<>();
    }

    /**
     * Update the current property selected
     *
     * @param property Current property
     */
    public void setCurrentProperty(Property property, LifecycleOwner lifecycleOwner) {
        propertyDataRepository.getProperty(property.getId())
                .observe(lifecycleOwner, propertyO -> currentProperty.setValue(propertyO));
        poiNextPropertyDataRepository.getPoisNextProperty(property.getId())
                .observe(lifecycleOwner, poiNextProperties -> currentPoisNextProperty.setValue(poiNextProperties));
        photoDataRepository.getPropertyPhotos(property.getId())
                .observe(lifecycleOwner, photos -> currentPhoto.setValue(photos));
    }

    /**
     * Change the current filter
     *
     * @param currentFilter Filter
     */
    public void setCurrentFilter(Filter currentFilter) {
        this.currentFilter.setValue(currentFilter);
    }

    /**
     * Get the current filter
     *
     * @return Filter
     */
    public MutableLiveData<Filter> getCurrentFilter() {
        return currentFilter;
    }

    /**
     * Get the last selected property
     *
     * @return Last selected property
     */
    public MutableLiveData<Property> getCurrentProperty() {
        return currentProperty;
    }

    /**
     * Get photos from the last property
     *
     * @return Photos from property
     */
    public MutableLiveData<List<Photo>> getCurrentPhotosProperty() {
        return currentPhoto;
    }

    public MutableLiveData<List<PoiNextProperty>> getCurrentPoisNextProperty() {
        return currentPoisNextProperty;
    }

    //--------------------
    //--- FOR PROPERTY ---
    //--------------------

    /**
     * Get all properties from the application database
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

    //-----------------
    //--- FOR PHOTO ---
    //-----------------

    /**
     * Obtain all photos from property
     *
     * @param propertyId Property id
     * @return Photo from property
     */
    public LiveData<List<Photo>> getPropertyPhotos(String propertyId) {
        return photoDataRepository.getPropertyPhotos(propertyId);
    }

    /**
     * Obtain all photos from AppDatabase
     *
     * @return Photos
     */
    public LiveData<List<Photo>> getPhotos() {
        return photoDataRepository.getPhotos();
    }

    /**
     * Insert a property photo in the AppDatabase
     *
     * @param photo Photo to insert
     */
    public void insertPropertyPhoto(Photo photo) {
        executor.execute(() -> photoDataRepository.insertPropertyPhoto(photo));
    }

    /**
     * Delete a photo from the database
     *
     * @param photo Photo to delete
     */
    public void deletePhoto(Photo photo) {
        executor.execute(() -> photoDataRepository.deletePhoto(photo));
    }

    //-----------------------------
    //--- FOR POI NEXT PROPERTY ---
    //-----------------------------

    /**
     * Get all points of interest next a property
     *
     * @param propertyID Property for which we want the POIs
     * @return All POI next the property
     */
    public LiveData<List<PoiNextProperty>> getPoisNextProperty(String propertyID) {
        return poiNextPropertyDataRepository.getPoisNextProperty(propertyID);
    }

    /**
     * Get all points of interest next to the properties
     *
     * @return All POI next to the properties
     */
    public LiveData<List<PoiNextProperty>> getPoisNextProperties() {
        return poiNextPropertyDataRepository.getPoisNextProperties();
    }

    /**
     * Insert a point of interest next property in the AppDatabase
     *
     * @param poiNextProperty Point of interest next a property to insert
     */
    public void insertPoiNextProperty(PoiNextProperty poiNextProperty) {
        executor.execute(() -> poiNextPropertyDataRepository.insertPoiNextProperty(poiNextProperty));
    }

    /**
     * Delete a point of interest next property in the AppDatabase
     *
     * @param poiNextProperty Point of interest next a property to delete
     */
    public void deletePoiNextProperty(PoiNextProperty poiNextProperty) {
        executor.execute(() -> poiNextPropertyDataRepository.deletePoiNextProperty(poiNextProperty));
    }

    //-----------------
    //--- FOR AGENT ---
    //-----------------

    /**
     * Get Agents from the AppDatabase
     *
     * @return List of agent
     */
    public LiveData<List<Agent>> getAgents() {
        return agentDataRepository.getAgents();
    }

    /**
     * Get an agent from the database
     *
     * @param agentID agent id
     * @return agent with id passed in parameter
     */
    public LiveData<Agent> getAgent(String agentID) {
        return agentDataRepository.getAgent(agentID);
    }

    /**
     * Insert an agent in the database
     *
     * @param agent agent to insert
     */
    public void insertAgent(Agent agent) {
        executor.execute(() -> agentDataRepository.insertAgent(agent));
    }

    /**
     * Update an agent in the database
     *
     * @param agent agent to update
     */
    public void updateAgent(Agent agent) {
        executor.execute(() -> agentDataRepository.updateAgent(agent));
    }
}