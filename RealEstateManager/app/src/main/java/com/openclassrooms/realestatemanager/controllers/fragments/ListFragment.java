package com.openclassrooms.realestatemanager.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.activities.EditActivity;
import com.openclassrooms.realestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.realestatemanager.model.Filter;
import com.openclassrooms.realestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends Fragment implements FilterDialogFragment.FilterListener {

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
        propertyViewModel.getProperties().observe(getViewLifecycleOwner(), adapter::setProperties);
        propertyViewModel.getPhotos().observe(getViewLifecycleOwner(), adapter::setPhotoList);
        propertyViewModel.getPoisNextProperties().observe(getViewLifecycleOwner(), adapter::setPoisNextProperty);
    }

    @Optional
    @OnClick(R.id.fragment_list_fab)
    void startEditActivity() {
        ((EditActivity.startEditActivityListener) context).createProperty();
    }

    @Override
    public void onApplyFilter(Filter filter) {
        adapter.setFilter(filter);
    }

    @Override
    public void filterError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}