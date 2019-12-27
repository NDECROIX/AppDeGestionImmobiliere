package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.openclassrooms.realestatemanager.api.PropertyHelper;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

public class UpdateProperty {

    public interface UpdatePropertyListener {
        void notification(String notification);

        void propertiesSynchronized(String typeData);

        void error(Exception exception);
    }

    private List<Property> propertiesRoom;
    private PropertyViewModel propertyViewModel;
    private UpdatePropertyListener callback;
    private LifecycleOwner lifecycleOwner;
    private int count = 0;

    public UpdateProperty(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdatePropertyListener callback) {
        this.callback = callback;
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    private Observer<List<Property>> observerProperty;

    public void updateData() {
        this.propertyViewModel.getProperties().observe(lifecycleOwner, new Observer<List<Property>>() {
            @Override
            public void onChanged(List<Property> properties) {
                propertiesRoom = new ArrayList<>(properties);
                propertyViewModel.getProperties().removeObserver(this);
                if (!propertiesRoom.isEmpty()){
                    updateProperties();
                } else {
                    getNewPropertiesFromFirebase();
                }
            }
        });
    }

    private void updateProperties() {
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
            count++;
            if (count < propertiesRoom.size()) {
                updateProperties();
            } else {
                getNewPropertiesFromFirebase();
            }
        });
    }

    private void updatePropertyInFirebase(Property property) {
        PropertyHelper.updateProperty(property).addOnFailureListener(callback::error);
        callback.notification(String.format("%s uploaded", property.getType()));
    }

    private void updatePropertyInRooms(Property property) {
        propertyViewModel.updateProperty(property);
        callback.notification(String.format("%s updated", property.getType()));
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
            callback.propertiesSynchronized("Properties");
        });
    }

    private void addPropertyInRoom(Property property) {
        propertyViewModel.insertProperty(property);
        callback.notification(String.format("%s added", property.getType()));
    }
}