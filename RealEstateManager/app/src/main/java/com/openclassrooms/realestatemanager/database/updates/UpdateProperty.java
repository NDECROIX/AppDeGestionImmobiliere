package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.PropertyHelper;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

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
    // Call on the api one after other to respect the unique constraint
    private int count;

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
                    if (!propertiesRoom.isEmpty()) {
                        updateProperties();
                    } else {
                        getNewPropertiesFromFirebase();
                    }
                }
                propertyViewModel.getProperties().removeObserver(this);
            }
        });
    }

    /**
     * Compare local properties with distance properties.
     * If update date is more recent on firebase, download data otherwise upload data.
     */
    private void updateProperties() {
        if (this.count >= propertiesRoom.size()) return;
        final int count = this.count;
        PropertyHelper.getProperty(propertiesRoom.get(count).getId()).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Property property = task.getResult().toObject(Property.class);
                if (property != null && property.getUpdateDate() != propertiesRoom.get(count).getUpdateDate()) {
                    if (property.getUpdateDate() > propertiesRoom.get(count).getUpdateDate()) {
                        updatePropertyInRooms(property);
                    } else if (property.getUpdateDate() < propertiesRoom.get(count).getUpdateDate()) {
                        updatePropertyInFirebase(propertiesRoom.get(count));
                    }
                } else if (property == null) {
                    updatePropertyInFirebase(propertiesRoom.get(count));
                }
            } else if (task.isSuccessful() && task.getResult() == null) {
                updatePropertyInFirebase(propertiesRoom.get(count));
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            this.count++;
            if (this.count < propertiesRoom.size()) {
                updateProperties();
            } else {
                getNewPropertiesFromFirebase();
            }
        });
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
     * Get properties from the firebase database and download any properties that do not exist
     * in the local database
     */
    private void getNewPropertiesFromFirebase() {
        PropertyHelper.getProperties().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<Property> properties = new ArrayList<>(task.getResult().toObjects(Property.class));
                for (Property property : properties) {
                    if (!propertiesRoom.contains(property)) {
                        addPropertyInRoom(property);
                    }
                }
            } else if (task.getException() != null) {
                callback.error(task.getException());
            }
            callback.propertiesSynchronized(propertiesDown);
        });
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