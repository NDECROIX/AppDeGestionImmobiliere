package com.openclassrooms.nycrealestatemanager;

import android.app.Application;

/**
 * Application class that provides unique data for
 * one application instance.
 */
public class RealEstateManager extends Application {

    private boolean isSyncData;

    /**
     * Synchronize data with firebase when application start
     *
     * @return true if app already sync
     */
    public boolean isSyncData() {
        if (!isSyncData) {
            isSyncData = true;
            return false;
        } else {
            return true;
        }
    }
}