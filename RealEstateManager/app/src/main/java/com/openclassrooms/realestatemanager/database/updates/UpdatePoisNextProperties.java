package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.PoisNexPropertiesHelper;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.viewmodel.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronize local database pois with the firebase database pois
 */
class UpdatePoisNextProperties {

    public interface UpdatePoiListener {
        void poisNextPropertiesSynchronized();

        void error(Exception exception);
    }

    // List of PoiNextProperty from the local database
    private List<PoiNextProperty> poisNextPropertiesRoom;
    // Activity view model
    private final PropertyViewModel propertyViewModel;
    // Notify errors and synchronization complete
    private UpdatePoiListener callback;
    // Activity lifecycle
    private LifecycleOwner lifecycleOwner;
    // Properties updated in room from firebase database
    private List<String> propertiesDown;

    private List<PoiNextProperty> poisNextPropertiesFirebase;

    /**
     * Constructor
     *
     * @param lifecycleOwner    Activity lifecycle
     * @param propertyViewModel Activity view model
     * @param callback          UpdatePoiListener
     * @param propertiesDown    Properties updated in room from firebase database
     */
    UpdatePoisNextProperties(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel,
                             UpdatePoiListener callback, List<String> propertiesDown) {
        this.callback = callback;
        this.propertiesDown = new ArrayList<>(propertiesDown);
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    /**
     * Get pois from the local database
     */
    void updateData() {
        this.propertyViewModel.getPoisNextProperties().observe(lifecycleOwner, new Observer<List<PoiNextProperty>>() {
            @Override
            public void onChanged(List<PoiNextProperty> pois) {
                if (poisNextPropertiesRoom == null) {
                    poisNextPropertiesRoom = new ArrayList<>(pois);
                    getPoisNextPropertiesFromFirebase();
                }
                propertyViewModel.getPoisNextProperties().removeObserver(this);
            }
        });
    }

    /**
     * Get pois from firebase database
     */
    private void getPoisNextPropertiesFromFirebase() {
        PoisNexPropertiesHelper.getPoisNextProperties().addOnCompleteListener(task -> {
            poisNextPropertiesFirebase = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                poisNextPropertiesFirebase.addAll(task.getResult().toObjects(PoiNextProperty.class));
            }
            syncData();
        });
    }

    /**
     * Synchronize data between the local database and Firebase database
     */
    private void syncData() {
        for (PoiNextProperty poiNextPropertyFirebase : poisNextPropertiesFirebase) {
            final int index = poisNextPropertiesRoom.indexOf(poiNextPropertyFirebase);
            if (index < 0) {
                if (propertiesDown.contains(poiNextPropertyFirebase.getPropertyID())) {
                    addPoiNextPropertyInRoom(poiNextPropertyFirebase);
                } else {
                    deletePoiNextPropertyFromFirebase(poiNextPropertyFirebase);
                }
            } else {
                poisNextPropertiesRoom.remove(index);
            }
        }
        for (PoiNextProperty poiNextPropertyRoom : poisNextPropertiesRoom) {
            if (propertiesDown.contains(poiNextPropertyRoom.getPropertyID())) {
                deletePoiFromRooms(poiNextPropertyRoom);
            } else {
                addPoiToFirebase(poiNextPropertyRoom);
            }
        }
        callback.poisNextPropertiesSynchronized();
    }

    /**
     * Delete PoiNextProperty from the local database
     *
     * @param poiNextProperty poiNextProperty to delete
     */
    private void deletePoiFromRooms(PoiNextProperty poiNextProperty) {
        propertyViewModel.deletePoiNextProperty(poiNextProperty);
    }

    /**
     * Add PoiNextProperty in the firebase database
     *
     * @param poiNextProperty poiNextProperty to add
     */
    private void addPoiToFirebase(PoiNextProperty poiNextProperty) {
        PoisNexPropertiesHelper.addPoisNextProperties(poiNextProperty).addOnFailureListener(callback::error);
    }

    /**
     * Delete poiNextProperty from the firebase database
     *
     * @param poiNextProperty PoiNextProperty to delete
     */
    private void deletePoiNextPropertyFromFirebase(PoiNextProperty poiNextProperty) {
        PoisNexPropertiesHelper.deletePoiNextProperty(poiNextProperty.getHash());
    }

    /**
     * Add poiNextProperty in the local database
     *
     * @param poiNextProperty PoiNextProperty to add
     */
    private void addPoiNextPropertyInRoom(PoiNextProperty poiNextProperty) {
        propertyViewModel.insertPoiNextProperty(poiNextProperty);
    }
}