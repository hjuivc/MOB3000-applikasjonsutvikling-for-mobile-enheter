package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
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

public class UpdateRecipe extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FirebaseUser user;
    private DatabaseReference referenceRecipe;
    private DatabaseReference referenceIngredients;
    private String userID;
    private RecyclerView recyclerView;
    private Button btnConfirmUpdateRecipe;
    private Switch favoriteSwitch, veganSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_recipe);



        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_update_recipes));

        /**
         * Aktivere tilbake knapp i action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Legge til elementer i cousine spinneren
         */
        final Spinner recipeCousineSpinner = findViewById(R.id.recipeCousine);
        ArrayAdapter<CharSequence> adapter_cousine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cousine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeCousineSpinner.setAdapter(adapter_cousine);

        user = FirebaseAuth.getInstance().getCurrentUser();
        referenceRecipe = FirebaseDatabase.getInstance().getReference("Recipes");
        referenceIngredients = FirebaseDatabase.getInstance().getReference("Ingredients");
        userID = user.getUid();
        favoriteSwitch = findViewById(R.id.favoriteSwitch);
        veganSwitch = findViewById(R.id.veganSwitch);

        String recipeId = getIntent().getStringExtra("recipeId");

        final EditText recipeNameEditText = findViewById(R.id.recipeName);
        final EditText recipeDescriptionEditText = findViewById(R.id.recipeDescription);
        final EditText recipeStepsEditText = findViewById(R.id.recipeStepByStep);

        btnConfirmUpdateRecipe = findViewById(R.id.btnUpdateRecipe);
        btnConfirmUpdateRecipe.setOnClickListener(this);

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
                    String vegan = String.valueOf(recipe.vegan);
                    String favorite = String.valueOf(recipe.favorite);

                    recipeNameEditText.setText(recipeName);
                    recipeDescriptionEditText.setText(recipeDescription);
                    recipeStepsEditText.setText(recipeSteps);
                    recipeCousineSpinner.setSelection(adapter_cousine.getPosition(recipeCousine));

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
                            Toast.makeText(UpdateRecipe.this, "Noe gikk galt", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(UpdateRecipe.this, "Noe gikk galt", Toast.LENGTH_SHORT).show();
        }
    });

        /**
         * If favorite is set it will show as set.
         */
        favoriteSwitch.setOnCheckedChangeListener(this);

        referenceRecipe.child(userID).child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe.getFavorite() == true) {
                    favoriteSwitch.setChecked(true);
                } else {
                    favoriteSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateRecipe.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * If vegan is set it will show as set.
         */
        final Switch veganSwitch = findViewById(R.id.veganSwitch);
        veganSwitch.setOnCheckedChangeListener(this);

        referenceRecipe.child(userID).child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                if (recipe.getVegan() == true) {
                    veganSwitch.setChecked(true);
                } else {
                    veganSwitch.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(UpdateRecipe.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
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
        }
    }

    /**
     * Metode for å oppdatere oppskriften.
     */
    private void updateRecipe() {
        String recipeName = ((EditText) findViewById(R.id.recipeName)).getText().toString();
        String recipeDescription = ((EditText) findViewById(R.id.recipeDescription)).getText().toString();
        String recipeSteps = ((EditText) findViewById(R.id.recipeStepByStep)).getText().toString();
        String cousine = ((Spinner) findViewById(R.id.recipeCousine)).getSelectedItem().toString();
        Boolean vegan = ((Switch) findViewById(R.id.veganSwitch)).isChecked();
        Boolean favorite = ((Switch) findViewById(R.id.favoriteSwitch)).isChecked();

        if (recipeName.isEmpty()) {
            ((EditText) findViewById(R.id.recipeName)).setError("Oppskriftens navn må fylles ut");
            ((EditText) findViewById(R.id.recipeName)).requestFocus();
            return;
        }
        if (recipeDescription.isEmpty()) {
            ((EditText) findViewById(R.id.recipeDescription)).setError("Oppskriftens beskrivelse må fylles ut");
            ((EditText) findViewById(R.id.recipeDescription)).requestFocus();
            return;
        }
        if (recipeSteps.isEmpty()) {
            ((EditText) findViewById(R.id.recipeStepByStep)).setError("Oppskriftens steg for steg må fylles ut");
            ((EditText) findViewById(R.id.recipeStepByStep)).requestFocus();
            return;
        }
        if (cousine.isEmpty()) {
            ((Spinner) findViewById(R.id.recipeCousine)).setPrompt("Oppskriftens kategori må fylles ut");
            ((Spinner) findViewById(R.id.recipeCousine)).requestFocus();
            return;
        }

        String recipeId = getIntent().getStringExtra("recipeId");
        referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(favorite);
        referenceRecipe.child(userID).child(recipeId).child("vegan").setValue(vegan);
        referenceRecipe.child(userID).child(recipeId).child("name").setValue(recipeName);
        referenceRecipe.child(userID).child(recipeId).child("description").setValue(recipeDescription);
        referenceRecipe.child(userID).child(recipeId).child("stepbystep").setValue(recipeSteps);
        referenceRecipe.child(userID).child(recipeId).child("cuisine").setValue(cousine);
        // make a toast for the user
        Toast.makeText(UpdateRecipe.this, "Recipe updated", Toast.LENGTH_SHORT).show();
        // back to showAllRecipes
        startActivity(new Intent(UpdateRecipe.this, ShowAllRecipes.class));

    }
    /**
     * Metode for å aktivere switch favorite i appen.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (veganSwitch.isChecked()) {
            veganSwitch.setText("Vegan");

            boolean vegan = Boolean.parseBoolean(veganSwitch.isChecked() ? "true" : "false");
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("vegan").setValue(vegan);

        } if (favoriteSwitch.isChecked()) {
            favoriteSwitch.setText("Favorite");
            boolean favorite = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");

            // Update recipe favorite status
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(favorite);

        } if (!veganSwitch.isChecked()) {
            veganSwitch.setText("Ikke vegan");
            boolean vegan = Boolean.parseBoolean(veganSwitch.isChecked() ? "true" : "false");
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("vegan").setValue(vegan);

        } if (!favoriteSwitch.isChecked()) {
            favoriteSwitch.setText("Ikke favoritt");
            boolean favorite = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(favorite);
        }

    }

}