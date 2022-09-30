package com.example.recipeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_profile));
    }
}