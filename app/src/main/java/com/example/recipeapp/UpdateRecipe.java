package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class UpdateRecipe extends AppCompatActivity implements View.OnClickListener {

    private FirebaseUser user;
    private DatabaseReference referenceRecipe;
    private DatabaseReference referenceIngredients;
    private String userID;
    private RecyclerView recyclerView;
    private Button btnConfirmUpdateRecipe;

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

    private void updateRecipe() {
    }
}