package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

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
    private EditText editRecipeName, editTextDescription, editStepByStep, editIngredient, editAmount,  txtAmount, txtIngredient;
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
        txtSaveRecipy.setOnClickListener((View.OnClickListener) this);

        editRecipeName = (EditText) findViewById(R.id.txtRecipeName);
        editTextDescription = (EditText) findViewById(R.id.txtDescription);
        editStepByStep = (EditText) findViewById(R.id.txtStep);
        spinnerCuisine = (Spinner) findViewById(R.id.spinnerCuisine);
        switchVegan = (Switch) findViewById(R.id.txtVeganSwitch);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        layoutList = findViewById(R.id.ingredientList);
        buttonAdd = findViewById(R.id.btnAddIngredient);

        buttonAdd.setOnClickListener(this);

        btnSaveIng = findViewById(R.id.btnSaveIngredients);

        btnSaveIng.setOnClickListener(this);

        /**
         * Legge til elementer i cousine spinneren
         */
        ArrayAdapter<CharSequence> adapter_cousine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cousine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(adapter_cousine);

        if (switchVegan != null) {
            switchVegan.setOnCheckedChangeListener(this);
            //TODO: Legge til funksjonalitet for å lagre om oppskriften er vegan eller ikke.
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
            case R.id.btnSaveIngredients:
                saveIngredients();
        }
    }

    /**
     * Metode for å legge til ingredienser
     */
    private void addIngredient() {
        final View ingredientsView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);

        EditText txtIngredient  = ingredientsView.findViewById(R.id.txtIngredient);
        EditText txtAmount = ingredientsView.findViewById(R.id.txtAmount);
        AppCompatSpinner spinnerUnit = (AppCompatSpinner) ingredientsView.findViewById(R.id.spinnerUnit);
        ImageView imageClose = (ImageView) ingredientsView.findViewById(R.id.image_remove);


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

    /**
     * Metode for å lagre ingredienser
     */
    private void saveIngredients() {
        if (checkIfValidAndRead()) {

            int ingredientCount = layoutList.getChildCount();
            for (int i=0; i < ingredientsList.size(); i++) {;

                Ingredients ingredients = new Ingredients(i + 1, 2, ingredientsList.get(i).getIngredient(), ingredientsList.get(i).getAmount(), ingredientsList.get(i).getUnit());

                FirebaseDatabase.getInstance().getReference("Ingredients")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .push() // Legger til for å ikke overskrive data til databasen hver gang
                        .setValue(ingredients);
            }
        }
    }

    private boolean checkIfValidAndRead() {
        ingredientsList.clear();
        boolean result = true;

        for (int i = 1; i < layoutList.getChildCount(); i++) {

            View ingredientsView = layoutList.getChildAt(i);

            EditText txtIngredient = (EditText) ingredientsView.findViewById(R.id.txtIngredient);
            EditText txtAmount = (EditText) ingredientsView.findViewById(R.id.txtAmount);
            AppCompatSpinner spinnerUnit = (AppCompatSpinner) ingredientsView.findViewById(R.id.spinnerUnit);

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

            if (spinnerUnit.getSelectedItemPosition()!=0){
                ingredients.setUnit(spinnerUnit.getSelectedItem().toString());
            } else {
                result = false;
                break;
            }
            ingredientsList.add(ingredients);

        }

        if (ingredientsList.size()==0){
            result = false;
            Toast.makeText(this, "Add ingredient first!", Toast.LENGTH_SHORT).show();
        } else if(!result){
            Toast.makeText(this, "Enter All Details Correctly!", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    /**
     * Metode for å lagre oppskriften.
     */
    private void saveRecipe() {
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();

        DatabaseReference recipies = (DatabaseReference) FirebaseDatabase.getInstance().getReference().child("Recipes");
        Query recipiesHighestId = recipies.child(userID).orderByChild("recipeID").limitToLast(1);
        recipiesHighestId.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot){
                for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                    String Key = childSnapshot.getKey();
                    System.out.println("Key: " + Key);
                    String value = childSnapshot.child("recipeID").getValue(String.class);
                    System.out.println("Value: " + value);
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(AddRecipe.this, "Something wrong happened!", Toast.LENGTH_LONG).show();
                throw databaseError.toException(); // don't swallow errors

            }
        });

        String name = editRecipeName.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();
        String stepByStep = editStepByStep.getText().toString().trim();
        String cuisine = spinnerCuisine.getSelectedItem().toString();
        boolean vegan = Boolean.parseBoolean(switchVegan.isChecked() ? "true" : "false");
        boolean favorite = false;

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

        progressBar.setVisibility(View.VISIBLE);

        //TODO: Her må vi ordne med autogenerering av recipeID og ingredientID.


        Recipe recipe = new Recipe(userID, 1, name, description, stepByStep, cuisine, vegan, favorite);

        FirebaseDatabase.getInstance().getReference("Recipes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push() // Legger til for å ikke overskrive data til databasen hver gang
                .setValue(recipe);

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

    //TODO: Må fikse at knappen er aktivert.
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switchVegan.setText("Vegan");
            System.out.println("Vegan");
        } else {
            switchVegan.setText("Not Vegan");
            System.out.println("Not Vegan");
        }
    }
}