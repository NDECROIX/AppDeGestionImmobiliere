package com.openclassrooms.realestatemanager.controllers.fragments;

import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

import com.google.firebase.messaging.FirebaseMessaging;
import com.openclassrooms.realestatemanager.R;

/**
 * Fragment that manage user preferences
 */
public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {

    // Preference keys
    private static final String KEY_SUBSCRIBE_PROPERTIES_PREFERENCE = "subscribe_properties";
    private static final String KEY_SUBSCRIBE_AGENTS_PREFERENCE = "subscribe_agents";
    private static final String KEY_AUTO_SYNC_PREFERENCE = "auto_sync";

    // Views
    private SwitchPreferenceCompat subscribeProperties;
    private SwitchPreferenceCompat subscribeAgents;
    private SwitchPreferenceCompat autoSynchronization;
    private Context context;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
        getPreference();
        configListener();
    }

    /**
     * Initialize preference variables
     */
    private void getPreference() {
        subscribeProperties = findPreference(getString(R.string.key_subscribe_properties));
        subscribeAgents = findPreference(getString(R.string.key_subscribe_agents));
        autoSynchronization = findPreference(getString(R.string.key_auto_sync));
    }

    /**
     * Configures listener on preference variables
     */
    private void configListener() {
        if (subscribeProperties != null) {
            subscribeProperties.setOnPreferenceChangeListener(this);
        }
        if (subscribeAgents != null) {
            subscribeAgents.setOnPreferenceChangeListener(this);
        }
        if (autoSynchronization != null) {
            autoSynchronization.setOnPreferenceChangeListener(this);
        }
    }

    // Manage the change of a preference
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        switch (preference.getKey()) {
            case KEY_SUBSCRIBE_PROPERTIES_PREFERENCE:
                updateSubscribeProperties(newValue);
                break;
            case KEY_SUBSCRIBE_AGENTS_PREFERENCE:
                updateSubscribeAgents(newValue);
                break;
            case KEY_AUTO_SYNC_PREFERENCE:
                updateAutoSync(newValue);
                break;
        }
        return true;
    }

    /**
     * Subscribe/Unsubscribe the user from the properties topic according to the user's choice.
     *
     * @param activation True subscribe - False unsubscribe
     */
    private void updateSubscribeProperties(Object activation) {
        if (activation instanceof Boolean && getContext() != null) {
            boolean subscribe = (boolean) activation;
            if (subscribe) {
                FirebaseMessaging.getInstance().subscribeToTopic(context.getString(R.string.subscribe_new_property))
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, context.getString(R.string.subscribe_new_property_fail), Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(context, context.getString(R.string.subscribe_new_property), Toast.LENGTH_SHORT).show();
                        });
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(context.getString(R.string.subscribe_new_property))
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, R.string.unsubscribe_new_property_fail, Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(context, R.string.unsubscribe_new_property, Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    /**
     * Subscribe/Unsubscribe the user from the agents topic according to the user's choice.
     *
     * @param activation True subscribe - False unsubscribe
     */
    private void updateSubscribeAgents(Object activation) {
        if (activation instanceof Boolean && getContext() != null) {
            boolean subscribe = (boolean) activation;
            if (subscribe) {
                FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.subscribe_new_agent))
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, getString(R.string.subscribe_new_agent_fail), Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(context, getString(R.string.subscribe_new_agent_success), Toast.LENGTH_SHORT).show();
                        });
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(getString(R.string.subscribe_new_agent))
                        .addOnCompleteListener(task -> {
                            if (!task.isSuccessful()) {
                                Toast.makeText(context, R.string.unsubscribe_new_agent_fail, Toast.LENGTH_SHORT).show();
                            }
                            Toast.makeText(context, R.string.unsubscribe_new_agent_success, Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    /**
     * Enable/Disable automatic synchronization at application startup according to the user's choice.
     *
     * @param activation True enable - False disable
     */
    private void updateAutoSync(Object activation) {
        if (activation instanceof Boolean && getContext() != null) {
            boolean autoSync = (boolean) activation;
            if (autoSync) {
                Toast.makeText(context, R.string.automatic_sync_disabled, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, R.string.automatic_sync_enabled, Toast.LENGTH_SHORT).show();
            }
        }
    }
}