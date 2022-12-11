package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;

import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
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
import java.util.Locale;

public class AddRecipe extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    // Making variables for the different elements in the layout.
    private TextView txtSaveRecipy;
    private EditText editRecipeName, editTextDescription, editStepByStep;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private Spinner spinnerCuisine;
    private Switch switchVegan;
    private ImageView image;
    private LinearLayout layoutList;
    private Button buttonAdd;
    private ArrayList<Ingredients> ingredientsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_add_recipe));

        // Activating "back- button" in the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Activate components in the layout and connecting them to the different variables.
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

        image = findViewById(R.id.logo);
        image.setOnClickListener(this);

        // Adding elements to the cuisine- spinner.
        ArrayAdapter<CharSequence> adapter_cousine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cousine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCuisine.setAdapter(adapter_cousine);

        // Set listener on the vegan switch.
        if (switchVegan != null) {
            switchVegan.setOnCheckedChangeListener(this);
        }
    }

    // Method for starting the other methods based on onClick. Activates button for saving recipe, adding ingredients and back to main page.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSave:
                saveRecipe();
                break;
            case R.id.btnAddIngredient:
                addIngredient();
                break;
            case R.id.logo:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
        }
    }

    // Method for adding ingredients to the recipe.
    private void addIngredient() {
        final View ingredientsView = getLayoutInflater().inflate(R.layout.row_add_ingredient, null, false);

        // Activating the fields in the new views created from row_add_ingredient.
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

    // Method for removing row_add_ingredients view from the main view.
    private void removeView(View view) {
        layoutList.removeView(view);
    }

    // Method for checking if all of the data is entered correctly before saving the recipe.
    private boolean checkIfValidAndRead() {
        ingredientsList.clear();
        boolean result = true;

        for (int i = 1; i < layoutList.getChildCount(); i++) {
            View ingredientsView = layoutList.getChildAt(i);

            EditText txtIngredient = ingredientsView.findViewById(R.id.txtIngredient);
            // Force txt ingredient first letter to be uppercase.
            String ingredient = txtIngredient.getText().toString().trim();
            ingredient = ingredient.substring(0, 1).toUpperCase() + ingredient.substring(1).toLowerCase();
            EditText txtAmount = ingredientsView.findViewById(R.id.txtAmount);
            AppCompatSpinner spinnerUnit = ingredientsView.findViewById(R.id.spinnerUnit);

            Ingredients ingredients = new Ingredients();

            if (!txtIngredient.getText().toString().equals("")) {
                ingredients.setIngredient(ingredient);
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

            if (spinnerUnit.getSelectedItemPosition() == 0) {
                TextView errorText = (TextView)spinnerUnit.getSelectedView();
                errorText.setError("");
                errorText.setTextColor(Color.RED);//just to highlight that this is an error
                errorText.setText(R.string.unit_error);//changes the selected item text to this
                spinnerUnit.requestFocus();
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

    // Method for saving the entire recipe.
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

                // Method for saving the ingredients to the database.
                private void saveIngredientsToDatabase(Integer highestRecipeID) {
                    Integer recipeID = highestRecipeID;
                    for (int i = 0; i < ingredientsList.size(); i++) {
                        Ingredients ingredients = new Ingredients(i + 1, recipeID, ingredientsList.get(i).getIngredient(), ingredientsList.get(i).getAmount(), ingredientsList.get(i).getUnit());

                        FirebaseDatabase.getInstance().getReference("Ingredients")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .push() // Adding this for not overwriting to the database every time
                                .setValue(ingredients);
                    }
                }

                // Method for saving the recipe to the database.
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

                    if (spinnerCuisine.getSelectedItemPosition() == 0) {
                        TextView errorText = (TextView)spinnerCuisine.getSelectedView();
                        errorText.setError("");
                        errorText.setTextColor(Color.RED);//just to highlight that this is an error
                        errorText.setText(R.string.cuisine_error);//changes the selected item text to this
                        spinnerCuisine.requestFocus();
                        return;
                    }

                    String new_cuisine = null;

                    if (Locale.getDefault().toString().equals("nb_NO")) {
                        // If cuisine is equal to the norwegian version replace it with english cuisine name before saving to database.
                        if (cuisine.equals("Japansk")) {
                            new_cuisine = cuisine.replace("Japansk", "Japanese");
                        } else if (cuisine.equals("Fransk")) {
                            new_cuisine = cuisine.replace("Fransk", "French");
                        } else if (cuisine.equals("Italiensk")) {
                            new_cuisine = cuisine.replace("Italiensk", "Italian");
                        } else if (cuisine.equals("Kinesisk")) {
                            new_cuisine = cuisine.replace("Kinesisk", "Chinese");
                        } else if (cuisine.equals("Meksikansk")) {
                            new_cuisine = cuisine.replace("Meksikansk", "Mexican");
                        } else if (cuisine.equals("Norsk")) {
                            new_cuisine = cuisine.replace("Norsk", "Norwegian");
                        } else if (cuisine.equals("Svensk")) {
                            new_cuisine = cuisine.replace("Svensk", "Swedish");
                        } else if (cuisine.equals("Thai")) {
                            new_cuisine = cuisine.replace("Thai", "Thai");
                        } else if (cuisine.equals("Tysk")) {
                            new_cuisine = cuisine.replace("Tysk", "German");
                        } else if (cuisine.equals("Spansk")) {
                            new_cuisine = cuisine.replace("Spansk", "Spanish");
                        } else if (cuisine.equals("Indisk")) {
                            new_cuisine = cuisine.replace("Indisk", "Indian");
                        } else if (cuisine.equals("Colombiansk")) {
                            new_cuisine = cuisine.replace("Colombiansk", "Colombian");
                        } else if (cuisine.equals("Tyrkisk")) {
                            new_cuisine = cuisine.replace("Tyrkisk", "Turkish");
                        } else if (cuisine.equals("Arabisk")) {
                            new_cuisine = cuisine.replace("Arabisk", "Arabic");
                        } else if (cuisine.equals("Libanesisk")) {
                            new_cuisine = cuisine.replace("Libanesisk", "Lebanese");
                        } else if (cuisine.equals("Marokkansk")) {
                            new_cuisine = cuisine.replace("Marokkansk", "Moroccan");
                        } else if (cuisine.equals("Koreansk")) {
                            new_cuisine = cuisine.replace("Koreansk", "Korean");
                        } else if (cuisine.equals("Vietnamesisk")) {
                            new_cuisine = cuisine.replace("Vietnamesisk", "Vietnamese");
                        } else if (cuisine.equals("Malaysisk")) {
                            new_cuisine = cuisine.replace("Malaysisk", "Malaysian");
                        } else if (cuisine.equals("Russisk")) {
                            new_cuisine = cuisine.replace("Russisk", "Russian");
                        } else if (cuisine.equals("Etiopisk")) {
                            new_cuisine = cuisine.replace("Etiopisk", "Ethiopian");
                        } else if (cuisine.equals("Polsk")) {
                            new_cuisine = cuisine.replace("Polsk", "Polish");
                        } else if (cuisine.equals("Tysk")) {
                            new_cuisine = cuisine.replace("Tysk", "German");
                        }
                    } else {
                        new_cuisine = spinnerCuisine.getSelectedItem().toString();
                    }

                    // Force first letter to upper string in name.
                    String nameFirstLetter = name.substring(0, 1).toUpperCase() + name.substring(1);

                    String new_unit = null;
                    if (Locale.getDefault().toString().equals("nb_NO")) {
                        // If units is equal to the norwegian version replace it with english cuisine name before saving to database
                        for (int i = 0; i < ingredientsList.size(); i++) {
                            if (ingredientsList.get(i).getUnit().equals("stk")) {
                                ingredientsList.get(i).setUnit("pcs");
                            } else if (ingredientsList.get(i).getUnit().equals("ts")) {
                                ingredientsList.get(i).setUnit("tsp");
                            } else if (ingredientsList.get(i).getUnit().equals("ss")) {
                                ingredientsList.get(i).setUnit("tbsp");
                            } else if (ingredientsList.get(i).getUnit().equals("dl")) {
                                ingredientsList.get(i).setUnit("dl");
                            } else if (ingredientsList.get(i).getUnit().equals("l")) {
                                ingredientsList.get(i).setUnit("l");
                            } else if (ingredientsList.get(i).getUnit().equals("kg")) {
                                ingredientsList.get(i).setUnit("kg");
                            } else if (ingredientsList.get(i).getUnit().equals("g")) {
                                ingredientsList.get(i).setUnit("g");
                            } else if (ingredientsList.get(i).getUnit().equals("ml")) {
                                ingredientsList.get(i).setUnit("ml");
                            }
                        } // End for loop
                    } else {
                        for (int i = 0; i < ingredientsList.size(); i++) {
                            new_unit = ingredientsList.get(i).getUnit();
                        }
                    }

                    progressBar.setVisibility(View.VISIBLE);
                    saveIngredientsToDatabase(highestRecipeID);
                    Recipe recipe = new Recipe(userID, recipeID, nameFirstLetter, description, stepByStep, new_cuisine, vegan, favorite);

                    FirebaseDatabase.getInstance().getReference("Recipes")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .push() // Adding this for not overwriting to the database every time
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

    // Code for activating the "back- button" in the app.
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

    // Method for activating the "SwitchVegan" in the app.
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            switchVegan.setText(R.string.vegan);
        } else {
            switchVegan.setText(R.string.not_vegan);
        }
    }
}