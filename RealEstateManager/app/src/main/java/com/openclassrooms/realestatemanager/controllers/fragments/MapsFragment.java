package com.openclassrooms.realestatemanager.controllers.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.MapsActivity;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.viewmodel.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

/**
 * Display a google map view
 */
public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener {

    /**
     * Object as a tag in the marker that contains property and photos
     */
    public class MarkerObject {
        public Property property;
        public List<Photo> photos;

        MarkerObject(Property property, List<Photo> photos) {
            this.property = property;
            this.photos = new ArrayList<>(photos);
        }
    }

    // Request permission to fine location
    private static final int PERMISSIONS_REQUEST_LOCATION_CENTRED = 898;
    private static final int PERMISSIONS_REQUEST_LOCATION_UI = 324;
    private static final int DEFAULT_ZOOM = 18;

    // View model
    private PropertyViewModel propertyViewModel;
    private GoogleMap mMap;

    // Init detail with first property

    // Activity context
    private Context context;

    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        ButterKnife.bind(this, view);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_maps_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        configViewModel();
        configToolbar();
        return view;
    }

    private void configToolbar() {
        ActionBar actionBar = ((MapsActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Maps");
        }
    }

    private void configViewModel() {
        propertyViewModel = ViewModelProviders.of((MapsActivity) context).get(PropertyViewModel.class);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        updateLocationSetting();
    }

    /**
     * Updating the parameters of the mMap
     */
    @AfterPermissionGranted(PERMISSIONS_REQUEST_LOCATION_UI)
    private void updateLocationSetting() {
        if (EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION)) {
            mMap.getUiSettings().setMapToolbarEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) context);
            mMap.setMyLocationEnabled(true);
            mMap.setOnMyLocationButtonClickListener(this);
            propertyViewModel.getProperties().observe(this, this::addMarker);
            getCurrentLocation();
        } else {
            getAccessFineLocationPermission(PERMISSIONS_REQUEST_LOCATION_UI);
        }
    }

    /**
     * Add a marker where a property is located
     *
     * @param properties Properties to locate
     */
    private void addMarker(List<Property> properties) {
        mMap.clear();
        for (Property property : properties) {
            if (property.getLatitude() == null || property.getLatitude() == 0) {
                // Get LntLag before displaying the property on the map
                String address = String.format("%s %s, %s, %s, %s", property.getStreetNumber(),
                        property.getStreetName(), property.getCity(), property.getCountry(),
                        property.getZip());
                LatLng latLng = Utils.getLocationFromAddress(getContext(), address);
                if (latLng != null) {
                    property.setLatitude(latLng.latitude);
                    property.setLongitude(latLng.longitude);
                    propertyViewModel.updateProperty(property);
                    addMarkerWithBitmap(property);
                }
            } else {
                addMarkerWithBitmap(property);
            }
        }
    }

    /**
     * Display photo from property as marker
     *
     * @param property Property
     */
    private void addMarkerWithBitmap(Property property) {
        propertyViewModel.getPropertyPhotos(property.getId()).observe(this, photos -> {
            Glide.with(this)
                    .asBitmap()
                    .load(photos.get(0).getUri(context))
                    .apply(RequestOptions.circleCropTransform())
                    .into(new CustomTarget<Bitmap>(80, 80) {
                        @Override
                        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                            MarkerOptions markerOptions = new MarkerOptions();
                            // Get position
                            LatLng latLng = new LatLng(property.getLatitude(), property.getLongitude());
                            markerOptions.position(latLng);
                            // Get bitmap
                            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resource));
                            // Add marker on the map
                            Marker marker = mMap.addMarker(markerOptions);
                            MarkerObject markerObject = new MarkerObject(property, photos);
                            marker.setTag(markerObject);
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                        }
                    });
        });
    }

    /**
     * Get the last known location of the device
     */
    private void getCurrentLocation() {
        if (EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION)) {
            LocationServices.getFusedLocationProviderClient(context).getLastLocation().addOnCompleteListener(locationTask -> {
                if (locationTask.isSuccessful() && locationTask.getResult() != null) {
                    LatLng latLng = new LatLng(locationTask.getResult().getLatitude(), locationTask.getResult().getLongitude());
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM);
                    mMap.animateCamera(cameraUpdate);
                }
            });
        } else {
            getAccessFineLocationPermission(PERMISSIONS_REQUEST_LOCATION_UI);
        }
    }

    /**
     * Get the location permission.
     *
     * @param resultCode Result Code
     */
    private void getAccessFineLocationPermission(int resultCode) {
        if (ContextCompat.checkSelfPermission(context,
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((MapsActivity) context, new String[]{ACCESS_FINE_LOCATION},
                    resultCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if (EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION)) {
            Toast.makeText(getContext(), R.string.maps_fragment_centred, Toast.LENGTH_SHORT).show();
        } else {
            getAccessFineLocationPermission(PERMISSIONS_REQUEST_LOCATION_CENTRED);
        }
        return true;
    }
}