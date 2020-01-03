package com.openclassrooms.realestatemanager.controllers.fragments;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.model.Agent;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Dialog box to create an agent
 */
public class AgentDialogFragment extends DialogFragment {

    // View
    @BindView(R.id.fragment_agent_dialog_tie_first_name)
    TextInputEditText firstName;
    @BindView(R.id.fragment_agent_dialog_tie_last_name)
    TextInputEditText lastName;
    @BindView(R.id.fragment_agent_dialog_tie_email)
    TextInputEditText email;
    @BindView(R.id.fragment_agent_dialog_tie_phone)
    TextInputEditText phone;

    public interface CreateAgentListener {
        void createAgent(Agent agent);

        void updateAgent(Agent agent);
    }

    // is an update
    private boolean update;
    private Agent agent;
    private Context context;
    private CreateAgentListener callback;
    private LayoutInflater layoutInflater;

    public AgentDialogFragment(Context context, CreateAgentListener callback, LayoutInflater layoutInflater, @Nullable Agent agent) {
        this.agent = (agent == null) ? new Agent() : agent;
        this.update = agent != null;
        this.context = context;
        this.callback = callback;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    // Display a dialogue box
    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final View view = layoutInflater.inflate(R.layout.fragment_agent_dialog, null);
        ButterKnife.bind(this, view);
        if (update) configView();
        builder.setView(view)
                .setPositiveButton((update) ? "Update" : "Create", (dialog, id) -> {
                    // Overwrite in on show listener
                })
                .setNegativeButton("Cancel", (dialog, id) -> dialog.cancel());
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(l ->
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    if (allCompleted()) {
                        if (update) {
                            agent.setUpdateDate(new Date().getTime());
                            callback.updateAgent(agent);
                        } else {
                            agent.createId();
                            callback.createAgent(agent);
                        }
                        dialog.dismiss();
                    }
                }));
        return dialog;
    }

    /**
     * Fill in views if update
     */
    private void configView() {
        firstName.setText(agent.getFirstName());
        lastName.setText(agent.getLastName());
        email.setText(agent.getEmail());
        phone.setText(agent.getPhone());
    }

    /**
     * Check all fields
     *
     * @return True if all ok
     */
    private boolean allCompleted() {
        if (firstName.getText() == null || firstName.getText().toString().isEmpty()) {
            showError("Invalid first name");
            return false;
        } else {
            agent.setFirstName(firstName.getText().toString());
        }
        if (lastName.getText() == null || lastName.getText().toString().isEmpty()) {
            showError("Invalid last name");
            return false;
        } else {
            agent.setLastName(lastName.getText().toString());
        }
        if (email.getText() == null || email.getText().toString().isEmpty() || !checkEmail(email.getText().toString())) {
            showError("Invalid email");
            return false;
        } else {
            agent.setEmail(email.getText().toString());
        }
        if (phone.getText() == null || phone.getText().toString().isEmpty() || !checkPhone(phone.getText().toString())) {
            showError("Invalid phone number");
            return false;
        } else {
            agent.setPhone(phone.getText().toString());
        }
        return true;
    }

    /**
     * Check the format of the phone number
     *
     * @param phone Phone number
     * @return true if format ok
     */
    private boolean checkPhone(String phone) {
        return Patterns.PHONE.matcher(phone).matches();
    }

    /**
     * Check the format of the mail
     *
     * @param email Email
     * @return true if format ok
     */
    private boolean checkEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Display message
     *
     * @param message text to show
     */
    private void showError(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
