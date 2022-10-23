package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeActivity extends AppCompatActivity {

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Recipes");
        userID = user.getUid();

        String recipeId = getIntent().getStringExtra("recipeId");

        final TextView recipeNameTextView = findViewById(R.id.recipeName);
        final TextView recipeDescriptionTextView = findViewById(R.id.recipeDescription);
        final TextView recipeStepsTextView = findViewById(R.id.recipeStepByStep);

        reference.child(userID).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);

                if (recipe != null) {
                    String recipeName = recipe.name;
                    String recipeDescription = recipe.description;
                    String recipeSteps = recipe.stepbystep;

                    recipeNameTextView.setText(recipeName);
                    recipeDescriptionTextView.setText(recipeDescription);
                    recipeStepsTextView.setText(recipeSteps);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeActivity.this, "Noe gikk galt", Toast.LENGTH_LONG).show();
            }
        });




        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_show_recipe));

        /**
         * Aktivere tilbake knapp i action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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