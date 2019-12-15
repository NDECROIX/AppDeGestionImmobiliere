package com.openclassrooms.realestatemanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.Type;
import com.openclassrooms.realestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.TypeDataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

public class PropertyViewModel extends ViewModel {

    // REPOSITORY
    private final PhotoDataRepository photoDataRepository;
    private final PoiDataRepository poiDataRepository;
    private final TypeDataRepository typeDataRepository;
    private final PoiNextPropertyDataRepository poiNextPropertyDataRepository;
    private final PropertyDataRepository propertyDataRepository;
    private final AgentDataRepository agentDataRepository;
    private final Executor executor;

    // CURRENT PROPERTY
    private Property currentProperty;
    private List<Photo> currentPhoto;
    private List<PoiNextProperty> currentPoisNextProperty;

    public PropertyViewModel(PhotoDataRepository photoDataRepository,
                             PoiDataRepository poiDataRepository,
                             TypeDataRepository typeDataRepository,
                             PoiNextPropertyDataRepository poiNextPropertyDataRepository,
                             PropertyDataRepository propertyDataRepository, AgentDataRepository agentDataRepository, Executor executor) {
        this.photoDataRepository = photoDataRepository;
        this.poiDataRepository = poiDataRepository;
        this.typeDataRepository = typeDataRepository;
        this.poiNextPropertyDataRepository = poiNextPropertyDataRepository;
        this.propertyDataRepository = propertyDataRepository;
        this.agentDataRepository = agentDataRepository;
        this.executor = executor;
    }

    /**
     * Update the current property selected
     *
     * @param property Current property
     */
    public void setCurrentProperty(Property property, List<Photo> photos) {
        this.currentProperty = property;
        this.currentPhoto = new ArrayList<>(photos);
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
     * Get photos from the last property
     *
     * @return Photos from property
     */
    public List<Photo> getCurrentPhotosProperty() {
        return currentPhoto;
    }

    public List<PoiNextProperty> getCurrentPoisNextProperty() {
        return currentPoisNextProperty;
    }

    public void setCurrentPoisNextProperty(List<PoiNextProperty> currentPoisNextProperty) {
        this.currentPoisNextProperty = currentPoisNextProperty;
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
     * Get property from the application database
     *
     * @return Property with id as parameter
     */
    public LiveData<Property> getProperty(String propertyID) {
        return propertyDataRepository.getProperty(propertyID);
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
     * @param photo Photo to delete
     */
    public void deletePhoto(Photo photo){
        executor.execute(() -> photoDataRepository.deletePhoto(photo));
    }

    //---------------
    //--- FOR POI ---
    //---------------

    /**
     * Get all Points of interest from the AppDatabase
     *
     * @return All Points of interest
     */
    public LiveData<List<Poi>> getAllPoi() {
        return poiDataRepository.getPOIs();
    }

    /**
     * Insert a point of interest in the AppDatabase
     *
     * @param poi Point of interest to add
     */
    public void insertPoi(Poi poi) {
        executor.execute(() -> poiDataRepository.insertPoiNextProperty(poi));
    }

    //---------------
    //--- FOR TYPE ---
    //---------------

    /**
     * Get all Type from the AppDatabase
     *
     * @return All types
     */
    public LiveData<List<Type>> getTypes() {
        return typeDataRepository.getTypes();
    }

    /**
     * Insert a type in the AppDatabase
     *
     * @param type Type to add
     */
    public void insertType(Type type) {
        executor.execute(() -> typeDataRepository.insertType(type));
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
    public LiveData<List<PoiNextProperty>> getPoiNextProperty(String propertyID) {
        return poiNextPropertyDataRepository.getProperties(propertyID);
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
     * @return List of agent
     */
    public LiveData<List<Agent>> getAgents(){
        return agentDataRepository.getAgents();
    }

    /**
     * Get an agent from the database
     * @param agentID agent id
     * @return agent with id passed in parameter
     */
    public LiveData<Agent> getAgent(String agentID){
        return agentDataRepository.getAgent(agentID);
    }

    /**
     * Insert an agent in the database
     * @param agent agent to insert
     */
    public void insertAgent(Agent agent){
        executor.execute(() -> agentDataRepository.insertAgent(agent));
    }

    /**
     * Update an agent in the database
     * @param agent agent to update
     */
    public void updateAgent(Agent agent){
        executor.execute(() -> agentDataRepository.updateAgent(agent));
    }

}
