package com.openclassrooms.realestatemanager.controllers.activities;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.MapsFragment;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

public class MapsActivity extends BaseActivity implements GoogleMap.OnMarkerClickListener {

    @BindView(R.id.maps_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_maps_frame_layout)
    FrameLayout frameLayout;
    @Nullable
    @BindView(R.id.activity_maps_frame_layout_detail)
    FrameLayout frameLayoutDetail;

    private final MapsFragment mapFragment = new MapsFragment();
    private final DetailFragment detailFragment = new DetailFragment();
    private Fragment currentFragment = mapFragment;

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
        if (currentFragment == mapFragment){
            currentFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Maps");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        MapsFragment.MarkerObject markerObject = (MapsFragment.MarkerObject) marker.getTag();
        if (markerObject == null) {
            return false;
        }
        propertyViewModel.setCurrentProperty(markerObject.property);
        propertyViewModel.setCurrentPhotos(new ArrayList<>(markerObject.photos));
        if (frameLayoutDetail == null) {
            currentFragment = detailFragment;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_maps_frame_layout, currentFragment)
                    .addToBackStack(null)
                    .commit();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (frameLayoutDetail == null && currentFragment != mapFragment) {
            currentFragment = mapFragment;
            getSupportFragmentManager().popBackStack();/*.beginTransaction()
                    .replace(R.id.activity_maps_frame_layout, currentFragment)
                    .commitNow();*/
            return;
        }
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
