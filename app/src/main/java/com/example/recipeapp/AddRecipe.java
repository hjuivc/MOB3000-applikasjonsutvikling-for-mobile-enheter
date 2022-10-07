package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Filterable;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class AddRecipe extends AppCompatActivity implements View.OnClickListener {

    /**
     * Lage variabler for de forskjellige elementene i layouten
     */
    private TextView txtSaveRecipy;
    private EditText editRecipeName, editTextDescription, editStepByStep, editIngredient, editAmount;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinnerCuisine, spinnerUnit;
    private Switch switchVegan;

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
        txtSaveRecipy.setOnClickListener((View.OnClickListener) this);

        editRecipeName = (EditText) findViewById(R.id.txtRecipeName);
        editTextDescription = (EditText) findViewById(R.id.txtDescription);
        editStepByStep = (EditText) findViewById(R.id.txtStep);
        editIngredient = (EditText) findViewById(R.id.txtIngredient);
        editAmount = (EditText) findViewById(R.id.txtAmount);
        spinnerCuisine = (Spinner) findViewById(R.id.spinnerCuisine);
        spinnerUnit = (Spinner) findViewById(R.id.spinnerUnit);
        switchVegan = (Switch) findViewById(R.id.txtVeganSwitch);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        /**
         * Legge til elementer i cousine spinneren
         */
        ArrayAdapter<CharSequence> adapter_cousine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cousine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(adapter_cousine);

        /**
         * Legge til elementer i unit spinneren
         */
        ArrayAdapter<CharSequence> adapter_unit = ArrayAdapter.createFromResource(this, R.array.unit_array, android.R.layout.simple_spinner_item);
        adapter_unit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUnit.setAdapter(adapter_unit);
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
            //TODO: Her må det legges inn case for add ingredient
        }
    }

    /**
     * Metode for å lagre oppskriften.
     */
    private void saveRecipe() {
        String name = editRecipeName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String stepByStep = editStepByStep.getText().toString().trim();
        String ingredient = editIngredient.getText().toString().trim();
        String amount = editAmount.getText().toString().trim();
        //TODO: java.lang.NullPointerException: Attempt to invoke virtual method 'java.lang.String java.lang.Object.toString()' on a null object reference
        String cuisine = spinnerCuisine.getSelectedItem().toString();
        String unit = spinnerUnit.getSelectedItem().toString();
        Boolean vegan = false;
        Boolean favorite = false;


        if (name.isEmpty()) {
            editRecipeName.setError("Recipe name is required");
            editRecipeName.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            editTextDescription.setError("Description is required");
            editTextDescription.requestFocus();
            return;
        }

        if (stepByStep.isEmpty()) {
            editStepByStep.setError("Steps is required");
            editStepByStep.requestFocus();
            return;
        }

        if (ingredient.isEmpty()) {
            editIngredient.setError("Ingredients are required");
            editIngredient.requestFocus();
            return;
        }

        if (amount.isEmpty()) {
            editAmount.setError("Amount is required");
            editAmount.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        int recipeID = 1;
        int ingredientID = 2;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();

        Recipe recipe = new Recipe(userID, recipeID, name, description, stepByStep, cuisine, vegan, favorite);
        Ingredients ingredients = new Ingredients(recipeID, ingredientID, ingredient, amount, unit);

        FirebaseDatabase.getInstance().getReference("Recipes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(userID)
                .setValue(recipe);

        FirebaseDatabase.getInstance().getReference("Ingredients")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(String.valueOf(recipeID))
                .setValue(ingredients);

        progressBar.setVisibility(View.GONE);
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