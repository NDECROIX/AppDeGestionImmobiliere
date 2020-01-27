package com.openclassrooms.nycrealestatemanager.controllers.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.controllers.activities.MainActivity;
import com.openclassrooms.nycrealestatemanager.controllers.activities.ManagePropertyActivity;
import com.openclassrooms.nycrealestatemanager.model.Filter;
import com.openclassrooms.nycrealestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.nycrealestatemanager.viewmodel.PropertyViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Optional;

/**
 * Display properties from the local database in a recycler view
 */
public class ListFragment extends Fragment {

    // Views
    @BindView(R.id.fragment_list_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fragment_swipe_refresh_view)
    SwipeRefreshLayout swipeRefreshLayout;

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
        propertyViewModel = new ViewModelProvider((MainActivity) context).get(PropertyViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        ButterKnife.bind(this, view);
        configRecyclerView();
        configureSwipeRefreshLayout();
        return view;
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            ((MainActivity) context).synchronizeData();
            swipeRefreshLayout.setRefreshing(false);
        });
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
        propertyViewModel.getProperties().observe(getViewLifecycleOwner(), properties -> {
            adapter.setProperties(properties);
            adapter.filterProperty();
        });
        propertyViewModel.getPhotos().observe(getViewLifecycleOwner(), adapter::setPhotoList);
        propertyViewModel.getPoisNextProperties().observe(getViewLifecycleOwner(), adapter::setPoisNextProperty);
        propertyViewModel.getCurrentFilter().observe(getViewLifecycleOwner(), adapter::setFilter);
    }

    @Optional
    @OnClick(R.id.fragment_list_fab)
    void startEditActivity() {
        ((ManagePropertyActivity.startEditActivityListener) context).createProperty();
    }

    public Filter getFilter() {
        return adapter.getFilter();
    }
}