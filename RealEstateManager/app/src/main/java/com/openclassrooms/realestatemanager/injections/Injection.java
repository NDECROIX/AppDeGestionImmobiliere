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
    private static PhotoDataRepository providePhotoDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PhotoDataRepository(database.photoDAO());
    }

    /**
     * Provide a poi data source
     *
     * @param context Activity context
     * @return PoiDataRepository
     */
    private static PoiDataRepository providePoiDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PoiDataRepository(database.poiDAO());
    }

    /**
     * Provide a type data source
     *
     * @param context Activity context
     * @return TypeDataRepository
     */
    private static TypeDataRepository provideTypeDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new TypeDataRepository(database.typeDAO());
    }

    /**
     * Provide a PoiNextProperty data source
     *
     * @param context Activity context
     * @return PoiNextPropertyDataRepository
     */
    private static PoiNextPropertyDataRepository providePoiNextPropertyDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PoiNextPropertyDataRepository(database.poiNextPropertyDAO());
    }

    /**
     * Provide a property data source
     *
     * @param context Activity source
     * @return PropertyDataRepository
     */
    private static PropertyDataRepository providePropertyDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PropertyDataRepository(database.propertyDAO());
    }

    /**
     * Provide a agent data source
     *
     * @param context Activity context
     * @return AgentDataRepository
     */
    private static AgentDataRepository provideAgentDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
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
     * @param context Activity context
     * @return ViewModelFactory
     */
    public static ViewModelFactory provideViewModelFactory(Context context) {
        PhotoDataRepository photoDataRepository = providePhotoDataSource(context);
        PoiDataRepository poiDataRepository = providePoiDataSource(context);
        TypeDataRepository typeDataRepository = provideTypeDataSource(context);
        PoiNextPropertyDataRepository poiNextPropertyDataRepository = providePoiNextPropertyDataSource(context);
        PropertyDataRepository dataSourceProperty = providePropertyDataSource(context);
        AgentDataRepository agentDataRepository = provideAgentDataSource(context);
        Executor executor = provideExecutor();
        return new ViewModelFactory(photoDataRepository, poiDataRepository, typeDataRepository, poiNextPropertyDataRepository, dataSourceProperty, agentDataRepository, executor);
    }
}