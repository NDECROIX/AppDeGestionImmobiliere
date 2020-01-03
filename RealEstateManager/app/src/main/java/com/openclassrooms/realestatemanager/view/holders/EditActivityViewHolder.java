package com.openclassrooms.realestatemanager.view.holders;

import android.app.Activity;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.adapters.EditActivityPhotoRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Get and handle all edit activity views
 */
public class EditActivityViewHolder {

    public interface CheckFieldsListener {
        void error(String message);

        void property(Property property);
    }

    @BindView(R.id.activity_edit_toolbar)
    public Toolbar toolbar;
    @BindView(R.id.activity_edit_radio_grp_type_left)
    public RadioGroup radioGroupTypeLeft;
    @BindView(R.id.activity_edit_radio_grp_type_right)
    public RadioGroup radioGroupTypeRight;
    @BindView(R.id.activity_edit_radio_grp_borough_left)
    public RadioGroup radioGroupBoroughLeft;
    @BindView(R.id.activity_edit_radio_grp_borough_right)
    public RadioGroup radioGroupBoroughRight;
    @BindView(R.id.activity_edit_check_box_grp_poi_left)
    public LinearLayout checkBoxGrpLeft;
    @BindView(R.id.activity_edit_check_box_grp_poi_right)
    public LinearLayout checkBoxGrpRight;
    @BindView(R.id.activity_edit_recycler_view)
    public RecyclerView recyclerView;
    @BindView(R.id.activity_edit_tie_date)
    public TextInputEditText tieEntryDate;
    @BindView(R.id.activity_edit_tie_date_sell)
    public TextInputEditText tieSaleDate;
    @BindView(R.id.activity_edit_tie_price)
    public TextInputEditText tiePrice;
    @BindView(R.id.activity_edit_tie_surface)
    public TextInputEditText tieSurface;
    @BindView(R.id.activity_edit_tie_rooms)
    public TextInputEditText tieRooms;
    @BindView(R.id.activity_edit_tie_bathroom)
    public TextInputEditText tieBathroom;
    @BindView(R.id.activity_edit_tie_bedroom)
    public TextInputEditText tieBedroom;
    @BindView(R.id.activity_edit_tie_description)
    public TextInputEditText tieDescription;
    @BindView(R.id.activity_edit_tie_street_number)
    public TextInputEditText tieStreetNumber;
    @BindView(R.id.activity_edit_tie_street_name)
    public TextInputEditText tieStreetName;
    @BindView(R.id.activity_edit_tie_street_supplement)
    public TextInputEditText tieStreetSupplement;
    @BindView(R.id.activity_edit_tie_city)
    public TextInputEditText tieCity;
    @BindView(R.id.activity_edit_tie_zip)
    public TextInputEditText tieZipCode;
    @BindView(R.id.activity_edit_tie_country)
    public TextInputEditText tieCountry;
    @BindView(R.id.activity_edit_tv_agent)
    public TextView tvAgent;

    public Property property;
    private Context context;

    public EditActivityViewHolder(Activity source) {
        property = new Property();
        this.context = source;
        ButterKnife.bind(this, source);
    }

