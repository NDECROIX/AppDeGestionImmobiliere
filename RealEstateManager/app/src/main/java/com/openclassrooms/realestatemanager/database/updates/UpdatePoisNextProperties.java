package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.PoisNexPropertiesHelper;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

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
    // Call on the api one after one to respect the unique constraint
    private int count = 0;

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
                    if (!poisNextPropertiesRoom.isEmpty()) {
                        updatePoisNextProperties();
                    } else {
                        getNewPoisFromFirebase();
                    }
                }
                propertyViewModel.getPoisNextProperties().removeObserver(this);
            }
        });
    }

    /**
     * Compare the Room value with the Firebase Value if Firebase does not contain the value and the property
     * of the value has been updated we remove the value from Room database otherwise  we add it to Firebase.
     */
    private void updatePoisNextProperties() {
        if (count >= poisNextPropertiesRoom.size()) return;
        final int count = this.count;
        PoisNexPropertiesHelper.getPoiNextProperty(poisNextPropertiesRoom.get(count).getHash()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                PoiNextProperty poisFirebase = task.getResult().toObject(PoiNextProperty.class);
                if (poisFirebase == null) {
                    if (propertiesDown.contains(poisNextPropertiesRoom.get(count).getPropertyID())) {
                        deletePoiFromRooms(poisNextPropertiesRoom.get(count));
                    } else {
                        addPoiToFirebase(poisNextPropertiesRoom.get(count));
                    }
                }
            } else if (task.isSuccessful() && task.getResult() == null) {
                if (propertiesDown.contains(poisNextPropertiesRoom.get(count).getPropertyID())) {
                    deletePoiFromRooms(poisNextPropertiesRoom.get(count));
                } else {
                    addPoiToFirebase(poisNextPropertiesRoom.get(count));
                }
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            this.count++;
            if (this.count < poisNextPropertiesRoom.size()) {
                updatePoisNextProperties();
            } else {
                getNewPoisFromFirebase();
            }
        });
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
     * After comparing the values between Room and Firebase, we get all the values from Firebase
     * we do not have.
     */
    private void getNewPoisFromFirebase() {
        PoisNexPropertiesHelper.getPoisNextProperties().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<PoiNextProperty> poisNextProperties = new ArrayList<>(task.getResult().toObjects(PoiNextProperty.class));
                for (PoiNextProperty poiNextProperty : poisNextProperties) {
                    if (!poisNextPropertiesRoom.contains(poiNextProperty)) {
                        if (propertiesDown.contains(poiNextProperty.getPropertyID())) {
                            addPoiNextPropertyInRoom(poiNextProperty);
                        } else {
                            deletePoiNextPropertyFromFirebase(poiNextProperty);
                        }
                    } else {
                        poisNextPropertiesRoom.remove(poiNextProperty);
                    }
                }
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            callback.poisNextPropertiesSynchronized();
        });
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