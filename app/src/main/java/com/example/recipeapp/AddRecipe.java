package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AddRecipe extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /**
     * Lage variabler for de forskjellige elementene i layouten
     */
    private TextView txtSaveRecipy;
    private EditText editRecipeName, editTextDescription, editStepByStep, editIngredient, editAmount, txtAmount, txtIngredient;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinnerCuisine, spinnerUnit;
    private Switch switchVegan;

    private FirebaseUser recipie;
    private DatabaseReference reference;

    private String recipieID;


    private LinearLayout layoutList;
    private Button buttonAdd, btnSaveIng;
    ArrayList<Ingredients> ingredientsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_add_recipe));

        /**
         * Aktivere tilbake knapp i action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Aktivisere komponentene i layouten og koble de til variablene.
         */
        mAuth = FirebaseAuth.getInstance();

        txtSaveRecipy = findViewById(R.id.btnSave);
        txtSaveRecipy.setOnClickListener(this);

        editRecipeName = findViewById(R.id.txtRecipeName);
        editTextDescription = findViewById(R.id.txtDescription);
        editStepByStep = findViewById(R.id.txtStep);
        spinnerCuisine = findViewById(R.id.spinnerCuisine);
        switchVegan = findViewById(R.id.txtVeganSwitch);

        progressBar = findViewById(R.id.progressBar);

        layoutList = findViewById(R.id.ingredientList);
        buttonAdd = findViewById(R.id.btnAddIngredient);

        buttonAdd.setOnClickListener(this);

        /**
         * Legge til elementer i cousine spinneren
         */
        ArrayAdapter<CharSequence> adapter_cousine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cousine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(adapter_cousine);

        if (switchVegan != null) {
            switchVegan.setOnCheckedChangeListener(this);
        }
    }

    /**
     * Metode for å registrere bruker. Kalles når man trykker "save" og "add ingredients.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                saveRecipe();
                break;
            case R.id.btnAddIngredient:
                addIngredient();
                break;
        }
    }

    /**
     * Metode for å legge til ingredienser
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
     * Metode for å fjerne ingredienser
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

    /**
     * Metode for å lagre hele oppskriften.
     */
    private void saveRecipe() {
        if (checkIfValidAndRead()) {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser currentUser = mAuth.getCurrentUser();
            String userID = currentUser.getUid();

            DatabaseReference recipies = FirebaseDatabase.getInstance().getReference().child("Recipes");
            Query recipiesHighestId = recipies.child(userID).orderByChild("recipeID").limitToLast(1);
            recipiesHighestId.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Integer highestRecipeID = null;
                    for (DataSnapshot recipeSnapshot : dataSnapshot.getChildren()) {
                        highestRecipeID = recipeSnapshot.child("recipeID").getValue(Integer.class);
                    }
                    if (highestRecipeID == null) {
                        highestRecipeID = 0;
                    }
                    highestRecipeID++;
                    saveRecipeToDatabase(highestRecipeID);

                }

                /**
                 * Metode for å lagre ingrediens til databasen.
                 */
                private void saveIngredientsToDatabase(Integer highestRecipeID) {
                    Integer recipeID = highestRecipeID;
                    for (int i = 0; i < ingredientsList.size(); i++) {

                        Ingredients ingredients = new Ingredients(i + 1, recipeID, ingredientsList.get(i).getIngredient(), ingredientsList.get(i).getAmount(), ingredientsList.get(i).getUnit());

                        FirebaseDatabase.getInstance().getReference("Ingredients")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .push() // Legger til for å ikke overskrive data til databasen hver gang
                                .setValue(ingredients);
                    }
                }

                /**
                 * Metode for å lagre oppskriften til databasen
                 */
                private void saveRecipeToDatabase(Integer highestRecipeID) {
                    Integer recipeID = highestRecipeID;
                    String name = editRecipeName.getText().toString().trim();
                    String description = editTextDescription.getText().toString().trim();
                    String stepByStep = editStepByStep.getText().toString().trim();
                    String cuisine = spinnerCuisine.getSelectedItem().toString();
                    boolean vegan = Boolean.parseBoolean(switchVegan.isChecked() ? "true" : "false");
                    boolean favorite = false;

                    if (name.isEmpty()) {
                        editRecipeName.setError(getText(R.string.recipe_name));
                        editRecipeName.requestFocus();
                        return;
                    }

                    if (description.isEmpty()) {
                        editTextDescription.setError(getText(R.string.recipe_description));
                        editTextDescription.requestFocus();
                        return;
                    }

                    if (stepByStep.isEmpty()) {
                        editStepByStep.setError(getText(R.string.recipe_step_by_step));
                        editStepByStep.requestFocus();
                        return;
                    }

                    progressBar.setVisibility(View.VISIBLE);

                    saveIngredientsToDatabase(highestRecipeID);

                    Recipe recipe = new Recipe(userID, recipeID, name, description, stepByStep, cuisine, vegan, favorite);

                    FirebaseDatabase.getInstance().getReference("Recipes")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .push() // Legger til for å ikke overskrive data til databasen hver gang
                            .setValue(recipe);

                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(AddRecipe.this, R.string.recipe_added, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddRecipe.this, ProfileActivity.class));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(AddRecipe.this, R.string.error_message, Toast.LENGTH_LONG).show();
                }
            });
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

    /**
     * Metode for å aktivere switch vegan i appen.
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switchVegan.setText(R.string.vegan);
        } else {
            switchVegan.setText(R.string.not_vegan);
        }
    }
}