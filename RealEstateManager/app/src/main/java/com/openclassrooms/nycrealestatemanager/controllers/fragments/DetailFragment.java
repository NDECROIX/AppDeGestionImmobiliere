package com.openclassrooms.nycrealestatemanager.controllers.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
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
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.nycrealestatemanager.controllers.activities.MapsActivity;
import com.openclassrooms.nycrealestatemanager.model.Photo;
import com.openclassrooms.nycrealestatemanager.model.PoiNextProperty;
import com.openclassrooms.nycrealestatemanager.model.Property;
import com.openclassrooms.nycrealestatemanager.utils.Utils;
import com.openclassrooms.nycrealestatemanager.view.adapters.DetailFragmentPhotoRecyclerViewAdapter;
import com.openclassrooms.nycrealestatemanager.viewmodel.PropertyViewModel;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import butterknife.BindView;
import butterknife.ButterKnife;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Property detail
 */
public class DetailFragment extends Fragment implements OnMapReadyCallback, DetailFragmentPhotoRecyclerViewAdapter.OnClickPhotoListener {

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

    private Context context;
    private PropertyViewModel propertyViewModel;
    private GoogleMap mMap;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyViewModel = new ViewModelProvider((FragmentActivity) context).get(PropertyViewModel.class);
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
        configToolbar();
        configRecyclerView();
        getPropertyFromDatabase();
        // Get the map and register for the ready callback
        if (Utils.isInternetAvailable(context)) {
            SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_detail_map_view);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }
        return view;
    }

    private void configToolbar() {
        if (getActivity() instanceof MainActivity && ((MainActivity) context).frameLayoutDetail != null) {
            return;
        }
        if (getActivity() instanceof MapsActivity) {
            ActionBar actionBar = ((MapsActivity) context).getSupportActionBar();
            if (actionBar != null && ((MapsActivity) getActivity()).frameLayoutDetail == null) {
                actionBar.setTitle("Details");
            }
            return;
        }
        // Change toolbar
        ActionBar actionBar = ((MainActivity) context).getSupportActionBar();
        if (actionBar != null) {
            // Get Main Toolbar
            Toolbar toolbar = ((MainActivity) context).findViewById(R.id.main_activity_toolbar);
            // Add back button
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
            // Add a listener on the back button
            toolbar.setNavigationOnClickListener((l) -> ((MainActivity) context).onBackPressed());

            actionBar.setTitle("Detail");
        }
        // Change icon
        setHasOptionsMenu(true);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        if (context instanceof MainActivity &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Hide search item
            menu.findItem(R.id.menu_activity_main_search).setVisible(false);
        }
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (context instanceof MainActivity &&
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            // Show edit item
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
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri photoURI = FileProvider.getUriForFile(context,
                getString(R.string.activity_edit_authority_uri), new File(photo.getUri(context)));
        intent.setDataAndType(photoURI, "image/*");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
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
    }

    private void getPropertyFromDatabase() {
        if (nestedScrollView != null) {
            nestedScrollView.setVisibility(View.GONE);
        }
        propertyViewModel.getCurrentProperty().observe(getViewLifecycleOwner(), property -> {
            displayPropertyData(property);
            if (nestedScrollView != null && nestedScrollView.getVisibility() == View.GONE) {
                nestedScrollView.setVisibility(View.VISIBLE);
            }
        });
        propertyViewModel.getCurrentPoisNextProperty()
                .observe(getViewLifecycleOwner(), this::displayPoi);
    }

    private void displayPoi(List<PoiNextProperty> poiNextProperty) {
        //propertyViewModel.setCurrentPoisNextProperty(poiNextProperty);
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
        propertyViewModel.getCurrentProperty().observe(getViewLifecycleOwner(), this::getPictureAddress);
    }

    private void getPictureAddress(Property property) {
        if (property.getLatitude() == null || property.getLatitude() == 0 && Utils.isInternetAvailable(context)) {
            String address = String.format("%s %s, %s, %s, %s", property.getStreetNumber(),
                    property.getStreetName(), property.getCity(), property.getCountry(),
                    property.getZip());
            Executors.newSingleThreadExecutor().execute(() -> {
                LatLng latLng = Utils.getLocationFromAddress(context, address);
                if (latLng != null) {
                    property.setLatitude(latLng.latitude);
                    property.setLongitude(latLng.longitude);
                    propertyViewModel.updateProperty(property);
                }
            });
            return;
        }
        LatLng latLng = new LatLng(property.getLatitude(), property.getLongitude());
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}