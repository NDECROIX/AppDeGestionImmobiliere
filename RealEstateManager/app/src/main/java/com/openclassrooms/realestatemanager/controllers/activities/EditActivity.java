package com.openclassrooms.realestatemanager.controllers.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.openclassrooms.realestatemanager.R;

public class EditActivity extends AppCompatActivity {

    public interface startEditActivityListener{
        void createProperty();
    }

    public static Intent newIntent(Context context){
        return new Intent(context, EditActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }
}
