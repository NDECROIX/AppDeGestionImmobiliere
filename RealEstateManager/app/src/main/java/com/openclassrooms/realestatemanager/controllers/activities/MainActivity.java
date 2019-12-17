package com.openclassrooms.realestatemanager.controllers.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.navigation.NavigationView;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.DetailFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.FilterDialogFragment;
import com.openclassrooms.realestatemanager.controllers.fragments.ListFragment;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Filter;
import com.openclassrooms.realestatemanager.model.Photo;
import com.openclassrooms.realestatemanager.model.Property;
import com.openclassrooms.realestatemanager.view.adapters.ListPropertyRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements ListPropertyRecyclerViewAdapter.PropertyOnClickListener,
        EditActivity.startEditActivityListener {

    @BindView(R.id.main_activity_drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.main_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_main_navigation_view)
    NavigationView navigationView;

    private PropertyViewModel propertyViewModel;

    //Fragments
    private Fragment activeFragment = new Fragment();
    private final ListFragment listFragment = new ListFragment();
    private final DetailFragment detailFragment = new DetailFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        configViewModel();
        activeFragment = listFragment;
        navigationView.setNavigationItemSelectedListener(this::onOptionsItemSelected);
        this.getSupportFragmentManager().beginTransaction()
                .add(R.id.activity_main_frame_layout, activeFragment)
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
            getSupportActionBar().show();
            getSupportActionBar().setTitle(R.string.app_name);
        }
    }

    @Override
    public void onBackPressed() {
        if (activeFragment == detailFragment) {
            activeFragment = listFragment;
            this.getSupportFragmentManager().beginTransaction()
                    .replace(R.id.activity_main_frame_layout, activeFragment)
                    .commitNow();
            configToolbar();
            configDrawerLayout();
        } else {
            super.onBackPressed();
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
                showToastMessage(this, "Add a real estate.");
                break;
            case R.id.menu_activity_main_edit:
                showToastMessage(this, "Edit a real estate.");
                break;
            case R.id.menu_fragment_detail_edit:
                editProperty();
                break;
            case R.id.menu_activity_main_search:
                startFilterDialogFragment();
                break;
            case R.id.activity_main_drawer_agent:
                startActivity(new Intent(this, AgentActivity.class));
                break;
            case android.R.id.home:
                if (activeFragment == detailFragment) onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startFilterDialogFragment() {
        if (activeFragment != listFragment){
            return;
        }
        FilterDialogFragment agentDialogFragment = new FilterDialogFragment(this, (ListFragment) activeFragment, this.getLayoutInflater());
        agentDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    private void editProperty() {
        startActivity(EditActivity.newIntent(this, propertyViewModel.getCurrentProperty(),
                propertyViewModel.getCurrentPoisNextProperty(), propertyViewModel.getCurrentPhotosProperty()));
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
        activeFragment = detailFragment;
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.activity_main_frame_layout, activeFragment)
                .commitNow();
    }

    @Override
    public void createProperty() {
        startActivity(EditActivity.newIntent(this, null, null, null));
    }

}