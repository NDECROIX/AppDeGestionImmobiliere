package com.openclassrooms.nycrealestatemanager.controllers.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.base.BaseActivity;
import com.openclassrooms.nycrealestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.nycrealestatemanager.controllers.fragments.MapsFragment;
import com.openclassrooms.nycrealestatemanager.database.AppDatabase;
import com.openclassrooms.nycrealestatemanager.injections.Injection;
import com.openclassrooms.nycrealestatemanager.injections.ViewModelFactory;
import com.openclassrooms.nycrealestatemanager.viewmodel.PropertyViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Display a google map with properties as marker
 */
public class MapsActivity extends BaseActivity implements GoogleMap.OnMarkerClickListener {

    // Views
    @BindView(R.id.maps_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_maps_frame_layout)
    FrameLayout frameLayout;
    @Nullable
    @BindView(R.id.activity_maps_frame_layout_detail)
    public FrameLayout frameLayoutDetail;

    // Fragments
    private final MapsFragment mapFragment = new MapsFragment();
    private final DetailFragment detailFragment = new DetailFragment();
    private Fragment currentFragment = mapFragment;

    // View model
    private PropertyViewModel propertyViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        configViewModel();
        configFragment();
        configToolbar();
    }

    /**
     * Display fragment(s)
     */
    private void configFragment() {
        this.getSupportFragmentManager().beginTransaction()
                .add(frameLayout.getId(), currentFragment)
                .commitNow();
        if (frameLayoutDetail != null) {
            this.getSupportFragmentManager().beginTransaction()
                    .add(frameLayoutDetail.getId(), detailFragment)
                    .commitNow();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (currentFragment == mapFragment) {
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    // Configure view model
    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(AppDatabase.getInstance(this));
        propertyViewModel = new ViewModelProvider(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Maps");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // Display property details by clicking on the property marker
    @Override
    public boolean onMarkerClick(Marker marker) {
        MapsFragment.MarkerObject markerObject = (MapsFragment.MarkerObject) marker.getTag();
        if (markerObject == null) {
            return false;
        }
        propertyViewModel.setCurrentProperty(markerObject.property, this);
        //propertyViewModel.setCurrentPhotos(new ArrayList<>(markerObject.photos));
        if (frameLayoutDetail == null) {
            currentFragment = detailFragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_maps_frame_layout, currentFragment)
                    .addToBackStack(null)
                    .commit();
        } else {
            frameLayoutDetail.setVisibility(View.VISIBLE);
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (frameLayoutDetail == null && currentFragment != mapFragment) {
            currentFragment = mapFragment;
            getSupportFragmentManager().popBackStack();
            return;
        } else if (frameLayoutDetail != null && frameLayoutDetail.getVisibility() == View.VISIBLE) {
            frameLayoutDetail.setVisibility(View.GONE);
            return;
        }
        this.finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
