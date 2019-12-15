package com.openclassrooms.realestatemanager.controllers.activities;

import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.base.BaseActivity;
import com.openclassrooms.realestatemanager.controllers.fragments.AgentDialogFragment;
import com.openclassrooms.realestatemanager.injections.Injection;
import com.openclassrooms.realestatemanager.injections.ViewModelFactory;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.view.adapters.AgentActivityRecyclerViewAdapter;
import com.openclassrooms.realestatemanager.viewmodels.PropertyViewModel;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AgentActivity extends BaseActivity implements AgentActivityRecyclerViewAdapter.OnClickAgentListener,
        AgentDialogFragment.CreateAgentListener {

    @BindView(R.id.activity_agent_layout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.activity_agent_recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.agent_activity_toolbar)
    Toolbar toolbar;

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
        configViewModel();
        configObserver();
        configRecyclerView();
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
    }

    @Override
    public void onClickAgent(Agent agent) {
        startAgentDialogFragment(agent);
    }

    @OnClick(R.id.activity_agent_fab)
    public void addAgent() {
        startAgentDialogFragment(null);
    }

    private void startAgentDialogFragment(Agent agent){
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
}
