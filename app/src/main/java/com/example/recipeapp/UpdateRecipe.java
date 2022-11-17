package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateRecipe extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private DatabaseReference referenceRecipe;
    private DatabaseReference referenceIngredients;
    private String userID;
    private RecyclerView recyclerView;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch favoriteSwitch;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch veganSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_update_recipes));

        // Activating the "back- button" on the action bar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Adding elements to the cuisine- spinner.
        final Spinner recipeCuisineSpinner = findViewById(R.id.recipeCousine);
        ArrayAdapter<CharSequence> adapter_cuisine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cuisine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeCuisineSpinner.setAdapter(adapter_cuisine);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        referenceRecipe = FirebaseDatabase.getInstance().getReference("Recipes");
        referenceIngredients = FirebaseDatabase.getInstance().getReference("Ingredients");
        if (user != null) {
            userID = user.getUid();
        }
        favoriteSwitch = findViewById(R.id.favoriteSwitch);
        veganSwitch = findViewById(R.id.veganSwitch);

        String recipeId = getIntent().getStringExtra("recipeId");

        final EditText recipeNameEditText = findViewById(R.id.recipeName);
        final EditText recipeDescriptionEditText = findViewById(R.id.recipeDescription);
        final EditText recipeStepsEditText = findViewById(R.id.recipeStepByStep);

        Button btnConfirmUpdateRecipe = findViewById(R.id.btnUpdateRecipe);
        btnConfirmUpdateRecipe.setOnClickListener(this);

        ImageView image = findViewById(R.id.logo);
        image.setOnClickListener(this);

        referenceRecipe.child(userID).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Recipe recipe = snapshot.getValue(Recipe.class);

                if (recipe != null) {
                    String recipeName = recipe.name;
                    String recipeDescription = recipe.description;
                    String recipeSteps = recipe.stepbystep;
                    String recipeCuisine = recipe.cuisine;
                    Integer recipeID = recipe.getRecipeID();

                    recipeNameEditText.setText(recipeName);
                    recipeDescriptionEditText.setText(recipeDescription);
                    recipeStepsEditText.setText(recipeSteps);
                    recipeCuisineSpinner.setSelection(adapter_cuisine.getPosition(recipeCuisine));

                    referenceIngredients.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                                Ingredients ingredients = ingredientSnapshot.getValue(Ingredients.class);

                                if (ingredients != null) {
                                    recyclerView = findViewById(R.id.ingredientList);
                                    String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
                                    DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Ingredients").child(userId);
                                    FirebaseRecyclerOptions<Ingredients> options = new FirebaseRecyclerOptions.Builder<Ingredients>()
                                            .setQuery(mAuth.orderByChild("recipeID").equalTo(recipeID), Ingredients.class)
                                            .build();
                                    IngredientRecAdapter adapter = new IngredientRecAdapter(options);
                                    recyclerView.setAdapter(adapter);
                                    adapter.startListening();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(UpdateRecipe.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(UpdateRecipe.this, R.string.toast_error, Toast.LENGTH_SHORT).show();
        }
    });

        // Adding the "onCheckedChanged" listener to the favorite-switch.
        favoriteSwitch.setOnCheckedChangeListener(this);

        referenceRecipe.child(userID).child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                assert recipe != null;
                favoriteSwitch.setChecked(recipe.getFavorite());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateRecipe.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        // Adding the "onCheckedChanged" listener to the vegan-switch.
        @SuppressLint("UseSwitchCompatOrMaterialCode") final Switch veganSwitch = findViewById(R.id.veganSwitch);
        veganSwitch.setOnCheckedChangeListener(this);

        referenceRecipe.child(userID).child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                assert recipe != null;
                veganSwitch.setChecked(recipe.getVegan());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateRecipe.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adding the back button on the top bar.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnUpdateRecipe) {
            updateRecipe();
        } else if (id == R.id.logo) {
            startActivity(new Intent(this, ProfileActivity.class));
        }
    }

    // Method for updating the recipe.
    private void updateRecipe() {
        String recipeName = ((EditText) findViewById(R.id.recipeName)).getText().toString();
        String recipeDescription = ((EditText) findViewById(R.id.recipeDescription)).getText().toString();
        String recipeSteps = ((EditText) findViewById(R.id.recipeStepByStep)).getText().toString();
        String cuisine = ((Spinner) findViewById(R.id.recipeCousine)).getSelectedItem().toString();
        Boolean vegan = Boolean.parseBoolean(veganSwitch.isChecked() ? "true" : "false");
        Boolean favorite = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");

        if (recipeName.isEmpty()) {
            ((EditText) findViewById(R.id.recipeName)).setError(getResources().getString(R.string.recipe_name));
            ((EditText) findViewById(R.id.recipeName)).requestFocus();
            ((EditText) findViewById(R.id.recipeName)).requestFocus();
            return;
        }
        if (recipeDescription.isEmpty()) {
            ((EditText) findViewById(R.id.recipeDescription)).setError(getResources().getString(R.string.recipe_description));
            ((EditText) findViewById(R.id.recipeDescription)).requestFocus();
            ((EditText) findViewById(R.id.recipeDescription)).requestFocus();
            return;
        }
        if (recipeSteps.isEmpty()) {
            ((EditText) findViewById(R.id.recipeStepByStep)).setError(getResources().getString(R.string.recipe_step_by_step));
            ((EditText) findViewById(R.id.recipeStepByStep)).requestFocus();
            ((EditText) findViewById(R.id.recipeStepByStep)).requestFocus();
            return;
        }
        if (cuisine.isEmpty()) {
            ((Spinner) findViewById(R.id.recipeCousine)).setPrompt(getResources().getString(R.string.recipe_cuisine));
            ((Spinner) findViewById(R.id.recipeCousine)).requestFocus();
            return;
        }

        String recipeId = getIntent().getStringExtra("recipeId");
        referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(favorite);
        referenceRecipe.child(userID).child(recipeId).child("vegan").setValue(vegan);
        referenceRecipe.child(userID).child(recipeId).child("name").setValue(recipeName);
        referenceRecipe.child(userID).child(recipeId).child("description").setValue(recipeDescription);
        referenceRecipe.child(userID).child(recipeId).child("stepbystep").setValue(recipeSteps);
        referenceRecipe.child(userID).child(recipeId).child("cuisine").setValue(cuisine);
        // make a toast for the user
        Toast.makeText(UpdateRecipe.this, R.string.toast_recipe_updated, Toast.LENGTH_SHORT).show();
        // back to showAllRecipes
        startActivity(new Intent(UpdateRecipe.this, ProfileActivity.class));

    }
    // Method for setting text on the switches using oncheckeschanged
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (veganSwitch.isChecked()) {
            veganSwitch.setText(R.string.vegan);
        }
        else if (favoriteSwitch.isChecked()) {
            favoriteSwitch.setText(R.string.update_recipe_favorite);
        } else {
            veganSwitch.setText(R.string.update_recipe_not_vegan);
            favoriteSwitch.setText(R.string.update_recipe_not_favorite);
        }
    }
}