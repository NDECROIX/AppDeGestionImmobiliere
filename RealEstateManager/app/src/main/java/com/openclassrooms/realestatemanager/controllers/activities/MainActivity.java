package com.openclassrooms.realestatemanager.controllers.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.FilterDialogFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.ListFragment;
import com.openclassrooms.realestatemanager.database.updates.UpdateData;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.io.Serializable;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends BaseActivity implements ListPropertyRecyclerViewAdapter.PropertyOnClickListener,
        EditActivity.startEditActivityListener, UpdateData.UpdateDataListener {

    @BindView(R.id.main_activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView navigationView;
    @Nullable
    @BindView(R.id.activity_main_frame_layout_detail)
    FrameLayout frameLayoutDetail;
    @BindView(R.id.main_activity_tv_empty)
    TextView noProperty;

    public static final int RC_READ_WRITE = 854;

    private PropertyViewModel propertyViewModel;

    //Fragments
    private Fragment activeFragment = new Fragment();
    private final ListFragment listFragment = new ListFragment();
    private final DetailFragment detailFragment = new DetailFragment();

    private Property property;
    private List<Photo> photos;
    private List<PoiNextProperty> poiNextProperties;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configViewModel();
        activeFragment = listFragment;
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);
        configFragment();
        configToolbar();
        configDrawerLayout();
    }

    private void configFragment() {
        this.getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_frame_layout, activeFragment)
                .commitNow();
        if (frameLayoutDetail != null) {
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout_detail, detailFragment)
                    .commitNow();
        }
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
        if (property != null) {
            propertyViewModel.setCurrentProperty(property);
            propertyViewModel.setCurrentPoisNextProperty(poiNextProperties);
            propertyViewModel.setCurrentPhotos(photos);
        }
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().show();
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

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
                startActivity(new Intent(this, MapsActivity.class));
                break;
            case R.id.activity_main_drawer_subscribe:
                subscribeToTopics();
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

    private void subscribeToTopics() {
        FirebaseMessaging.getInstance().subscribeToTopic("newAgent")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showToastMessage(this, "Fail to subscribe");
                    }
                    showToastMessage(this, "Subscribe OK");
                });
        FirebaseMessaging.getInstance().subscribeToTopic("newProperty")
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        showToastMessage(this, "Fail to subscribe");
                    }
                    showToastMessage(this, "Subscribe OK");
                });
    }

    @AfterPermissionGranted(RC_READ_WRITE)
    public void synchronizeData() {
        if (!Utils.isInternetAvailable(this)) {
            showToastMessage(this, "No internet");
            return;
        }
        if (EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
            UpdateData updateData = new UpdateData(this, propertyViewModel, this, this);
            customToast(this, "Synchronization start");
            updateData.startSynchronisation();
        } else {
            getPermission();
        }
    }

    private void startFilterDialogFragment() {
        if (activeFragment != listFragment) {
            return;
        }
        FilterDialogFragment agentDialogFragment = new FilterDialogFragment(this, (ListFragment) activeFragment,
                this.getLayoutInflater(), getSupportFragmentManager());
        agentDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void editProperty() {
        if (propertyViewModel.getCurrentProperty() != null) {
            startActivity(EditActivity.newIntent(this, propertyViewModel.getCurrentProperty().getValue(),
                    propertyViewModel.getCurrentPoisNextProperty().getValue(), propertyViewModel.getCurrentPhotosProperty().getValue()));
        } else {
            showToastMessage(this, "No property exist!");
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

    @Override
    public void firstPropertyAdded(Property property, List<Photo> photos) {
        noProperty.setVisibility(View.GONE);
        if (this.property == null) {
            propertyViewModel.setCurrentProperty(property);
            propertyViewModel.setCurrentPhotos(photos);
        }
    }

    @Override
    public void recyclerViewEmpty() {
        noProperty.setVisibility(View.VISIBLE);
        if (frameLayoutDetail != null && detailFragment.getView() != null) {
            detailFragment.getView()
                    .findViewById(R.id.fragment_detail_main_layout).setVisibility(View.GONE);
        }
    }

    @Override
    public void createProperty() {
        startActivity(EditActivity.newIntent(this, null, null, null));
    }

    public static final String PROPERTY = "property";
    public static final String PHOTOS = "photo";
    public static final String POIS = "pois";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(PROPERTY, propertyViewModel.getCurrentProperty().getValue());
        outState.putSerializable(PHOTOS, (Serializable) propertyViewModel.getCurrentPhotosProperty().getValue());
        outState.putSerializable(POIS, (Serializable) propertyViewModel.getCurrentPoisNextProperty().getValue());
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        property = (Property) savedInstanceState.getSerializable(PROPERTY);
        photos = (List<Photo>) savedInstanceState.getSerializable(PHOTOS);
        poiNextProperties = (List<PoiNextProperty>) savedInstanceState.getSerializable(POIS);
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

    @Override
    public void synchronisationComplete() {
        customToast(this, "Synchronization complete");
    }

    @Override
    public void notification(String notification) {
        showToastMessage(this, notification);
    }

    /**
     * Get the read/write external storage permission.
     */
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    RC_READ_WRITE);
        }
    }
}