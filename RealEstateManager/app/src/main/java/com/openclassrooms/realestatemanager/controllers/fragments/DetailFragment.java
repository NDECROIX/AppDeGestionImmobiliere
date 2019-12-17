package com.openclassrooms.realestatemanager.controllers.fragments;


import android.content.Context;
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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.PoiNextProperty;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.view.adapters.DetailFragmentPhotoRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements DetailFragmentPhotoRecyclerViewAdapter.OnClickPhotoListener {

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

    private Context context;
    private PropertyViewModel propertyViewModel;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyViewModel = ViewModelProviders.of((MainActivity) context).get(PropertyViewModel.class);
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
        displayPropertyData();
        getPropertyPoiFromDatabase();
        return view;
    }

    private void configToolbar() {
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
        menu.findItem(R.id.menu_activity_main_search).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_detail_toolbar_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        DetailFragmentPhotoRecyclerViewAdapter adapter = new DetailFragmentPhotoRecyclerViewAdapter(this, propertyViewModel.getCurrentPhotosProperty());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickPhoto(Photo photo) {

    }

    private void displayPropertyData() {
        Property property = propertyViewModel.getCurrentProperty();
        type.setText(property.getType());
        description.setText(property.getDescription());
        price.setText(String.format("Price : %s $", new DecimalFormat("#").format(property.getPrice())));
        surface.append(String.valueOf(property.getSurface()));
        room.append(String.valueOf(property.getRooms()));
        bathroom.append(String.valueOf(property.getBathrooms()));
        bedroom.append(String.valueOf(property.getBedrooms()));
        String supplement = property.getAddressSupplement();
        supplement = (supplement == null) ? "" : "\n" + supplement;
        address.setText(String.format(Locale.getDefault(), "%d %s %s\n%s\n%d\n%s",
                property.getStreetNumber(), property.getStreetName(), supplement,
                property.getCity(), property.getZip(), property.getCountry()));
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(property.getEntryDate());
        date.setText(String.format(Locale.getDefault(), "%d/%d/%d",
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        if (property.getAgentID() != null && !property.getAgentID().isEmpty()) {
            agent.setText(property.getAgentID());
            propertyViewModel.getAgent(property.getAgentID()).observe(getViewLifecycleOwner(), agent ->
                    this.agent.setText(String.format("%s %s", agent.getFirstName(), agent.getLastName())));
        }
    }

    private void getPropertyPoiFromDatabase() {
        propertyViewModel.getPoisNextProperty(propertyViewModel.getCurrentProperty().getId())
                .observe(getViewLifecycleOwner(), this::displayPoi);
    }

    private void displayPoi(List<PoiNextProperty> poiNextProperty) {
        propertyViewModel.setCurrentPoisNextProperty(poiNextProperty);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
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
}
