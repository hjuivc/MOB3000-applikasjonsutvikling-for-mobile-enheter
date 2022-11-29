package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import java.util.ArrayList;
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
    private LinearLayout layoutList;
    ArrayList<Ingredients> ingredientsList = new ArrayList<>();
    private Button buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_update_recipes));

        // Activating the "back- button" on the action bar.
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        // Adding ingredient layout
        layoutList = findViewById(R.id.ingredientList);

        // Adding the "add ingredient" button.
        buttonAdd = findViewById(R.id.btnAddIngredient);
        buttonAdd.setOnClickListener(this);

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

                                // Loop through all ingredients and add them to the list if they belong to the recipe.
                                if (ingredients != null) {
                                    if (ingredients.getRecipeID() == recipeID) {
                                        String ingredientUnit = ingredients.unit;
                                        ingredientsList.add(ingredients);
                                    }
                                    }
                                }
                            // Create spinner unit for ingredients to load existing data into


                            // Add the view to the layout.
                            // Loop through the list and add the ingredients to the layout.
                            for (Ingredients ingredient : ingredientsList) {
                                View view = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);

                                // Create a spinner
                                Spinner spinner = view.findViewById(R.id.spinnerUnit);
                                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(UpdateRecipe.this, R.array.unit_array, android.R.layout.simple_spinner_item);
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinner.setAdapter(adapter);
                                EditText ingredientName = view.findViewById(R.id.txtIngredient);
                                EditText ingredientAmount = view.findViewById(R.id.txtAmount);
                                String ingredientUnit = ingredient.unit;
                                ImageView imageClose = view.findViewById(R.id.image_remove);


                                ingredientName.setText(ingredient.getIngredient());
                                ingredientAmount.setText(ingredient.getAmount());
                                spinner.setSelection(adapter.getPosition(ingredientUnit));

                                layoutList.addView(view);

                                imageClose.setOnClickListener(v -> removeView(view));

                            }
                        }
                        private void removeView(View view) {
                            layoutList.removeView(view);
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
        } else if (id == R.id.btnAddIngredient) {
            addIngredient();
        }
    }

    /**
     * Method for adding ingredients to the recipe.
     */
    private void addIngredient() {
        final View ingredientsView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);

        EditText txtIngredient = ingredientsView.findViewById(R.id.txtIngredient);
        EditText txtAmount = ingredientsView.findViewById(R.id.txtAmount);
        AppCompatSpinner spinnerUnit = ingredientsView.findViewById(R.id.spinnerUnit);
        ImageView imageClose = ingredientsView.findViewById(R.id.image_remove);

        ArrayAdapter<CharSequence> adapter_unit = ArrayAdapter.createFromResource(this, R.array.unit_array, android.R.layout.simple_spinner_item);
        adapter_unit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter_unit);

        imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(ingredientsView);
            }
        });
        layoutList.addView(ingredientsView);
    }

    /**
     * Method for removing ingredients from the recipe.
     */
    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private boolean checkIfValidAndRead() {
        ingredientsList.clear();
        boolean result = true;

        for (int i = 1; i < layoutList.getChildCount(); i++) {

            View ingredientsView = layoutList.getChildAt(i);

            EditText txtIngredient = ingredientsView.findViewById(R.id.txtIngredient);
            EditText txtAmount = ingredientsView.findViewById(R.id.txtAmount);
            AppCompatSpinner spinnerUnit = ingredientsView.findViewById(R.id.spinnerUnit);

            Ingredients ingredients = new Ingredients();

            if (!txtIngredient.getText().toString().equals("")) {
                ingredients.setIngredient(txtIngredient.getText().toString());
            } else {
                result = false;
                break;
            }

            if (!txtAmount.getText().toString().equals("")) {
                ingredients.setAmount(txtAmount.getText().toString());
            } else {
                result = false;
                break;
            }

            if (spinnerUnit.getSelectedItemPosition() != 0) {
                ingredients.setUnit(spinnerUnit.getSelectedItem().toString());
            } else {
                result = false;
                break;
            }
            ingredientsList.add(ingredients);

        }

        if (ingredientsList.size() == 0) {
            result = false;
            Toast.makeText(this, R.string.add_ingredient_first, Toast.LENGTH_SHORT).show();
        } else if (!result) {
            Toast.makeText(this, R.string.ingredient_error, Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    // Method for updating the recipe.
    private void updateRecipe() {
        if (checkIfValidAndRead()) {
            // Get id from intent
            Intent intent = getIntent();
            String recipeId = intent.getStringExtra("recipeId");

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
            // Snapshot through referenceIngredients and delete all ingredients.

            referenceRecipe.child(userID).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Recipe recipe = snapshot.getValue(Recipe.class);
                    if (recipe != null) {
                        Integer recipeID = recipe.getRecipeID();
                        // Loop through reference ingredients and delete all ingredients.
                        referenceIngredients.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                    Ingredients ingredients = dataSnapshot.getValue(Ingredients.class);

                                    if (ingredients != null) {
                                        Integer ingredientRecipeID = Integer.valueOf(ingredients.getRecipeID());
                                        if (ingredientRecipeID.equals(recipeID)) {
                                            String ingredientId = dataSnapshot.getKey();
                                            referenceIngredients.child(userID).child(ingredientId).removeValue();

                                        }
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                        for (int i = 0; i < ingredientsList.size(); i++) {
                            Ingredients ingredients = new Ingredients(i + 1, recipeID, ingredientsList.get(i).getIngredient(), ingredientsList.get(i).getAmount(), ingredientsList.get(i).getUnit());
                            // Get recipeID from the intent
                            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(favorite);
                            referenceRecipe.child(userID).child(recipeId).child("vegan").setValue(vegan);
                            referenceRecipe.child(userID).child(recipeId).child("name").setValue(recipeName);
                            referenceRecipe.child(userID).child(recipeId).child("description").setValue(recipeDescription);
                            referenceRecipe.child(userID).child(recipeId).child("stepbystep").setValue(recipeSteps);
                            referenceRecipe.child(userID).child(recipeId).child("cuisine").setValue(cuisine);



                            // Add ingredients to database
                            FirebaseDatabase.getInstance().getReference("Ingredients")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .push() // Adding this for not overwriting to the database every time
                                    .setValue(ingredients);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateRecipe.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            // make a toast for the user
            Toast.makeText(UpdateRecipe.this, R.string.toast_recipe_updated, Toast.LENGTH_SHORT).show();
            // back to showAllRecipes
            startActivity(new Intent(UpdateRecipe.this, ProfileActivity.class));
            }


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