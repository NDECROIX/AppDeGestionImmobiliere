package com.openclassrooms.realestatemanager;

import android.app.Application;

public class RealEstateManager extends Application {

    private boolean isSyncData;

    public boolean isSyncData(){
        if (!isSyncData){
            isSyncData = true;
            return false;
        }else {
            return true;
        }
    }
}
