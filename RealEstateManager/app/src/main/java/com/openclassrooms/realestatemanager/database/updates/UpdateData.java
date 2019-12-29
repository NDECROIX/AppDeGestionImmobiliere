package com.openclassrooms.realestatemanager.database.updates;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

public class UpdateData implements UpdateAgent.UpdateAgentListener, UpdateProperty.UpdatePropertyListener,
    UpdatePhoto.UpdatePhotoListener{

    public interface UpdateDataListener {
        void synchronisationComplete();

        void notification(String notification);
    }

    private UpdateDataListener callback;
    private LifecycleOwner lifecycleOwner;
    private PropertyViewModel propertyViewModel;
    private List<String> propertiesDown;

    public UpdateData(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdateDataListener callback) {
        this.lifecycleOwner = lifecycleOwner;
        this.propertyViewModel = propertyViewModel;
        this.callback = callback;
    }

    public void startSynchronisation() {
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
        UpdatePhoto updatePhoto = new UpdatePhoto(lifecycleOwner, propertyViewModel, this, propertiesDown);
        updatePhoto.updateData();
    }

    @Override
    public void notification(String notification) {
        callback.notification(notification);
    }

    @Override
    public void error(Exception exception) {
        callback.notification(exception.getMessage());
    }

    @Override
    public void agentsSynchronized(String typeData) {
        synchronizeProperties();
    }

    @Override
    public void propertiesSynchronized(List<String> propertiesPush, List<String> propertiesDown) {
        this.propertiesDown = new ArrayList<>(propertiesDown);
        synchronizePhotos();
    }

    @Override
    public void photosSynchronized() {
        callback.synchronisationComplete();
    }
}