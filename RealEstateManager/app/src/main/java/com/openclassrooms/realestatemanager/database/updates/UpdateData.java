package com.openclassrooms.realestatemanager.database.updates;

import androidx.lifecycle.LifecycleOwner;

import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

public class UpdateData implements UpdateAgent.UpdateAgentListener, UpdateProperty.UpdatePropertyListener {

    public interface UpdateDataListener {
        void synchronisationComplete();

        void notification(String notification);
    }

    private UpdateDataListener callback;
    private LifecycleOwner lifecycleOwner;
    private PropertyViewModel propertyViewModel;

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

    private void synchronizeProperty() {
        UpdateProperty updateProperty = new UpdateProperty(lifecycleOwner, propertyViewModel, this);
        updateProperty.updateData();
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
        synchronizeProperty();
    }

    @Override
    public void propertiesSynchronized(String typeData) {
        callback.synchronisationComplete();
    }
}