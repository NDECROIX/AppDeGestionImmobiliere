package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.openclassrooms.realestatemanager.api.AgentHelper;
import com.openclassrooms.realestatemanager.api.PoisNexPropertiesHelper;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

class UpdatePoisNextProperties {

    public interface UpdatePoiListener {
        void poisNextPropertiesSynchronized();

        void error(Exception exception);
    }

    private List<PoiNextProperty> poisNextPropertiesRoom;
    private final PropertyViewModel propertyViewModel;
    private UpdatePoiListener callback;
    private LifecycleOwner lifecycleOwner;
    private List<String> propertiesDown;
    private int count = 0;

    UpdatePoisNextProperties(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel,
                             UpdatePoiListener callback, List<String> propertiesDown) {
        this.callback = callback;
        this.propertiesDown = new ArrayList<>(propertiesDown);
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
    }

    void updateData() {
        this.propertyViewModel.getPoisNextProperties().observe(lifecycleOwner, new Observer<List<PoiNextProperty>>() {
            @Override
            public void onChanged(List<PoiNextProperty> pois) {
                if (poisNextPropertiesRoom == null){
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

    private void deletePoiFromRooms(PoiNextProperty poiNextProperty) {
        propertyViewModel.deletePoiNextProperty(poiNextProperty);
    }

    private void addPoiToFirebase(PoiNextProperty poiNextProperty) {
        PoisNexPropertiesHelper.addPoisNextProperties(poiNextProperty).addOnFailureListener(callback::error);
    }

    /**
     * After comparing the values between Room and Firebase, we get all the values from Firebase
     * we don't have.
     */
    private void getNewPoisFromFirebase() {
        PoisNexPropertiesHelper.getPoisNextProperties().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                List<PoiNextProperty> poisNextProperties = new ArrayList<>(task.getResult().toObjects(PoiNextProperty.class));
                for (PoiNextProperty poiNextProperty : poisNextProperties) {
                    if (!poisNextPropertiesRoom.contains(poiNextProperty)) {
                        if (propertiesDown.contains(poiNextProperty.getPropertyID())){
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

    private void deletePoiNextPropertyFromFirebase(PoiNextProperty poiNextProperty) {
        PoisNexPropertiesHelper.deletePoiNextProperty(poiNextProperty.getHash());
    }

    private void addPoiNextPropertyInRoom(PoiNextProperty poiNextProperty) {
        propertyViewModel.insertPoiNextProperty(poiNextProperty);
    }
}