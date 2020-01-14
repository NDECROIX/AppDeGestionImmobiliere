package com.openclassrooms.realestatemanager.injections;

import android.content.Context;

import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.TypeDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Data injection in a view model factory
 */
public class Injection {

    /**
     * Provide a photo data source
     *
     * @param context Activity context
     * @return PhotoDataRepository
     */
    private static PhotoDataRepository providePhotoDataSource(AppDatabase context) {
        AppDatabase database = context;
        return new PhotoDataRepository(database.photoDAO());
    }

    /**
     * Provide a poi data source
     *
     * @param context Activity context
     * @return PoiDataRepository
     */
    private static PoiDataRepository providePoiDataSource(AppDatabase context) {
        AppDatabase database = context;
        return new PoiDataRepository(database.poiDAO());
    }

    /**
     * Provide a type data source
     *
     * @param context Activity context
     * @return TypeDataRepository
     */
    private static TypeDataRepository provideTypeDataSource(AppDatabase context) {
        AppDatabase database = context;
        return new TypeDataRepository(database.typeDAO());
    }

    /**
     * Provide a PoiNextProperty data source
     *
     * @param context Activity context
     * @return PoiNextPropertyDataRepository
     */
    private static PoiNextPropertyDataRepository providePoiNextPropertyDataSource(AppDatabase context) {
        AppDatabase database = context;
        return new PoiNextPropertyDataRepository(database.poiNextPropertyDAO());
    }

    /**
     * Provide a property data source
     *
     * @param context Activity source
     * @return PropertyDataRepository
     */
    private static PropertyDataRepository providePropertyDataSource(AppDatabase context) {
        AppDatabase database = context;
        return new PropertyDataRepository(database.propertyDAO());
    }

    /**
     * Provide a agent data source
     *
     * @param context Activity context
     * @return AgentDataRepository
     */
    private static AgentDataRepository provideAgentDataSource(AppDatabase context) {
        AppDatabase database = context;
        return new AgentDataRepository(database.agentDAO());
    }

    /**
     * Provide an executor to perform an action on a background thread
     *
     * @return Executor
     */
    private static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    /**
     * Provide a view model factory
     *
     * @param appDatabase Activity context
     * @return ViewModelFactory
     */
    public static ViewModelFactory provideViewModelFactory(AppDatabase appDatabase) {
        PhotoDataRepository photoDataRepository = providePhotoDataSource(appDatabase);
        PoiDataRepository poiDataRepository = providePoiDataSource(appDatabase);
        TypeDataRepository typeDataRepository = provideTypeDataSource(appDatabase);
        PoiNextPropertyDataRepository poiNextPropertyDataRepository = providePoiNextPropertyDataSource(appDatabase);
        PropertyDataRepository dataSourceProperty = providePropertyDataSource(appDatabase);
        AgentDataRepository agentDataRepository = provideAgentDataSource(appDatabase);
        Executor executor = provideExecutor();
        return new ViewModelFactory(photoDataRepository, poiDataRepository, typeDataRepository, poiNextPropertyDataRepository, dataSourceProperty, agentDataRepository, executor);
    }
}