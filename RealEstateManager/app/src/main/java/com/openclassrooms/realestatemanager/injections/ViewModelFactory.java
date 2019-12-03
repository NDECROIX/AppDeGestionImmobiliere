package com.openclassrooms.realestatemanager.injections;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.concurrent.Executor;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private final PropertyDataRepository propertyDataRepository;
    private final Executor executor;

    public ViewModelFactory(PropertyDataRepository propertyDataRepository, Executor executor) {
        this.propertyDataRepository = propertyDataRepository;
        this.executor = executor;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(PropertyViewModel.class)){
            return (T) new PropertyViewModel(propertyDataRepository, executor);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
