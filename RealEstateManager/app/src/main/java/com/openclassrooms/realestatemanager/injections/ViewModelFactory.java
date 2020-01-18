package com.openclassrooms.realestatemanager.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.viewmodel.PropertyViewModel;

import java.util.concurrent.Executor;

/**
 * View model factory
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    // Repositories
    private final PhotoDataRepository photoDataRepository;
    private final PoiNextPropertyDataRepository poiNextPropertyDataRepository;
    private final PropertyDataRepository propertyDataRepository;
    private final AgentDataRepository agentDataRepository;
    private final Executor executor;

    // View model for PropertyViewModel
    ViewModelFactory(PhotoDataRepository photoDataRepository,
                     PoiNextPropertyDataRepository poiNextPropertyDataRepository,
                     PropertyDataRepository propertyDataRepository, AgentDataRepository agentDataRepository, Executor executor) {
        this.photoDataRepository = photoDataRepository;
        this.poiNextPropertyDataRepository = poiNextPropertyDataRepository;
        this.propertyDataRepository = propertyDataRepository;
        this.agentDataRepository = agentDataRepository;
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PropertyViewModel.class)) {
            return (T) new PropertyViewModel(photoDataRepository,
                    poiNextPropertyDataRepository, propertyDataRepository, agentDataRepository, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}