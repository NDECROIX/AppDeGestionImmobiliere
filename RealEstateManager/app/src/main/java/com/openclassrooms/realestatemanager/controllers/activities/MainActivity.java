package com.openclassrooms.realestatemanager.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.RealEstateManager;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.FilterDialogFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.ListFragment;
import com.openclassrooms.realestatemanager.database.AppDatabase;
import com.openclassrooms.realestatemanager.database.updates.UpdateData;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Filter;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodel.PropertyViewModel;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity implements ListPropertyRecyclerViewAdapter.PropertyOnClickListener,
        EditActivity.startEditActivityListener, UpdateData.UpdateDataListener, FilterDialogFragment.FilterListener {

    // Views
    @BindView(R.id.main_activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView navigationView;
    @Nullable
    @BindView(R.id.activity_main_frame_layout_detail)
    public FrameLayout frameLayoutDetail;
    @BindView(R.id.main_activity_tv_empty)
    TextView noProperty;
    @BindView(R.id.main_activity_progress_bar)
    ProgressBar progressBar;

    public static final String SUBSCRIBE_TOPICS_TOKEN = "subscribe_topics_token";

    // View model
    private PropertyViewModel propertyViewModel;

    //Fragments
    private Fragment activeFragment = new Fragment();
    private final ListFragment listFragment = new ListFragment();
    private final DetailFragment detailFragment = new DetailFragment();

    // Save on rotation
    private Property property;
    private List<Photo> photos;
    private List<PoiNextProperty> poiNextProperties;
    private Filter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configToolbar();
        configViewModel();
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);
        configFragment();
        configProgressBar();
        configDrawerLayout();
        if (Utils.isInternetAvailable(this)) {
            configSubscribeToTopics();
            if (!((RealEstateManager) getApplication()).isSyncData()) {
                synchronizeData();
            }
        }
    }

    /**
     * Configure the progress bar that starts during data synchronization
     */
    private void configProgressBar() {
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(
                this.getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    /**
     * Subscribe the user to firebase topics if token has be change
     */
    private void configSubscribeToTopics() {
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String lastToken = sharedPref.getString(SUBSCRIBE_TOPICS_TOKEN, "");
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful() || task.getResult() == null) {
                        return;
                    }
                    // Get new Instance ID token
                    String newToken = task.getResult().getToken();
                    if (!lastToken.equals(newToken)) {
                        subscribeToTopics();
                        sharedPref.edit().putString(SUBSCRIBE_TOPICS_TOKEN, newToken).apply();
                    }
                });
    }

    /**
     * Display fragment(s)
     */
    private void configFragment() {
        activeFragment = listFragment;
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_frame_layout, activeFragment)
                .commitNow();
        if (frameLayoutDetail != null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout_detail, detailFragment)
                    .commitNow();
        }
    }

    /**
     * Init the property view model
     */
    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(AppDatabase.getInstance(this));
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        if (property != null) {
            propertyViewModel.setCurrentProperty(property);
            propertyViewModel.setCurrentPoisNextProperty(poiNextProperties);
            propertyViewModel.setCurrentPhotos(photos);
        }
        if (filter != null){
            propertyViewModel.setCurrentFilter(filter);
        }
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    /**
     * Replace fragment if active fragment is detail fragment.
     */
    @Override
    public void onBackPressed() {
        if (activeFragment == detailFragment) {
            activeFragment = listFragment;
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, activeFragment)
                    .commitNow();
            configToolbar();
            configDrawerLayout();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (frameLayoutDetail == null) {
            getMenuInflater().inflate(R.menu.activity_main_toolbar_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.activity_main_toolbar_menu_tablet, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_main_add:
                createProperty();
                break;
            case R.id.menu_activity_main_edit:
            case R.id.menu_fragment_detail_edit:
                editProperty();
                break;
            case R.id.menu_activity_main_search:
                startFilterDialogFragment();
                break;
            case R.id.activity_main_drawer_agent:
                startActivity(new Intent(this, AgentActivity.class));
                break;
            case R.id.activity_main_drawer_map:
                if (Utils.isInternetAvailable(this)){
                    startActivity(new Intent(this, MapsActivity.class));
                } else {
                    customToast(this, "No internet");
                }
                break;
            case R.id.activity_main_drawer_synchronize:
                synchronizeData();
                drawerLayout.closeDrawers();
                break;
            case android.R.id.home:
                if (activeFragment == detailFragment) onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Subscribe to topics to be notified when new agent or property is added on firebase
     */
    private void subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("newAgent")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showToastMessage(this, "Fail to subscribe on new agents");
                    }
                    customToast(this, "Subscribe to new agents");
                });
        FirebaseMessaging.getInstance().subscribeToTopic("newProperty")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showToastMessage(this, "Fail to subscribe on new properties");
                    }
                    customToast(this, "Subscribe to new properties");
                });
    }

    /**
     * Synchronize the local database with firebase database
     */
    @AfterPermissionGranted(RC_READ_WRITE)
    public void synchronizeData() {
        if (progressBar != null && progressBar.getVisibility() != View.GONE) {
            return;
        }
        if (!Utils.isInternetAvailable(this)) {
            showToastMessage(this, "No internet");
            return;
        }
        if (EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
            UpdateData updateData = new UpdateData(this, propertyViewModel, this, this);
            progressBar.setVisibility(View.VISIBLE);
            updateData.startSynchronisation();
        } else {
            getReadWriteExternalStoragePermission();
        }
    }

    /**
     * Display the filter fragment
     */
    private void startFilterDialogFragment() {
        if (activeFragment != listFragment) {
            return;
        }
        FilterDialogFragment agentDialogFragment = new FilterDialogFragment(this, this,
                this.getLayoutInflater(), getSupportFragmentManager());
        agentDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Start Edit property to update a property
     */
    private void editProperty() {
        if (propertyViewModel.getCurrentProperty() != null) {
            startActivity(EditActivity.newIntent(this, propertyViewModel.getCurrentProperty().getValue(),
                    propertyViewModel.getCurrentPoisNextProperty().getValue(), propertyViewModel.getCurrentPhotosProperty().getValue()));
        } else {
            showToastMessage(this, "No property to edit!");
        }
    }

    /**
     * Configures the Drawer layout
     */
    private void configDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    // Display the details of the selected property
    @Override
    public void onClickPropertyListener(Property property, List<Photo> photos) {
        propertyViewModel.setCurrentProperty(property);
        propertyViewModel.setCurrentPhotos(photos);
        if (frameLayoutDetail == null) {
            activeFragment = detailFragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, activeFragment)
                    .commitNow();
        }
    }

    // Update views with the first property added in the recycler view
    @Override
    public void firstPropertyAdded(Property property, List<Photo> photos) {
        noProperty.setVisibility(View.GONE);
        if (this.property == null) {
            propertyViewModel.setCurrentProperty(property);
            propertyViewModel.setCurrentPhotos(photos);
        }
    }

    // Notified an empty recycler view
    @Override
    public void recyclerViewEmpty() {
        noProperty.setVisibility(View.VISIBLE);
        if (frameLayoutDetail != null && detailFragment.getView() != null) {
            detailFragment.getView()
                    .findViewById(R.id.fragment_detail_main_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_READ_WRITE) {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        } else {
            activeFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Synchronization with firebase database complete
    @Override
    public void synchronisationComplete() {
        customToast(this, "Synchronization complete");
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    // Error on the synchronization with firebase database
    @Override
    public void notification(String notification) {
        showToastMessage(this, notification);
    }

    // Start Edit activity to create a property
    @Override
    public void createProperty() {
        startActivity(EditActivity.newIntent(this, null, null, null));
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        if (frameLayoutDetail != null && !detailFragment.isInLayout()) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout_detail, detailFragment)
                    .commitNow();
        }
        super.onResume();
    }

    public static final String FILTER = "filter";
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        if (frameLayoutDetail != null) {
            getSupportFragmentManager().beginTransaction().remove(detailFragment).commitAllowingStateLoss();
        }
        outState.putParcelable(PROPERTY, propertyViewModel.getCurrentProperty().getValue());
        outState.putSerializable(PHOTOS, (Serializable) propertyViewModel.getCurrentPhotosProperty().getValue());
        outState.putSerializable(POIS, (Serializable) propertyViewModel.getCurrentPoisNextProperty().getValue());
        if (activeFragment == listFragment){
            outState.putParcelable(FILTER, ((ListFragment) activeFragment).getFilter());
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        property = savedInstanceState.getParcelable(PROPERTY);
        photos = (List<Photo>) savedInstanceState.getSerializable(PHOTOS);
        poiNextProperties = (List<PoiNextProperty>) savedInstanceState.getSerializable(POIS);
        filter = savedInstanceState.getParcelable(FILTER);
        if (propertyViewModel != null) {
            configViewModel();
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onApplyFilter(Filter filter) {
        propertyViewModel.setCurrentFilter(filter);
    }

    @Override
    public void filterError(String message) {
        customToast(this, message);
    }
}