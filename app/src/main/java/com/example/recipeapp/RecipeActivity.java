package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class RecipeActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private FirebaseUser user;
    private DatabaseReference referenceRecipe;
    private DatabaseReference referenceIngredients;

    private String userID;
    private Switch favoriteSwitch;
    private ImageView image;

    private Button btnUpdateRecipe, btnDeleteRecipe;

    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);
        // Always keep the screen on when showing this page
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


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
        favoriteSwitch.setOnCheckedChangeListener(this);

        image = findViewById(R.id.logo);
        image.setOnClickListener(this);

        /**
         * If favorite is set it will show as set.
         */
        referenceRecipe.child(userID).child(recipeId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Recipe recipe = dataSnapshot.getValue(Recipe.class);
                try {
                    if (recipe.getFavorite()) {
                        favoriteSwitch.setChecked(true);
                    } else {
                        favoriteSwitch.setChecked(false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(RecipeActivity.this, databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        /**
         * Iterate through recipes database and display all recipes.
         */
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
                                    IngredientRecAdapter adapter = new IngredientRecAdapter(options);
                                    recyclerView.setAdapter(adapter);
                                    adapter.startListening();
                                    }
                            }
                        }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(RecipeActivity.this, R.string.toast_error, Toast.LENGTH_LONG).show();
                            }
                        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(RecipeActivity.this, R.string.toast_error, Toast.LENGTH_LONG).show();
            }
        });




        /**
         Adding title.
         */
        this.setTitle(getResources().getString(R.string.activity_show_recipe));

        /**
         * Activating "back- button" on the action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    /**
     * Method for acitvating the SwitchVegan in the app
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            favoriteSwitch.setText(R.string.update_recipe_favorite);
            boolean vegan = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");

            // Update recipe vegan status
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(vegan);

        } else {
            favoriteSwitch.setText(R.string.update_recipe_not_favorite);
            boolean vegan = Boolean.parseBoolean(favoriteSwitch.isChecked() ? "true" : "false");

            // Update recipe vegan status
            String recipeId = getIntent().getStringExtra("recipeId");
            referenceRecipe.child(userID).child(recipeId).child("favorite").setValue(vegan);
        }
    }

    /**
     * Code for activating the "back- button" on the action bar.
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
            case R.id.logo:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
    }

    private void updateRecipe() {
        String recipeId = getIntent().getStringExtra("recipeId");
        Intent intent = new Intent(this, UpdateRecipe.class);
        intent.putExtra("recipeId", recipeId);
        startActivity(intent);
    }

    private void deleteRecipe() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        String recipeId = getIntent().getStringExtra("recipeId");

                        // Get recipeID in recipe
                        referenceRecipe.child(userID).child(recipeId).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                Recipe recipe = snapshot.getValue(Recipe.class);


                                if (recipe != null) {
                                    Integer recipeID = Integer.valueOf(recipe.getRecipeID());


                                    // Delete ingredients
                                    referenceIngredients.child(userID).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            for (DataSnapshot ingredientSnapshot : snapshot.getChildren()) {
                                                Ingredients ingredients = ingredientSnapshot.getValue(Ingredients.class);

                                                if (ingredients != null) {
                                                    Integer ingredientRecipeID = Integer.valueOf(ingredients.getRecipeID());
                                                    referenceRecipe.child(userID).child(recipeId).removeValue();

                                                    if (ingredientRecipeID.equals(recipeID)) {
                                                        String ingredientId = ingredientSnapshot.getKey();
                                                        referenceIngredients.child(userID).child(ingredientId).removeValue();

                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Toast.makeText(RecipeActivity.this, R.string.toast_error, Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Toast.makeText(RecipeActivity.this, R.string.toast_error, Toast.LENGTH_LONG).show();
                            }
                        });
                        // Loop through ingredients database with snapshot and delete all ingredients with the same recipeID as the recipe that is being deleted.

                        Toast.makeText(RecipeActivity.this, R.string.toast_recipe_deleted, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(RecipeActivity.this, ProfileActivity.class));
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_recipe_toast).setPositiveButton(R.string.builder_message_yes, dialogClickListener).setNegativeButton(R.string.builder_message_no, dialogClickListener).show();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}