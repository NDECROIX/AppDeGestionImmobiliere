package com.openclassrooms.realestatemanager.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.view.adapters.DetailFragmentPhotoRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.view.adapters.EditActivityPhotoRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements DetailFragmentPhotoRecyclerViewAdapter.OnClickPhotoListener {

    @BindView(R.id.fragment_detail_recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private PropertyViewModel propertyViewModel;
    private DetailFragmentPhotoRecyclerViewAdapter adapter;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        propertyViewModel = ViewModelProviders.of((MainActivity)context).get(PropertyViewModel.class);
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
        configRecyclerView();
        return view;
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        adapter = new DetailFragmentPhotoRecyclerViewAdapter(this, propertyViewModel.getPhotosProperty());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onClickPhoto(Photo photo) {

    }
}
