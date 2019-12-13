package com.openclassrooms.realestatemanager.injections;

import android.content.Context;

import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.repositories.AgentDataRepository;
import com.openclassrooms.realestatemanager.repositories.PhotoDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiDataRepository;
import com.openclassrooms.realestatemanager.repositories.PoiNextPropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.PropertyDataRepository;
import com.openclassrooms.realestatemanager.repositories.TypeDataRepository;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Injection {

    public static PhotoDataRepository providePhotoDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PhotoDataRepository(database.photoDAO());
    }

    public static PoiDataRepository providePoiDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PoiDataRepository(database.poiDAO());
    }

    public static TypeDataRepository provideTypeDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new TypeDataRepository(database.typeDAO());
    }

    public static PoiNextPropertyDataRepository providePoiNextPropertyDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PoiNextPropertyDataRepository(database.poiNextPropertyDAO());
    }

    public static PropertyDataRepository providePropertyDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new PropertyDataRepository(database.propertyDAO());
    }

    public static AgentDataRepository provideAgentDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        return new AgentDataRepository(database.agentDAO());
    }

    public static Executor provideExecutor() {
        return Executors.newSingleThreadExecutor();
    }

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
