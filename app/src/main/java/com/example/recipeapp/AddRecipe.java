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
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddRecipe extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    /**
     * Lage variabler for de forskjellige elementene i layouten
     */
    private TextView txtSaveRecipy;
    private EditText editRecipeName, editTextDescription, editStepByStep, editIngredient, editAmount;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinnerCuisine, spinnerUnit;
    private Switch switchVegan;

    private LinearLayout layoutList;
    private Button buttonAdd, btnSaveIng;
    private List<String> units = new ArrayList<>();
    private ArrayList<String> ingredients = new ArrayList<>();
    ArrayList<View> viewsList =new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        /**
         * Legger til elementer i array som skal inn i spinneren
         */
        units.add("g");
        units.add("kg");
        units.add("ml");
        units.add("l");
        units.add("pcs");
        units.add("c");
        units.add("lb");
        units.add("oz");
        units.add("pt");
        units.add("tsp");
        units.add("tbsp");

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
        View ingredientsView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);

        EditText txtIngredient  = ingredientsView.findViewById(R.id.txtIngredient);
        EditText txtAmount = ingredientsView.findViewById(R.id.txtAmount);
        AppCompatSpinner spinnerUnit = (AppCompatSpinner) ingredientsView.findViewById(R.id.spinnerUnit);
        ImageView imageClose = (ImageView) ingredientsView.findViewById(R.id.image_remove);


        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, units);
        spinnerUnit.setAdapter(arrayAdapter);

            imageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(ingredientsView);
            }
        });


            layoutList.addView(ingredientsView);
            ingredientsView.setId(View.generateViewId());
            System.out.println(ingredientsView.getId());
    }

    private void removeView(View view) {
        layoutList.removeView(view);
    }

    private void saveIngredients() {
        System.out.println("save ingredients");
        //TODO: Legge til ingredienser i array
        View child = getLayoutInflater().inflate(R.layout.activity_add_recipe, null, false);
        layoutList.addView(child);
        System.out.println(layoutList.getChildCount());
        System.out.println(layoutList.getChildAt(1));
        System.out.println(layoutList.getChildAt(2).getId());
        System.out.println(layoutList.getChildAt(3));
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

        //TODO: Her må vi ordne med autogenerering av recipeID og ingredientID.
        int recipeID = 1;
        int ingredientID = 2;
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String userID = currentUser.getUid();

        Recipe recipe = new Recipe(userID, recipeID, name, description, stepByStep, cuisine, vegan, favorite);
        Ingredients ingredients = new Ingredients(recipeID, ingredientID, ingredient, amount, unit);

        FirebaseDatabase.getInstance().getReference("Recipes")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push() // Legger til for å ikke overskrive data til databasen hver gang
                .child(userID)
                .setValue(recipe);

        FirebaseDatabase.getInstance().getReference("Ingredients")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .push() // Legger til for å ikke overskrive data til databasen hver gang
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