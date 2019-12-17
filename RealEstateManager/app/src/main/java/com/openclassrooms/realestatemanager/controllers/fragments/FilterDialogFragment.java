package com.openclassrooms.realestatemanager.controllers.fragments;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.Type;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener {

    public interface FilterListener {
        void onApplyFilter(Filter filter);
        void filterError(String message);
    }

    private FilterListener callback;

    @BindView(R.id.fragment_filter_dialog_radio_grp_type_left)
    RadioGroup radioGroupTypeLeft;
    @BindView(R.id.fragment_filter_dialog_radio_grp_type_right)
    RadioGroup radioGroupTypeRight;
    @BindView(R.id.fragment_filter_dialog_radio_grp_borough_left)
    RadioGroup radioGroupBoroughLeft;
    @BindView(R.id.fragment_filter_dialog_radio_grp_borough_right)
    RadioGroup radioGroupBoroughRight;
    @BindView(R.id.fragment_filter_dialog_check_box_grp_poi_left)
    LinearLayout checkBoxGrpLeft;
    @BindView(R.id.fragment_filter_dialog_check_box_grp_poi_right)
    LinearLayout checkBoxGrpRight;
    @BindView(R.id.fragment_filter_dialog_slider_photos)
    SeekBar seekBar;
    @BindView(R.id.fragment_filter_dialog_tv_photos_nbr)
    TextView nbrPhotos;
    @BindView(R.id.fragment_filter_dialog_tie_price_min)
    TextInputEditText minPrice;
    @BindView(R.id.fragment_filter_dialog_tie_price_max)
    TextInputEditText maxPrice;
    @BindView(R.id.fragment_filter_dialog_tie_surface_min)
    TextInputEditText minSurface;
    @BindView(R.id.fragment_filter_dialog_tie_surface_max)
    TextInputEditText maxSurface;


    private Context context;
    private LayoutInflater layoutInflater;

    private PropertyViewModel propertyViewModel;

    private String type;
    private String borough;
    private List<Poi> pois;
    private int photos;
    private Filter filter;

    public FilterDialogFragment(Context context, FilterListener callback, LayoutInflater layoutInflater) {
        this.photos = 1;
        filter = new Filter();
        this.context = context;
        this.callback = callback;
        this.layoutInflater = layoutInflater;
        this.propertyViewModel = ViewModelProviders.of((MainActivity) context).get(PropertyViewModel.class);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = layoutInflater.inflate(R.layout.fragment_filter_dialog, null);
        ButterKnife.bind(this, view);
        configObserver();
        configViews();
        builder.setView(view)
                .setPositiveButton("Apply", (dialog, id) -> {
                    // Overwrite in on show listener
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener( l ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    if (checkCast()){
                        callback.onApplyFilter(createFilter());
                        dialog.dismiss();
                    }}));
        return dialog;
    }

    private void configViews() {
        seekBar.setOnSeekBarChangeListener(this);
    }

    private void configObserver() {
        displayBoroughRadioBtn();
        propertyViewModel.getTypes().observe(this, this::updateTypeRadioBtn);
        propertyViewModel.getAllPoi().observe(this, this::updatePoiCheckBox);
    }

    /**
     * Display all types as a radio button in the group radio and
     * switch between left and right
     *
     * @param types all types from the database
     */
    private void updateTypeRadioBtn(List<Type> types) {
        boolean left = true;
        for (Type type : types) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(type.getName());
            radioButton.setOnClickListener(l -> {
                if (l.getParent() == radioGroupTypeRight) {
                    radioGroupTypeLeft.clearCheck();
                } else {
                    radioGroupTypeRight.clearCheck();
                }
                this.type = type.getName();
            });
            if (left) {
                radioGroupTypeLeft.addView(radioButton);
            } else {
                radioGroupTypeRight.addView(radioButton);
            }
            left = !left;
        }
    }

    /**
     * Display all borough as a radio button in the group radio and
     * switch between left and right
     */
    private void displayBoroughRadioBtn() {
        boolean left = true;
        for (String borough : Property.getBoroughs()) {
            RadioButton radioButton = new RadioButton(context);
            radioButton.setText(borough);
            radioButton.setOnClickListener(l -> {
                if (l.getParent() == radioGroupBoroughRight) {
                    radioGroupBoroughLeft.clearCheck();
                } else {
                    radioGroupBoroughRight.clearCheck();
                }
                this.borough = borough;
            });
            if (left) {
                radioGroupBoroughLeft.addView(radioButton);
            } else {
                radioGroupBoroughRight.addView(radioButton);
            }
            left = !left;
        }
    }

    /**
     * Display all poi as check box and switch between left and right
     *
     * @param POIs All POIs from the database
     */
    private void updatePoiCheckBox(List<Poi> POIs) {
        boolean left = true;
        pois = new ArrayList<>();
        for (Poi poi : POIs) {
            CheckBox checkBox = new CheckBox(context);
            checkBox.setText(poi.getName());
            checkBox.setOnCheckedChangeListener((btn, checked) -> {
                if (checked) {
                    pois.add(poi);
                } else {
                    pois.remove(poi);
                }
            });
            if (left) {
                checkBoxGrpLeft.addView(checkBox);
            } else {
                checkBoxGrpRight.addView(checkBox);
            }
            left = !left;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int number, boolean b) {
        nbrPhotos.setText(String.valueOf(number + 1));
        photos = number + 1;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    private boolean checkCast(){
        if (minPrice.getText() != null && !minPrice.getText().toString().isEmpty()) {
            try {
                filter.minPrice = Double.parseDouble(minPrice.getText().toString());
            } catch (NumberFormatException e) {
                callback.filterError("Error in the min price format");
                return false;
            }
        }

        // Check max price
        if (maxPrice.getText() != null && !maxPrice.getText().toString().isEmpty()) {
            try {
                filter.maxPrice = Double.parseDouble(maxPrice.getText().toString());
            } catch (NumberFormatException e) {
                callback.filterError("Error in the max price format");
                return false;
            }
        }

        // Check min surface
        if (minSurface.getText() != null && !minSurface.getText().toString().isEmpty()) {
            try {
                filter.minSurface = Double.parseDouble(minSurface.getText().toString());
            } catch (NumberFormatException e) {
                callback.filterError("Error in the min surface format");
                return false;
            }
        }

        // Check max surface
        if (maxSurface.getText() != null && !maxSurface.getText().toString().isEmpty()) {
            try {
                filter.maxSurface = Double.parseDouble(maxSurface.getText().toString());
            } catch (NumberFormatException e) {
                callback.filterError("Error in the max surface format");
                return false;
            }
        }
        return true;
    }

    private Filter createFilter(){
        filter.type = this.type;
        filter.nbrPhotos = this.photos;
        filter.borough = this.borough;
        filter.pois.addAll(this.pois);
        return filter;
    }

    public class Filter {
        public String type;
        public Double minPrice;
        public Double maxPrice;
        public Double minSurface;
        public Double maxSurface;
        public int nbrPhotos;
        public String borough;
        public List<Poi> pois;

        public Filter() {
            pois = new ArrayList<>();
        }
    }
}