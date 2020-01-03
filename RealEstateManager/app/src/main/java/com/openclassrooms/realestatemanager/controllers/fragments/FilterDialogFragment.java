package com.openclassrooms.realestatemanager.controllers.fragments;


import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Filter;
import com.openclassrooms.realestatemanager.model.Poi;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.model.Type;
import com.openclassrooms.realestatemanager.view.holders.FilterViewHolder;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Filter fragment to filter properties
 */
public class FilterDialogFragment extends DialogFragment implements SeekBar.OnSeekBarChangeListener,
        DatePickerDialog.OnDateSetListener {

    /**
     * Notified all errors and passed the filter
     */
    public interface FilterListener {
        void onApplyFilter(Filter filter);

        void filterError(String message);
    }

    private FilterListener callback;

    // Views
    private FilterViewHolder viewHolder;

    private Context context;
    private LayoutInflater layoutInflater;

    // View model
    private PropertyViewModel propertyViewModel;

    private String type;
    private String borough;
    private List<Poi> pois;
    private int photos;
    private Filter filter;
    private FragmentManager fragmentManager;
    private int idCalendar;

    public FilterDialogFragment(Context context, FilterListener callback, LayoutInflater layoutInflater, FragmentManager fragmentManager) {
        this.photos = 1;
        filter = new Filter();
        this.context = context;
        this.callback = callback;
        this.layoutInflater = layoutInflater;
        this.fragmentManager = fragmentManager;
        this.propertyViewModel = ViewModelProviders.of((MainActivity) context).get(PropertyViewModel.class);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = layoutInflater.inflate(R.layout.fragment_filter_dialog, null);
        viewHolder = new FilterViewHolder(view);
        ButterKnife.bind(this, view);
        configObserver();
        configViews();
        builder.setView(view)
                .setPositiveButton("Apply", (dialog, id) -> {
                    // Overwrite in on show listener
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(l ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    if (viewHolder.checkCast(callback)) {
                        filter = viewHolder.filter;
                        callback.onApplyFilter(createFilter());
                        dialog.dismiss();
                    }
                }));
        return dialog;
    }

    private void configViews() {
        viewHolder.seekBar.setOnSeekBarChangeListener(this);
        viewHolder.checkBoxOnSale.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) viewHolder.checkBoxSold.setChecked(false);
        });
        viewHolder.checkBoxSold.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) viewHolder.checkBoxOnSale.setChecked(false);
        });
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
                if (l.getParent() == viewHolder.radioGroupTypeRight) {
                    viewHolder.radioGroupTypeLeft.clearCheck();
                } else {
                    viewHolder.radioGroupTypeRight.clearCheck();
                }
                this.type = type.getName();
            });
            if (left) {
                viewHolder.radioGroupTypeLeft.addView(radioButton);
            } else {
                viewHolder.radioGroupTypeRight.addView(radioButton);
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
                if (l.getParent() == viewHolder.radioGroupBoroughRight) {
                    viewHolder.radioGroupBoroughLeft.clearCheck();
                } else {
                    viewHolder.radioGroupBoroughRight.clearCheck();
                }
                this.borough = borough;
            });
            if (left) {
                viewHolder.radioGroupBoroughLeft.addView(radioButton);
            } else {
                viewHolder.radioGroupBoroughRight.addView(radioButton);
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
                viewHolder.checkBoxGrpLeft.addView(checkBox);
            } else {
                viewHolder.checkBoxGrpRight.addView(checkBox);
            }
            left = !left;
        }
    }

    // Handle click on calendar
    @OnClick(R.id.fragment_filter_dialog_ib_entry_date_from | R.id.fragment_filter_dialog_ib_entry_date_to
            | R.id.fragment_filter_dialog_ib_sale_date_from | R.id.fragment_filter_dialog_ib_sale_date_to)
    void clickOnCalendar(View v) {
        switch (v.getId()) {
            case R.id.fragment_filter_dialog_ib_entry_date_from:
                idCalendar = R.id.fragment_filter_dialog_ib_entry_date_from;
                break;
            case R.id.fragment_filter_dialog_ib_entry_date_to:
                idCalendar = R.id.fragment_filter_dialog_ib_entry_date_to;
                break;
            case R.id.fragment_filter_dialog_ib_sale_date_from:
                idCalendar = R.id.fragment_filter_dialog_ib_sale_date_from;
                break;
            case R.id.fragment_filter_dialog_ib_sale_date_to:
                idCalendar = R.id.fragment_filter_dialog_ib_sale_date_to;
                break;
            default:
                return;
        }
        DatePickerFragment datePickerFragment = new DatePickerFragment(this);
        datePickerFragment.show(fragmentManager, "datePicker");
    }

    // result from the calendar
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        switch (idCalendar) {
            case R.id.fragment_filter_dialog_ib_entry_date_from:
                viewHolder.tieEntryDateFrom.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
                break;
            case R.id.fragment_filter_dialog_ib_entry_date_to:
                viewHolder.tieEntryDateTo.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
                break;
            case R.id.fragment_filter_dialog_ib_sale_date_from:
                viewHolder.tieSaleDateFrom.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
                break;
            case R.id.fragment_filter_dialog_ib_sale_date_to:
                viewHolder.tieSaleDateTo.setText(String.format("%s/%s/%s", month + 1, dayOfMonth, year));
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int number, boolean b) {
        viewHolder.nbrPhotos.setText(String.valueOf(number + 1));
        photos = number + 1;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    // End of the filter creation
    private Filter createFilter() {
        filter.setType(this.type);
        filter.setNbrPhotos(this.photos);
        filter.setBorough(this.borough);
        filter.setPois(this.pois);
        return filter;
    }
}