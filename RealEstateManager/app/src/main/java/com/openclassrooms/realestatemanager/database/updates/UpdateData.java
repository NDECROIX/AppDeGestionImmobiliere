package com.openclassrooms.realestatemanager.database.updates;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

public class UpdateData implements UpdateAgent.UpdateAgentListener, UpdateProperty.UpdatePropertyListener,
        UpdatePhoto.UpdatePhotoListener, UpdatePoisNextProperties.UpdatePoiListener {

    public interface UpdateDataListener {
        void synchronisationComplete();

        void notification(String notification);
    }

    private UpdateDataListener callback;
    private LifecycleOwner lifecycleOwner;
    private PropertyViewModel propertyViewModel;
    private List<String> propertiesDown;
    private Context context;

    public UpdateData(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdateDataListener callback, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.propertyViewModel = propertyViewModel;
        this.callback = callback;
    }

    public void startSynchronisation() {
        callback.notification("Synchronization");
        synchronizeAgents();
    }

    private void synchronizeAgents() {
        UpdateAgent updateAgent = new UpdateAgent(lifecycleOwner, propertyViewModel, this);
        updateAgent.updateData();
    }

    private void synchronizeProperties() {
        UpdateProperty updateProperty = new UpdateProperty(lifecycleOwner, propertyViewModel, this);
        updateProperty.updateData();
    }

    private void synchronizePhotos() {
        UpdatePhoto updatePhoto = new UpdatePhoto(lifecycleOwner, propertyViewModel, this, propertiesDown, context);
        updatePhoto.updateData();
    }

    private void synchronizePoisNextProperties() {
        UpdatePoisNextProperties updatePoisNextProperties = new UpdatePoisNextProperties(lifecycleOwner, propertyViewModel, this, propertiesDown);
        updatePoisNextProperties.updateData();
    }

    @Override
    public void error(Exception exception) {
        callback.notification(exception.getMessage());
    }

    @Override
    public void agentsSynchronized() {
        callback.notification("Agents synchronized");
        synchronizeProperties();
    }

    @Override
    public void propertiesSynchronized(List<String> propertiesDown) {
        this.propertiesDown = new ArrayList<>(propertiesDown);
        synchronizePhotos();
    }

    @Override
    public void photosSynchronized() {
        synchronizePoisNextProperties();
    }

    @Override
    public void poisNextPropertiesSynchronized() {
        callback.notification("Properties synchronized");
        callback.synchronisationComplete();
    }
}