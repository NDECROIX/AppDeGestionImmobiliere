package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.PropertyHelper;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.viewmodel.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronize local database properties with the firebase database properties
 */
class UpdateProperty {

    public interface UpdatePropertyListener {
        void propertiesSynchronized(List<String> propertiesDown);

        void error(Exception exception);
    }

    // Properties from the local database
    private List<Property> propertiesRoom;
    // Activity view model
    private final PropertyViewModel propertyViewModel;
    // Notify errors and synchronization complete
    private UpdatePropertyListener callback;
    // Activity lifecycle
    private LifecycleOwner lifecycleOwner;
    // Properties updated from the firebase database
    private List<String> propertiesDown;

    private List<Property> propertiesFirebase;

    /**
     * Constructor
     *
     * @param lifecycleOwner    Activity lifecycle
     * @param propertyViewModel Activity property view model
     * @param callback          UpdatePropertyListener
     */
    UpdateProperty(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdatePropertyListener callback) {
        this.callback = callback;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    /**
     * Get properties from the local database
     */
    void updateData() {
        this.propertyViewModel.getProperties().observe(lifecycleOwner, new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                if (propertiesRoom == null) {
                    propertiesDown = new ArrayList<>();
                    propertiesRoom = new ArrayList<>(properties);
                    getPropertiesFromFirebase();
                }
                propertyViewModel.getProperties().removeObserver(this);
            }
        });
    }

    /**
     * Get properties from firebase database
     */
    private void getPropertiesFromFirebase() {
        PropertyHelper.getProperties().addOnCompleteListener(task -> {
            propertiesFirebase = new ArrayList<>();
            if (task.isSuccessful() && task.getResult() != null) {
                propertiesFirebase.addAll(task.getResult().toObjects(Property.class));
            }
            syncData();
        });
    }

    /**
     * Sync data between local database and firebase database
     */
    private void syncData() {
        for (Property propertyFirebase : propertiesFirebase) {
            final int index = propertiesRoom.indexOf(propertyFirebase);
            if (index >= 0) {
                if (propertyFirebase.getUpdateDate() > propertiesRoom.get(index).getUpdateDate()) {
                    updatePropertyInRooms(propertyFirebase);
                } else if (propertyFirebase.getUpdateDate() < propertiesRoom.get(index).getUpdateDate()) {
                    updatePropertyInFirebase(propertiesRoom.get(index));
                }
                propertiesRoom.remove(index);
            } else {
                addPropertyInRoom(propertyFirebase);
            }
        }
        for (Property propertyRoom : propertiesRoom) {
            updatePropertyInFirebase(propertyRoom);
        }
        callback.propertiesSynchronized(propertiesDown);
    }

    /**
     * Update/Add property in the firebase database
     *
     * @param property Property to update or add
     */
    private void updatePropertyInFirebase(Property property) {
        PropertyHelper.updateProperty(property).addOnFailureListener(callback::error);
    }

    /**
     * Update property to add in the local database
     *
     * @param property Property to add
     */
    private void updatePropertyInRooms(Property property) {
        propertiesDown.add(property.getId());
        propertyViewModel.updateProperty(property);
    }

    /**
     * Add property in the local database
     *
     * @param property Property to add
     */
    private void addPropertyInRoom(Property property) {
        propertiesDown.add(property.getId());
        propertyViewModel.insertProperty(property);
    }
}