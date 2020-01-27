package com.openclassrooms.nycrealestatemanager.view.holders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.openclassrooms.nycrealestatemanager.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AgentViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.activity_agent_item_name)
    public TextView names;

    @BindView(R.id.activity_agent_item_email)
    public TextView email;

    @BindView(R.id.activity_agent_item_phone)
    public TextView phone;

    public AgentViewHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
    }
}
