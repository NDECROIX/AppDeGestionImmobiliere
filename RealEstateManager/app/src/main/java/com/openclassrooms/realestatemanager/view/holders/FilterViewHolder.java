package com.openclassrooms.realestatemanager.view.holders;

import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.fragments.FilterDialogFragment;
import com.openclassrooms.realestatemanager.model.Filter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterViewHolder {

    @BindView(R.id.fragment_filter_dialog_radio_grp_type_left)
    public RadioGroup radioGroupTypeLeft;
    @BindView(R.id.fragment_filter_dialog_radio_grp_type_right)
    public RadioGroup radioGroupTypeRight;
    @BindView(R.id.fragment_filter_dialog_radio_grp_borough_left)
    public RadioGroup radioGroupBoroughLeft;
    @BindView(R.id.fragment_filter_dialog_radio_grp_borough_right)
    public RadioGroup radioGroupBoroughRight;
    @BindView(R.id.fragment_filter_dialog_check_box_grp_poi_left)
    public LinearLayout checkBoxGrpLeft;
    @BindView(R.id.fragment_filter_dialog_check_box_grp_poi_right)
    public LinearLayout checkBoxGrpRight;
    @BindView(R.id.fragment_filter_dialog_slider_photos)
    public SeekBar seekBar;
    @BindView(R.id.fragment_filter_dialog_tv_photos_nbr)
    public TextView nbrPhotos;
    @BindView(R.id.fragment_filter_dialog_tie_price_min)
    public TextInputEditText minPrice;
    @BindView(R.id.fragment_filter_dialog_tie_price_max)
    public TextInputEditText maxPrice;
    @BindView(R.id.fragment_filter_dialog_tie_surface_min)
    public TextInputEditText minSurface;
    @BindView(R.id.fragment_filter_dialog_tie_surface_max)
    public TextInputEditText maxSurface;
    @BindView(R.id.fragment_filter_dialog_tie_entry_date_from)
    public TextInputEditText tieEntryDateFrom;
    @BindView(R.id.fragment_filter_dialog_tie_entry_date_to)
    public TextInputEditText tieEntryDateTo;
    @BindView(R.id.fragment_filter_dialog_tie_sale_date_from)
    public TextInputEditText tieSaleDateFrom;
    @BindView(R.id.fragment_filter_dialog_tie_sale_date_to)
    public TextInputEditText tieSaleDateTo;
    @BindView(R.id.fragment_filter_dialog_cb_on_sale)
    public CheckBox checkBoxOnSale;
    @BindView(R.id.fragment_filter_dialog_cb_sold)
    public CheckBox checkBoxSold;

    public Filter filter;

    public FilterViewHolder(View source) {
        ButterKnife.bind(this, source);
    }

    public boolean checkCast(FilterDialogFragment.FilterListener errorCallback) {
        filter = new Filter();
        if (minPrice.getText() != null && !minPrice.getText().toString().isEmpty()) {
            try {
                filter.setMinPrice(Double.parseDouble(minPrice.getText().toString()));
            } catch (NumberFormatException e) {
                errorCallback.filterError("Error in the min price format");
                return false;
            }
        }

        // Check max price
        if (maxPrice.getText() != null && !maxPrice.getText().toString().isEmpty()) {
            try {
                filter.setMaxPrice(Double.parseDouble(maxPrice.getText().toString()));
            } catch (NumberFormatException e) {
                errorCallback.filterError("Error in the max price format");
                return false;
            }
        }

        // Check min surface
        if (minSurface.getText() != null && !minSurface.getText().toString().isEmpty()) {
            try {
                filter.setMinSurface(Double.parseDouble(minSurface.getText().toString()));
            } catch (NumberFormatException e) {
                errorCallback.filterError("Error in the min surface format");
                return false;
            }
        }

        // Check max surface
        if (maxSurface.getText() != null && !maxSurface.getText().toString().isEmpty()) {
            try {
                filter.setMaxSurface(Double.parseDouble(maxSurface.getText().toString()));
            } catch (NumberFormatException e) {
                errorCallback.filterError("Error in the max surface format");
                return false;
            }
        }

        if (checkBoxSold.isChecked()) {
            filter.setStatus(2);
        } else if (checkBoxOnSale.isChecked()) {
            filter.setStatus(1);
        }

        // Check sale idCalendar from
        if (tieSaleDateFrom.getText() != null && !tieSaleDateFrom.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = simpleDateFormat.parse(tieSaleDateFrom.getText().toString());
                filter.setSaleDateFrom(date.getTime());
            } catch (ParseException p) {
                errorCallback.filterError("Error on the sale idCalendar from");
                return false;
            }
        }

        // Check sale idCalendar to
        if (tieSaleDateTo.getText() != null && !tieSaleDateTo.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = simpleDateFormat.parse(tieSaleDateTo.getText().toString());
                filter.setSaleDateTo(date.getTime());
            } catch (ParseException p) {
                errorCallback.filterError("Error on the sale idCalendar to");
                return false;
            }
        }

        // Check entry idCalendar from
        if (tieEntryDateFrom.getText() != null && !tieEntryDateFrom.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = simpleDateFormat.parse(tieEntryDateFrom.getText().toString());
                filter.setEntryDateFrom(date.getTime());
            } catch (ParseException p) {
                errorCallback.filterError("Error on the entry idCalendar from");
                return false;
            }
        }

        // Check entry idCalendar to
        if (tieEntryDateTo.getText() != null && !tieEntryDateTo.getText().toString().isEmpty()) {
            try {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                Date date = simpleDateFormat.parse(tieEntryDateTo.getText().toString());
                filter.setEntryDateTo(date.getTime());
            } catch (ParseException p) {
                errorCallback.filterError("Error on the entry idCalendar from");
                return false;
            }
        }
        return true;
    }
}
