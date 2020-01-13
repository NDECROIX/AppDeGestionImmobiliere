package com.openclassrooms.realestatemanager.repositories;

import androidx.lifecycle.LiveData;

import com.openclassrooms.realestatemanager.database.dao.TypeDAO;
import com.openclassrooms.realestatemanager.model.Type;

import java.util.List;

/**
 * Repository of type
 */
public class TypeDataRepository {

    private final TypeDAO typeDAO;

    public TypeDataRepository(TypeDAO typeDAO) {
        this.typeDAO = typeDAO;
    }

    /**
     * Get all POI from the application database
     *
     * @return List of all POI
     */
    public LiveData<List<Type>> getTypes() {
        return this.typeDAO.getAll();
    }

}