package com.openclassrooms.realestatemanager.controllers.fragments;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.openclassrooms.realestatemanager.R;

import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FilterDialogFragment extends DialogFragment {

    private Context context;
    private LayoutInflater layoutInflater;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = layoutInflater.inflate(R.layout.fragment_filter_dialog, null);
        ButterKnife.bind(this, view);
        builder.setView(view)
                .setPositiveButton("Apply", (dialog, id) -> {
                    // Overwrite in on show listener
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();

        return dialog;
    }
}