package com.openclassrooms.realestatemanager.view.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.model.Agent;
import com.openclassrooms.realestatemanager.view.holders.AgentViewHolder;

import java.util.ArrayList;
import java.util.List;

public class AgentActivityRecyclerViewAdapter extends RecyclerView.Adapter<AgentViewHolder> {

    public interface OnClickAgentListener{
        void onClickAgent(Agent agent);
    }

    private final OnClickAgentListener callback;
    private List<Agent> agents;

    public AgentActivityRecyclerViewAdapter(OnClickAgentListener callback) {
        this.callback = callback;
        this.agents = new ArrayList<>();
    }

    public void setAgents(List<Agent> agents) {
        this.agents.clear();
        this.agents.addAll(agents);
        notifyDataSetChanged();
    }

    public void setAgent(Agent agent){
        agents.add(agent);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AgentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_agent_item, parent, false);
        return new AgentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AgentViewHolder holder, int position) {
        Agent agent = agents.get(position);
        holder.names.setText(String.format("%s %s", agent.getFirstName(), agent.getLastName()));
        holder.email.setText(agent.getEmail());
        holder.phone.setText(agent.getPhone());
        holder.itemView.setOnClickListener(l -> callback.onClickAgent(agent));
    }

    @Override
    public int getItemCount() {
        return agents.size();
    }
}
