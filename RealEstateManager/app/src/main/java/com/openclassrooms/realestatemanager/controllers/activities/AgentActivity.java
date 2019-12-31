package com.openclassrooms.realestatemanager.controllers.activities;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.snackbar.Snackbar;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.AgentDialogFragment;
import com.openclassrooms.realestatemanager.database.updates.UpdateData;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.utils.Utils;
import com.openclassrooms.realestatemanager.view.adapters.AgentActivityRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.openclassrooms.realestatemanager.controllers.activities.MainActivity.RC_READ_WRITE;

public class AgentActivity extends BaseActivity implements AgentActivityRecyclerViewAdapter.OnClickAgentListener,
        AgentDialogFragment.CreateAgentListener, UpdateData.UpdateDataListener {

    @BindView(R.id.activity_agent_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.activity_agent_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.agent_activity_toolbar)
    Toolbar toolbar;
    @BindView(R.id.activity_agent_swipe_refresh_view)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.main_activity_progress_bar)
    ProgressBar progressBar;

    private AgentActivityRecyclerViewAdapter adapter;
    private PropertyViewModel propertyViewModel;
    private List<Agent> agents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Agents");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        configProgressBar();
        configViewModel();
        configObserver();
        configRecyclerView();
    }

    private void configProgressBar() {
        progressBar.setVisibility(View.GONE);
        progressBar.getIndeterminateDrawable().setColorFilter(
                this.getResources().getColor(R.color.colorAccent), android.graphics.PorterDuff.Mode.SRC_IN);
    }

    private void configObserver() {
        propertyViewModel.getAgents().observe(this, agents -> {
            this.agents = agents;
            adapter.setAgents(agents);
        });
    }

    private void configViewModel() {
        ViewModelFactory viewModelFactory = Injection.provideViewModelFactory(this);
        this.propertyViewModel = ViewModelProviders.of(this, viewModelFactory).get(PropertyViewModel.class);
    }

    private void configRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        adapter = new AgentActivityRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        configureSwipeRefreshLayout();
    }

    private void configureSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            synchronizeData();
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    @AfterPermissionGranted(RC_READ_WRITE)
    public void synchronizeData() {
        if (progressBar != null && progressBar.getVisibility() != View.GONE){
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

    @Override
    public void onClickAgent(Agent agent) {
        startAgentDialogFragment(agent);
    }

    @OnClick(R.id.activity_agent_fab)
    public void addAgent() {
        startAgentDialogFragment(null);
    }

    private void startAgentDialogFragment(Agent agent) {
        AgentDialogFragment agentDialogFragment = new AgentDialogFragment(this, this, this.getLayoutInflater(), agent);
        agentDialogFragment.show(getSupportFragmentManager(), "dialog");
    }

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

    @Override
    public void updateAgent(Agent agent) {
        propertyViewModel.updateAgent(agent);
        Snackbar.make(coordinatorLayout,
                String.format("Agent %s %s has been updated.", agent.getLastName(), agent.getFirstName()),
                Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void synchronisationComplete() {
        customToast(this, "Synchronization complete");
        if (progressBar != null){
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void notification(String notification) {
        showToastMessage(this, notification);
    }

}