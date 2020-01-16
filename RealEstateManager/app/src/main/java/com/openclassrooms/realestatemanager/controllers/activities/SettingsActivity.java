package com.openclassrooms.realestatemanager.controllers.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.openclassrooms.realestatemanager.R;
import com.openclassrooms.realestatemanager.controllers.fragments.SettingsFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Activity that displays user preferences
 */
public class SettingsActivity extends AppCompatActivity {

    @BindView(R.id.settings_toolbar)
    Toolbar settingsToolbar;

    /**
     * Create an intent of this activity
     *
     * @param context Context to launch this activity
     * @return SettingsActivity intent
     */
    public static Intent newIntent(Context context) {
        return new Intent(context, SettingsActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.settings, new SettingsFragment())
                .commit();
        configToolbar();
    }

    /**
     * Configures toolbar
     */
    private void configToolbar() {
        setSupportActionBar(settingsToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getString(R.string.toolbar_title_settings));
        }
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
