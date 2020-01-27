package com.openclassrooms.nycrealestatemanager.injections;

        import com.openclassrooms.nycrealestatemanager.database.AppDatabase;
        import com.openclassrooms.nycrealestatemanager.repositories.AgentDataRepository;
        import com.openclassrooms.nycrealestatemanager.repositories.PhotoDataRepository;
        import com.openclassrooms.nycrealestatemanager.repositories.PoiNextPropertyDataRepository;
        import com.openclassrooms.nycrealestatemanager.repositories.PropertyDataRepository;

        import java.util.concurrent.Executor;
        import java.util.concurrent.Executors;

/**
 * Data injection in a view model factory
 */
public class Injection {

    /**
     * Provide a photo data source
     *
     * @param appDatabase Activity context
     * @return PhotoDataRepository
     */
    private static PhotoDataRepository providePhotoDataSource(AppDatabase appDatabase) {
        return new PhotoDataRepository(appDatabase.photoDAO());
    }

    /**
     * Provide a PoiNextProperty data source
     *
     * @param appDatabase Activity context
     * @return PoiNextPropertyDataRepository
     */
    private static PoiNextPropertyDataRepository providePoiNextPropertyDataSource(AppDatabase appDatabase) {
        return new PoiNextPropertyDataRepository(appDatabase.poiNextPropertyDAO());
    }

    /**
     * Provide a property data source
     *
     * @param appDatabase Activity source
     * @return PropertyDataRepository
     */
    private static PropertyDataRepository providePropertyDataSource(AppDatabase appDatabase) {
        return new PropertyDataRepository(appDatabase.propertyDAO());
    }

    /**
     * Provide a agent data source
     *
     * @param appDatabase Activity context
     * @return AgentDataRepository
     */
    private static AgentDataRepository provideAgentDataSource(AppDatabase appDatabase) {
        return new AgentDataRepository(appDatabase.agentDAO());
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
        PoiNextPropertyDataRepository poiNextPropertyDataRepository = providePoiNextPropertyDataSource(appDatabase);
        PropertyDataRepository dataSourceProperty = providePropertyDataSource(appDatabase);
        AgentDataRepository agentDataRepository = provideAgentDataSource(appDatabase);
        Executor executor = provideExecutor();
        return new ViewModelFactory(photoDataRepository, poiNextPropertyDataRepository, dataSourceProperty, agentDataRepository, executor);
    }
}