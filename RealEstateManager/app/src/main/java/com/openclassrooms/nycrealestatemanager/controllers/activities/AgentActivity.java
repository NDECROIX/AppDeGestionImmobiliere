package com.openclassrooms.nycrealestatemanager.controllers.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.openclassrooms.nycrealestatemanager.R;
import com.openclassrooms.nycrealestatemanager.base.BaseActivity;
import com.openclassrooms.nycrealestatemanager.controllers.fragments.AgentDialogFragment;
import com.openclassrooms.nycrealestatemanager.database.AppDatabase;
import com.openclassrooms.nycrealestatemanager.database.updates.UpdateData;
import com.openclassrooms.nycrealestatemanager.injections.Injection;
import com.openclassrooms.nycrealestatemanager.injections.ViewModelFactory;
import com.openclassrooms.nycrealestatemanager.model.Agent;
import com.openclassrooms.nycrealestatemanager.utils.Utils;
import com.openclassrooms.nycrealestatemanager.view.adapters.AgentActivityRecyclerViewAdapter;
import com.openclassrooms.nycrealestatemanager.viewmodel.PropertyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class AgentActivity extends BaseActivity implements AgentActivityRecyclerViewAdapter.OnClickAgentListener,
        AgentDialogFragment.CreateAgentListener, UpdateData.UpdateDataListener {

    // Views
    @BindView(R.id.activity_agent_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.activity_agent_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.agent_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_agent_swipe_refresh_view)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.agent_activity_progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.activity_agent_tv_empty)
    TextView noAgantsText;

    // Agent RecyclerView adapter
    private AgentActivityRecyclerViewAdapter adapter;

    // View model
    private PropertyViewModel propertyViewModel;

    // List of agent from the local database
    private List<Agent> agents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        ButterKnife.bind(this);
        configToolbar();
        configProgressBar();
        configViewModel();
        configRecyclerView();
    }

    /**
     * Configure the toolbar
     */
    private void configToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Agents");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * Configure the progress bar that starts during data synchronization
     */
    private void configProgressBar() {
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(
                this.getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    /**
     * Configure the property view model
     */
    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(AppDatabase.getInstance(this));
        this.propertyViewModel = new ViewModelProvider(this, viewModelFactory).get(PropertyViewModel.class);
        configObserver();
    }

    /**
     * Create an observer on the Agents
     */
    private void configObserver() {
        propertyViewModel.getAgents().observe(this, agents -> {
            this.agents = agents;
            if (this.agents == null || !this.agents.isEmpty()) {
                noAgantsText.setVisibility(View.GONE);
            }
            adapter.setAgents(agents);
        });
    }

    /**
     * Configure the recycler view
     */
    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new AgentActivityRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        configureSwipeRefreshLayout();
    }

    /**
     * Reload data from the firebase database when swipe the recycler view
     */
    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            synchronizeData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    /**
     * Synchronize data with firebase database
     */
    @AfterPermissionGranted(RC_READ_WRITE)
    public void synchronizeData() {
        if (progressBar != null && progressBar.getVisibility() != View.GONE) {
            return;
        }
        if (!Utils.isInternetAvailable(this)) {
            showToastMessage(this, "No internet");
            return;
        }
        if (EasyPermissions.hasPermissions(this, WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE)) {
            UpdateData updateData = new UpdateData(this, propertyViewModel, this, this);
            customToast(this, "Synchronization start");
            updateData.startSynchronisation();
        } else {
            getPermission();
        }
    }

    /**
     * Get the read/write external storage permission.
     */
    private void getPermission() {
        if (ContextCompat.checkSelfPermission(this,
                WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE},
                    RC_READ_WRITE);
        }
    }

    /**
     * Open a dialog box with the agent data when the user clicks on an agent item.
     *
     * @param agent Agent to update
     */
    @Override
    public void onClickAgent(Agent agent) {
        startAgentDialogFragment(agent);
    }

    /**
     * Open a dialog box to create an agent
     */
    @OnClick(R.id.activity_agent_fab)
    public void addAgent() {
        startAgentDialogFragment(null);
    }

    /**
     * Call the fragment that display a dialog box to create  or update an agent.
     *
     * @param agent Agent to update, can be null if create
     */
    private void startAgentDialogFragment(Agent agent) {
        AgentDialogFragment agentDialogFragment = new AgentDialogFragment(this, this, this.getLayoutInflater(), agent);
        agentDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

    /**
     * Add the agent in the local database
     *
     * @param agent Agent to add in the database
     */
    @Override
    public void createAgent(Agent agent) {
        if (agents == null || !agents.contains(agent)) {
            propertyViewModel.insertAgent(agent);
            Snackbar.make(coordinatorLayout,
                    String.format("Agent %s %s has been added.", agent.getLastName(), agent.getFirstName()),
                    Snackbar.LENGTH_SHORT).show();
        } else {
            customToast(this, String.format("Agent %s %s already exist.", agent.getLastName(), agent.getFirstName()));
        }
    }

    /**
     * Update an agent in the local database
     *
     * @param agent Agent to update
     */
    @Override
    public void updateAgent(Agent agent) {
        propertyViewModel.updateAgent(agent);
        Snackbar.make(coordinatorLayout,
                String.format("Agent %s %s has been updated.", agent.getLastName(), agent.getFirstName()),
                Snackbar.LENGTH_SHORT).show();
    }

    /**
     * Call when the synchronization with Firebase database is complete
     */
    @Override
    public void synchronisationComplete() {
        customToast(this, "Synchronization complete");
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void notification(String notification) {
        showToastMessage(this, notification);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}