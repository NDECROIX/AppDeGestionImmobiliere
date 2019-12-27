package com.openclassrooms.realestatemanager.controllers.fragments;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.controllers.activities.MapsActivity;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.adapters.DetailFragmentPhotoRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.INTERNET;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback, DetailFragmentPhotoRecyclerViewAdapter.OnClickPhotoListener {

    @Nullable
    @BindView(R.id.fragment_detail_toolbar)
    Toolbar toolbar;
    @BindView(R.id.fragment_detail_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_detail_tv_type)
    TextView type;
    @BindView(R.id.fragment_detail_tv_price)
    TextView price;
    @BindView(R.id.fragment_detail_tv_description_text)
    TextView description;
    @BindView(R.id.fragment_detail_tv_surface)
    TextView surface;
    @BindView(R.id.fragment_detail_tv_room)
    TextView room;
    @BindView(R.id.fragment_detail_tv_bathroom)
    TextView bathroom;
    @BindView(R.id.fragment_detail_tv_bedroom)
    TextView bedroom;
    @BindView(R.id.fragment_detail_tv_address)
    TextView address;
    @BindView(R.id.fragment_detail_tv_poi_left)
    LinearLayout poiGrpLeft;
    @BindView(R.id.fragment_detail_tv_poi_right)
    LinearLayout poiGrpRight;
    @BindView(R.id.fragment_detail_tv_date)
    TextView date;
    @BindView(R.id.fragment_detail_tv_agent)
    TextView agent;
    @Nullable
    @BindView(R.id.fragment_detail_sv)
    NestedScrollView nestedScrollView;
    @BindView(R.id.fragment_detail_tv_sold)
    TextView tvSold;
    @Nullable
    @BindView(R.id.fragment_detail_tv_empty)
    TextView empty;

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 898;

    private Context context;
    private PropertyViewModel propertyViewModel;
    private GoogleMap mMap;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyViewModel = ViewModelProviders.of((FragmentActivity) context).get(PropertyViewModel.class);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        if (getActivity() != null && getActivity().findViewById(R.id.activity_main_frame_layout_detail) == null) {
            configToolbar();
        }
        configRecyclerView();
        getPropertyFromDatabase();
        // Get the map and register for the ready callback
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_detail_map_view);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        return view;
    }

    private void configToolbar() {
        if (getActivity() instanceof MapsActivity) {
            ActionBar actionBar = ((MapsActivity) context).getSupportActionBar();
            if (actionBar != null) actionBar.setTitle("Details");
            if (toolbar != null)
                toolbar.setVisibility(View.GONE);
            return;
        }
        // Hide main toolbar
        ActionBar supportActionBar = ((MainActivity) context).getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.hide();
        }

        // Show Fragment toolbar
        setHasOptionsMenu(true);
        ((MainActivity) context).setSupportActionBar(toolbar);
        ActionBar actionBar = ((MainActivity) context).getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Detail");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (context instanceof MainActivity) {
            menu.findItem(R.id.menu_activity_main_search).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (context instanceof MainActivity) {
            inflater.inflate(R.menu.fragment_detail_toolbar_menu, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        DetailFragmentPhotoRecyclerViewAdapter adapter = new DetailFragmentPhotoRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        propertyViewModel.getCurrentPhotosProperty().observe(getViewLifecycleOwner(), adapter::setPhotos);
    }

    @Override
    public void onClickPhoto(Photo photo) {

    }

    private void displayPropertyData(Property property) {
        if (empty != null) empty.setVisibility(View.GONE);
        type.setText(property.getType());
        description.setText(property.getDescription());
        price.setText(String.format("Price : $ %s ", Utils.getPrice(property.getPrice())));
        surface.setText(String.format(Locale.getDefault(), "Surface : %s sq m", property.getSurface()));
        room.setText(String.format(Locale.getDefault(), "Number of rooms : %d", property.getRooms()));
        bathroom.setText(String.format(Locale.getDefault(), "Number of bathrooms : %d", property.getBathrooms()));
        bedroom.setText(String.format(Locale.getDefault(), "Number of bedrooms : %d", property.getBedrooms()));
        String supplement = property.getAddressSupplement();
        supplement = (supplement == null) ? "" : "\n" + supplement;
        address.setText(String.format(Locale.getDefault(), "%d %s %s\n%s\n%d\n%s",
                property.getStreetNumber(), property.getStreetName(), supplement,
                property.getCity(), property.getZip(), property.getCountry()));
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(property.getEntryDate());
        date.setText(String.format(Locale.getDefault(), "%d/%d/%d",
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        if (property.isSold()) {
            tvSold.setVisibility(View.VISIBLE);
            calendar = new GregorianCalendar();
            calendar.setTimeInMillis(property.getSaleDate());
            date.append(String.format(Locale.getDefault(), " - %d/%d/%d",
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        } else {
            tvSold.setVisibility(View.GONE);
        }
        if (property.getAgentID() != null && !property.getAgentID().isEmpty()) {
            agent.setText(property.getAgentID());
            propertyViewModel.getAgent(property.getAgentID()).observe(getViewLifecycleOwner(), agent ->
                    this.agent.setText(String.format("%s %s", agent.getFirstName(), agent.getLastName())));
        }
        if (mMap != null) getPictureAddress();
    }

    private void getPropertyFromDatabase() {
        if (nestedScrollView != null) {
            nestedScrollView.setVisibility(View.GONE);
        }
        propertyViewModel.getCurrentProperty().observe(getViewLifecycleOwner(), property -> {
            displayPropertyData(property);
            propertyViewModel.getPoisNextProperty(property.getId())
                    .observe(getViewLifecycleOwner(), this::displayPoi);
            if (nestedScrollView != null && nestedScrollView.getVisibility() == View.GONE) {
                nestedScrollView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayPoi(List<PoiNextProperty> poiNextProperty) {
        propertyViewModel.setCurrentPoisNextProperty(poiNextProperty);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        poiGrpRight.removeAllViews();
        poiGrpLeft.removeAllViews();
        boolean left = true;
        for (PoiNextProperty poi : poiNextProperty) {
            TextView poiName = new TextView(getActivity());
            poiName.setText(poi.getPoiName());
            poiName.setLayoutParams(params);
            if (left) {
                poiGrpLeft.addView(poiName);
            } else {
                poiGrpRight.addView(poiName);
            }
            left = !left;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        getPictureAddress();
    }

    @AfterPermissionGranted(PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
    private void getPictureAddress() {
        if (EasyPermissions.hasPermissions(context, ACCESS_FINE_LOCATION)) {
            Property property = propertyViewModel.getCurrentProperty().getValue();
            if (property == null) return;
            if (property.getLatitude() == null || property.getLatitude() == 0) {
                String address = String.format("%s %s, %s, %s, %s", property.getStreetNumber(),
                        property.getStreetName(), property.getCity(), property.getCountry(),
                        property.getZip());
                LatLng latLng = Utils.getLocationFromAddress(context, address);
                if (latLng != null) {
                    property.setLatitude(latLng.latitude);
                    property.setLongitude(latLng.longitude);
                    propertyViewModel.updateProperty(property);
                }
            }
            LatLng latLng = new LatLng(property.getLatitude(), property.getLongitude());
            mMap.addMarker(new MarkerOptions().position(latLng));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
        } else {
            getAccessFineLocationPermission();
        }
    }

    /**
     * Get the location permission.
     */
    private void getAccessFineLocationPermission() {
        if (getActivity() != null && ContextCompat.checkSelfPermission(context,
                ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{ACCESS_FINE_LOCATION, WRITE_EXTERNAL_STORAGE, INTERNET},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
