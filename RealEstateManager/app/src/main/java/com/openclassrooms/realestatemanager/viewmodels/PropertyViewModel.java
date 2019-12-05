package com.openclassrooms.realestatemanager.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;

import java.util.List;
import java.util.concurrent.Executor;

public class PropertyViewModel extends ViewModel {

    // REPOSITORY
    private final PhotoDataRepository photoDataRepository;
    private final PoiDataRepository poiDataRepository;
    private final PoiNextPropertyDataRepository poiNextPropertyDataRepository;
    private final PropertyDataRepository propertyDataRepository;
    private final Executor executor;

    // CURRENT PROPERTY
    private Property currentProperty;

    public PropertyViewModel(PhotoDataRepository photoDataRepository,
                             PoiDataRepository poiDataRepository,
                             PoiNextPropertyDataRepository poiNextPropertyDataRepository,
                             PropertyDataRepository propertyDataRepository, Executor executor) {
        this.photoDataRepository = photoDataRepository;
        this.poiDataRepository = poiDataRepository;
        this.poiNextPropertyDataRepository = poiNextPropertyDataRepository;
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

    //--------------------
    //--- FOR PROPERTY ---
    //--------------------

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

    //-----------------
    //--- FOR PHOTO ---
    //-----------------

    /**
     * Obtain all photos from property
     * @param propertyId Property id
     * @return Photo from property
     */
    public LiveData<List<Photo>> getPropertyPhotos(long propertyId){
        return photoDataRepository.getPropertyPhotos(propertyId);
    }

    /**
     * Insert a property photo in the AppDatabase
     * @param photo Photo to insert
     */
    public void insertPropertyPhoto(Photo photo){
        executor.execute(() -> photoDataRepository.insertPropertyPhoto(photo));
    }

    //---------------
    //--- FOR POI ---
    //---------------

    /**
     * Get all Points of interest from the AppDatabase
     * @return All Points of interest
     */
    public LiveData<List<Poi>> getAllPoi(){
        return poiDataRepository.getPOIs();
    }

    /**
     * Insert an point of interest in the AppDatabase
     * @param poi Point of interest to add
     */
    public void insertPoi(Poi poi){
        executor.execute(()-> poiDataRepository.insertPoiNextProperty(poi));
    }

    //-----------------------------
    //--- FOR POI NEXT PROPERTY ---
    //-----------------------------

    /**
     * Get all points of interest next a property
     * @param propertyID Property for which we want the POIs
     * @return All POI next the property
     */
    public LiveData<List<PoiNextProperty>> getPoiNextProperty(long propertyID){
        return poiNextPropertyDataRepository.getProperties(propertyID);
    }

    /**
     * Insert a point of interest next property in the AppDatabase
     * @param poiNextProperty Point of interest next a property to insert
     */
    public void insertPoiNextProperty(PoiNextProperty poiNextProperty){
        executor.execute(() -> poiNextPropertyDataRepository.insertPoiNextProperty(poiNextProperty));
    }
}