    public void completeFieldsWithProperty(Property property, PropertyViewModel propertyViewModel, LifecycleOwner lifecycleOwner) {
        DecimalFormat df = new DecimalFormat("#.##");
        tiePrice.setText(df.format(property.getPrice()));
        tieSurface.setText(String.valueOf(property.getSurface()));
        tieRooms.setText(String.valueOf(property.getRooms()));
        tieBedroom.setText(String.valueOf(property.getBedrooms()));
        tieBathroom.setText(String.valueOf(property.getBathrooms()));
        tieDescription.setText(property.getDescription());
        tieStreetNumber.setText(String.valueOf(property.getStreetNumber()));
        tieStreetName.setText(String.valueOf(property.getStreetName()));
        if (property.getAddressSupplement() != null)
            tieStreetSupplement.setText(property.getAddressSupplement());
        tieCity.setText(property.getCity());
        tieZipCode.setText(String.valueOf(property.getZip()));
        tieCountry.setText(property.getCountry());
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.setTimeInMillis(property.getEntryDate());
        tieEntryDate.setText(String.format(Locale.getDefault(), "%d/%d/%d",
                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        if (property.isSold()) {
            calendar = new GregorianCalendar();
            calendar.setTimeInMillis(property.getSaleDate());
            tieSaleDate.setText(String.format(Locale.getDefault(), "%d/%d/%d",
                    calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.YEAR)));
        }
        if (property.getAgentID() != null && !property.getAgentID().isEmpty())
            propertyViewModel.getAgent(property.getAgentID()).observe(lifecycleOwner, agent ->
                    tvAgent.setText(String.format("%s %s", agent.getFirstName(), agent.getLastName())));
    }

    public boolean champNotEmpty(CheckFieldsListener callback, EditActivityPhotoRecyclerViewAdapter adapter, Agent agent, String type, String borough) {
        // Check than at least one type is selected and if it does not exist in the database, add it.
        if (type == null) {
            callback.error("Please add a type");
            return false;
        } else {
            property.setType(type);
        }

        // Check if the price is correct
        if (tiePrice.getText() == null || tiePrice.getText().toString().isEmpty()) {
            callback.error("Please add a price");
            return false;
        }
        try {
            Double price = Double.parseDouble(tiePrice.getText().toString());
            property.setPrice(price);
        } catch (NumberFormatException e) {
            callback.error("Error in the price format");
            return false;
        }

        // Check if the surface is correct
        if (tieSurface.getText() == null || tieSurface.getText().toString().isEmpty()) {
            callback.error("Please add a surface");
            return false;
        }
        try {
            Double surface = Double.parseDouble(tieSurface.getText().toString());
            property.setSurface(surface);
        } catch (NumberFormatException e) {
            callback.error("Error on the surface");
            return false;
        }

        // Check if the number of rooms is correct
        if (tieRooms.getText() == null || tieRooms.getText().toString().isEmpty()) {
            callback.error("Please add a number of rooms");
            return false;
        }
        try {
            int rooms = Integer.parseInt(tieRooms.getText().toString());
            property.setRooms(rooms);
        } catch (NumberFormatException e) {
            callback.error("Error on the number of rooms");
            return false;
        }

        // Check if the number of bathrooms is correct
        if (tieBathroom.getText() != null && !tieBathroom.getText().toString().isEmpty()) {
            try {
                int bathrooms = Integer.parseInt(tieBathroom.getText().toString());
                property.setBathrooms(bathrooms);
            } catch (NumberFormatException e) {
                callback.error("Error on the number of bathrooms");
                return false;
            }
        }

        // Check if the number of bedrooms is correct
        if (tieBedroom.getText() != null && !tieBedroom.getText().toString().isEmpty()) {
            try {
                int bedrooms = Integer.parseInt(tieBedroom.getText().toString());
                property.setBedrooms(bedrooms);
            } catch (NumberFormatException e) {
                callback.error("Error on the number of bedrooms");
                return false;
            }
        }

        // Check if the number of bedrooms and bathrooms is less than the number of rooms
        if (property.getBathrooms() + property.getBedrooms() >= property.getRooms()) {
            callback.error("Number of room less than bedrooms + bathrooms");
            return false;
        }


        // Check description
        if (tieDescription.getText() != null && !tieDescription.getText().toString().isEmpty()) {
            property.setDescription(tieDescription.getText().toString());
        }

        // Check if we have at least one photo
        if (adapter.getItemCount() == 0) {
            callback.error("You need at least one photo");
            return false;
        }

        // Check street number
        if (tieStreetNumber.getText() == null || tieStreetNumber.getText().toString().isEmpty()) {
            callback.error("Please add a street number");
            return false;
        }
        try {
            int streetNumber = Integer.parseInt(tieStreetNumber.getText().toString());
            property.setStreetNumber(streetNumber);
        } catch (NumberFormatException e) {
            callback.error("Error on the street number");
            return false;
        }

        // Check the street name
        if (tieStreetName.getText() == null || tieStreetName.getText().toString().isEmpty()) {
            callback.error("Please add a street name");
            return false;
        } else {
            property.setStreetName(tieStreetName.getText().toString());
        }

        // Check the address supplement
        if (tieStreetSupplement.getText() != null && !tieStreetSupplement.getText().toString().isEmpty()) {
            property.setAddressSupplement(tieStreetSupplement.getText().toString());
        }

        // Check the city
        if (tieCity.getText() == null || tieCity.getText().toString().isEmpty()) {
            callback.error("Please add a city");
            return false;
        } else {
            property.setCity(tieCity.getText().toString());
        }

        // Check the Zip code
        if (tieZipCode.getText() == null || tieZipCode.getText().toString().isEmpty()) {
            callback.error("Please add a Zip code");
            return false;
        }
        try {
            int zip = Integer.parseInt(tieZipCode.getText().toString());
            property.setZip(zip);
        } catch (NumberFormatException e) {
            callback.error("Error on the zip code");
            return false;
        }

        // Check the country
        if (tieCountry.getText() == null || tieCountry.getText().toString().isEmpty()) {
            callback.error("Please add a country");
            return false;
        } else {
            property.setCountry(tieCountry.getText().toString());
        }

        // Check the borough
        if (borough == null) {
            callback.error("Please add a borough");
            return false;
        } else {
            property.setBorough(borough);
        }

        // Check entry date
        if (tieEntryDate.getText() == null || tieEntryDate.getText().toString().isEmpty()) {
            callback.error("Please choose a date");
            return false;
        }
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = simpleDateFormat.parse(tieEntryDate.getText().toString());
            property.setEntryDate(date.getTime());
        } catch (ParseException p) {
            callback.error("Error on the date");
            return false;
        }

        // Check sell date
        if (tieSaleDate.getText() != null && !tieSaleDate.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = simpleDateFormat.parse(tieSaleDate.getText().toString());
                property.setSaleDate(date.getTime());
                property.setSold(true);
            } catch (ParseException p) {
                callback.error("Error on the sale date");
                return false;
            }
        } else {
            property.setSold(false);
        }

        // Check date of sale against date of entry
        if (property.getSaleDate() != 0 && property.getEntryDate() > property.getSaleDate()) {
            callback.error("Error between dates");
            return false;
        }

        // Check agent
        if (agent != null) {
            property.setAgentID(agent.getId());
        }

        String address = String.format("%s %s, %s, %s, %s", property.getStreetNumber(),
                property.getStreetName(), property.getCity(), property.getCountry(),
                property.getZip());
        LatLng latLng = Utils.getLocationFromAddress(context, address);
        if (latLng != null) {
            property.setLatitude(latLng.latitude);
            property.setLongitude(latLng.longitude);
            callback.property(property);
        }
        return true;
    }
}