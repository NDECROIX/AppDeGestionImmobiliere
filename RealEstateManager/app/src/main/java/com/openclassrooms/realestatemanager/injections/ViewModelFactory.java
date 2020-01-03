package com.openclassrooms.realestatemanager.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.TypeDataRepository;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.concurrent.Executor;

/**
 * View model factory
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    // Repositories
    private final PhotoDataRepository photoDataRepository;
    private final PoiDataRepository poiDataRepository;
    private final TypeDataRepository typeDataRepository;
    private final PoiNextPropertyDataRepository poiNextPropertyDataRepository;
    private final PropertyDataRepository propertyDataRepository;
    private final AgentDataRepository agentDataRepository;
    private final Executor executor;

    // View model for PropertyViewModel
    ViewModelFactory(PhotoDataRepository photoDataRepository,
                     PoiDataRepository poiDataRepository,
                     TypeDataRepository typeDataRepository,
                     PoiNextPropertyDataRepository poiNextPropertyDataRepository,
                     PropertyDataRepository propertyDataRepository, AgentDataRepository agentDataRepository, Executor executor) {
        this.photoDataRepository = photoDataRepository;
        this.poiDataRepository = poiDataRepository;
        this.typeDataRepository = typeDataRepository;
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
            return (T) new PropertyViewModel(photoDataRepository, poiDataRepository, typeDataRepository,
                    poiNextPropertyDataRepository, propertyDataRepository, agentDataRepository, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}