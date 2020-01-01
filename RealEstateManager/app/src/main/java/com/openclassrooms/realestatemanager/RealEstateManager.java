package com.openclassrooms.realestatemanager;

import android.app.Application;

import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

public class RealEstateManager extends Application {

    private boolean isSyncData;
    private PropertyViewModel propertyViewModel;

    public boolean isSyncData() {

        if (!isSyncData) {
            isSyncData = true;
            return false;
        } else {
            return true;
        }
    }
}