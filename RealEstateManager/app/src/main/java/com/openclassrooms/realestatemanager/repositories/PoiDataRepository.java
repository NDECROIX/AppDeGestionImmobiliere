package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.PoiDAO;
import com.openclassrooms.realestatemanager.model.POI;

import java.util.List;

public class PoiDataRepository {

    private final PoiDAO poiDAO;

    public PoiDataRepository(PoiDAO poiDAO) {
        this.poiDAO = poiDAO;
    }

    /**
     * Get all POI from the application database
     *
     * @return List of all POI
     */
    public LiveData<List<POI>> getPOIs() {
        return this.poiDAO.getAll();
    }

    /**
     * Insert a POI in the database
     *
     * @param poi New POI
     */
    public void insertPoiNextProperty(POI poi) {
        this.poiDAO.insertPOI(poi);
    }
}
