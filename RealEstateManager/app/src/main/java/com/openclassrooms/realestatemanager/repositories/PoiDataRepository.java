package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.PoiDAO;
import com.openclassrooms.realestatemanager.model.Poi;

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
    public LiveData<List<Poi>> getPOIs() {
        return this.poiDAO.getAll();
    }

    /**
     * Insert a POI in the database
     *
     * @param poi New POI
     */
    public void insertPoiNextProperty(Poi poi) {
        this.poiDAO.insertPOI(poi);
    }
}
