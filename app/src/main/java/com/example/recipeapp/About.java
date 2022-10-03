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
        emoji = (TextView) findViewById(R.id.emoji);
        emoji.setText(getResources().getString(R.string.emoji));

        about = (TextView) findViewById(R.id.about);
        about.setText(getResources().getString(R.string.about));

        about2 = (TextView) findViewById(R.id.about2);
        about2.setText(getResources().getString(R.string.about2));

        about3 = (TextView) findViewById(R.id.about3);
        about3.setText(getResources().getString(R.string.about3));

        copyright = (TextView) findViewById(R.id.copyright);
        copyright.setText(getResources().getString(R.string.copyright));
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