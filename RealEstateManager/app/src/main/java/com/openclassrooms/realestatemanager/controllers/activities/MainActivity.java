package com.openclassrooms.realestatemanager.controllers.activities;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.ListFragment;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements ListPropertyRecyclerViewAdapter.PropertyOnClickListener,
EditActivity.startEditActivityListener{

    @BindView(R.id.main_activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_activity_toolbar)
    Toolbar toolbar;

    private PropertyViewModel propertyViewModel;

    //Fragments
    private final ListFragment listFragment = new ListFragment();
    private final DetailFragment detailFragment = new DetailFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configViewModel();
        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_frame_layout, listFragment)
                .commitNow();
        configToolbar();
        configDrawerLayout();
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void configToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main_toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_activity_main_add:
                showToastMessage("Add a real estate.");
                break;
            case R.id.menu_activity_main_edit:
                showToastMessage("Edit a real estate.");
                break;
            case R.id.menu_activity_main_search:
                showToastMessage("Search for a real estate.");

        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configures the Drawer layout
     */
    private void configDrawerLayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onClickListener(Property property, List<Photo> photos) {
        propertyViewModel.setCurrentProperty(property, photos);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_frame_layout, detailFragment)
                .commitNow();
    }

    @Override
    public void createProperty() {
        startActivity(EditActivity.newIntent(this));
    }
}