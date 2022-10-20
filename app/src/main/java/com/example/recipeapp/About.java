package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

public class About extends AppCompatActivity {

    private TextView emoji, about, about2, about3, copyright;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_about));

        /**
         * Aktivere tilbake knapp i action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Legge til emoji og tekst på about siden.
         */
        emoji = (TextView) findViewById(R.id.txtAbout);
        emoji.setText(getResources().getString(R.string.txtAbout));

        about = (TextView) findViewById(R.id.txtAbout1);
        about.setText(getResources().getString(R.string.txtAbout1));

        about2 = (TextView) findViewById(R.id.txtAbout2);
        about2.setText(getResources().getString(R.string.txtAbout2));

        about3 = (TextView) findViewById(R.id.txtAbout3);
        about3.setText(getResources().getString(R.string.txtAbout3));

        copyright = (TextView) findViewById(R.id.txtCopyrightAbout);
        copyright.setText(getResources().getString(R.string.txtCopyrightAbout));
    }

    /**
     * Kode for å aktivere tilbake knappen i appen.
     */
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}