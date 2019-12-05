package com.openclassrooms.realestatemanager.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final PhotoDataRepository photoDataRepository;
    private final PoiDataRepository poiDataRepository;
    private final PoiNextPropertyDataRepository poiNextPropertyDataRepository;
    private final PropertyDataRepository propertyDataRepository;
    private final Executor executor;

    public ViewModelFactory(PhotoDataRepository photoDataRepository,
                            PoiDataRepository poiDataRepository,
                            PoiNextPropertyDataRepository poiNextPropertyDataRepository,
                            PropertyDataRepository propertyDataRepository, Executor executor) {
        this.photoDataRepository = photoDataRepository;
        this.poiDataRepository = poiDataRepository;
        this.poiNextPropertyDataRepository = poiNextPropertyDataRepository;
        this.propertyDataRepository = propertyDataRepository;
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PropertyViewModel.class)){
            return (T) new PropertyViewModel(photoDataRepository, poiDataRepository,
                    poiNextPropertyDataRepository, propertyDataRepository, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}