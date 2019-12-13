package com.openclassrooms.realestatemanager.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.EditActivity;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment {

    @BindView(R.id.fragment_list_recycler_view)
    RecyclerView recyclerView;

    private Context context;
    private ListPropertyRecyclerViewAdapter adapter;
    private PropertyViewModel propertyViewModel;

    public ListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        propertyViewModel = ViewModelProviders.of((MainActivity) context).get(PropertyViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        configRecyclerView();
        return view;
    }

    /**
     * Configures the recycler view
     */
    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
        adapter = new ListPropertyRecyclerViewAdapter((ListPropertyRecyclerViewAdapter.PropertyOnClickListener) context);
        recyclerView.setAdapter(adapter);
        getDataFromViewModel();
    }

    /**
     * Get data from database
     */
    private void getDataFromViewModel() {
        propertyViewModel.getProperties().observe(this, properties -> {
            adapter.setPropertyList(properties);
        });
        propertyViewModel.getPhotos().observe(this, photos ->
                adapter.setPhotoList(photos));
    }

    @OnClick(R.id.fragment_list_fab)
    public void startEditActivity() {
        ((EditActivity.startEditActivityListener) context).createProperty();
    }
}