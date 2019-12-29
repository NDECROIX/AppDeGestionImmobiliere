package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.PropertyHelper;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

class UpdateProperty {

    public interface UpdatePropertyListener {
        void propertiesSynchronized(List<String> propertiesDown);

        void error(Exception exception);
    }

    private List<Property> propertiesRoom;
    private final PropertyViewModel propertyViewModel;
    private UpdatePropertyListener callback;
    private LifecycleOwner lifecycleOwner;
    private List<String> propertiesDown;
    private int count;

    UpdateProperty(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdatePropertyListener callback) {
        this.callback = callback;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    void updateData() {
        this.propertyViewModel.getProperties().observe(lifecycleOwner, new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                if (propertiesRoom == null){
                    propertiesDown = new ArrayList<>();
                    propertiesRoom = new ArrayList<>(properties);
                    if (!propertiesRoom.isEmpty()){
                        updateProperties();
                    } else {
                        getNewPropertiesFromFirebase();
                    }
                }
                propertyViewModel.getProperties().removeObserver(this);
            }
        });
    }

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

    private void updatePropertyInFirebase(Property property) {
        PropertyHelper.updateProperty(property).addOnFailureListener(callback::error);
    }

    private void updatePropertyInRooms(Property property) {
        propertiesDown.add(property.getId());
        propertyViewModel.updateProperty(property);
    }

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

    private void addPropertyInRoom(Property property) {
        propertiesDown.add(property.getId());
        propertyViewModel.insertProperty(property);
    }
}