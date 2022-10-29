package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RecipeActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FirebaseUser user;
    private DatabaseReference referenceRecipe;
    private DatabaseReference referenceIngredients;

    private String userID;
    private Switch favoriteSwitch;

    private Button btnUpdateRecipe, btnDeleteRecipe;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        user = FirebaseAuth.getInstance().getCurrentUser();
        referenceRecipe = FirebaseDatabase.getInstance().getReference("Recipes");
        referenceIngredients = FirebaseDatabase.getInstance().getReference("Ingredients");
        userID = user.getUid();
        favoriteSwitch = findViewById(R.id.txtFavoriteSwitch);

        String recipeId = getIntent().getStringExtra("recipeId");

        final TextView recipeNameTextView = findViewById(R.id.recipeName);
        final TextView recipeDescriptionTextView = findViewById(R.id.recipeDescription);
        final TextView recipeStepsTextView = findViewById(R.id.recipeStepByStep);
        final TextView recipeCousineTextView = findViewById(R.id.recipeCousine);

        btnUpdateRecipe = findViewById(R.id.btnUpdateRecipe);
        btnDeleteRecipe = findViewById(R.id.btnDeleteRecipe);

        btnUpdateRecipe.setOnClickListener(this);
        btnDeleteRecipe.setOnClickListener(this);

        if (favoriteSwitch != null) {
            favoriteSwitch.setOnCheckedChangeListener(this);
        }

        referenceRecipe.child(userID).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);

                if (recipe != null) {
                    String recipeName = recipe.name;
                    String recipeDescription = recipe.description;
                    String recipeSteps = recipe.stepbystep;
                    String recipeCousine = recipe.cuisine;
                    Integer recipeID = Integer.valueOf(recipe.getRecipeID());

                    recipeNameTextView.setText(recipeName);
                    recipeDescriptionTextView.setText(recipeDescription);
                    recipeStepsTextView.setText(recipeSteps);
                    recipeCousineTextView.setText(recipeCousine);

                    referenceIngredients.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                                Ingredients ingredients = ingredientSnapshot.getValue(Ingredients.class);

                                if (ingredients != null) {
                                    recyclerView = findViewById(R.id.ingredientList);
                                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                    DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Ingredients").child(userId);
                                    FirebaseRecyclerOptions<Ingredients> options = new FirebaseRecyclerOptions.Builder<Ingredients>()
                                            .setQuery(mAuth.orderByChild("recipeID").equalTo(recipeID), Ingredients.class)
                                            .build();
                                    System.out.println(options);
                                    IngredientRecAdapter adapter = new IngredientRecAdapter(options);
                                    recyclerView.setAdapter(adapter);
                                    adapter.startListening();
                                    }
                            }
                        }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(RecipeActivity.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
                            }
                        });
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
     * Metode for å aktivere switch vegan i appen.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            favoriteSwitch.setText("Favorite");
            boolean vegan = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");

            // Update recipe vegan status
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(vegan);

        } else {
            favoriteSwitch.setText("Not favorite");
            boolean vegan = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");

            // Update recipe vegan status
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(vegan);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnUpdateRecipe:
                updateRecipe();
                break;
            case R.id.btnDeleteRecipe:
                deleteRecipe();
                break;
        }
    }

    private void updateRecipe() {
    }

    private void deleteRecipe() {
     
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}