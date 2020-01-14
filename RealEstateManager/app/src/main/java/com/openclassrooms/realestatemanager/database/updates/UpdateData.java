package com.openclassrooms.realestatemanager.database.updates;

import android.content.Context;

import androidx.lifecycle.LifecycleOwner;

import com.openclassrooms.realestatemanager.viewmodel.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

/**
 * Synchronize local database with the firebase database
 */
public class UpdateData implements UpdateAgent.UpdateAgentListener, UpdateProperty.UpdatePropertyListener,
        UpdatePhoto.UpdatePhotoListener, UpdatePoisNextProperties.UpdatePoiListener {

    public interface UpdateDataListener {
        void synchronisationComplete();

        void notification(String notification);
    }

    // Notify the user for each error and when synchronization is complete
    private UpdateDataListener callback;
    // Activity lifecycle
    private LifecycleOwner lifecycleOwner;
    // Activity view model
    private PropertyViewModel propertyViewModel;
    // Properties updated in the local database from firebase database
    private List<String> propertiesDown;
    // Activity context
    private Context context;

    /**
     * Constructor
     * @param lifecycleOwner Activity lifecycle
     * @param propertyViewModel Activity view model
     * @param callback UpdateDataListener
     * @param context Activity context
     */
    public UpdateData(LifecycleOwner lifecycleOwner, PropertyViewModel propertyViewModel, UpdateDataListener callback, Context context) {
        this.lifecycleOwner = lifecycleOwner;
        this.context = context;
        this.propertyViewModel = propertyViewModel;
        this.callback = callback;
    }

    /**
     * Starting the synchronization by agents
     */
    public void startSynchronisation() {
        synchronizeAgents();
    }

    /**
     * Synchronize local database agents with firebase database agents
     */
    private void synchronizeAgents() {
        UpdateAgent updateAgent = new UpdateAgent(lifecycleOwner, propertyViewModel, this);
        updateAgent.updateData();
    }

    /**
     * Synchronize local database properties with firebase database properties
     */
    private void synchronizeProperties() {
        UpdateProperty updateProperty = new UpdateProperty(lifecycleOwner, propertyViewModel, this);
        updateProperty.updateData();
    }

    /**
     * Synchronize local database photos with firebase database photos
     */
    private void synchronizePhotos() {
        UpdatePhoto updatePhoto = new UpdatePhoto(lifecycleOwner, propertyViewModel, this, propertiesDown, context);
        updatePhoto.updateData();
    }

    /**
     * Synchronize local database PoiNextProperties with firebase database PoiNextProperties
     */
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
        callback.synchronisationComplete();
    }
}