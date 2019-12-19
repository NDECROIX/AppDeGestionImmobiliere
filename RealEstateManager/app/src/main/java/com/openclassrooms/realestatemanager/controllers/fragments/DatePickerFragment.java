package com.openclassrooms.realestatemanager.controllers.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.openclassrooms.realestatemanager.R;

import java.util.Calendar;

/**
 * Create DatePickerFragment
 */
public class DatePickerFragment extends DialogFragment {

    private final DatePickerDialog.OnDateSetListener onDateSetListener;

    public DatePickerFragment(Context context) {
        this.onDateSetListener = (DatePickerDialog.OnDateSetListener) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(), R.style.dateTimePicker,
                onDateSetListener, year, month, day);
    }
}
