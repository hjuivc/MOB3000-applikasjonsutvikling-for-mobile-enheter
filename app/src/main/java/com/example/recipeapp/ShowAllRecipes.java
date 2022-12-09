package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class ShowAllRecipes extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private RecyclerView recyclerView;
    RecipeRecAdapter adapter;
    SearchView searchView;
    private Switch veganSwitch;
    private String userID;
    private String recipeId;
    private FirebaseUser user;
    private DatabaseReference referenceRecipe;
    private ImageView image;
    private FloatingActionButton addRecipeBtn;
    private FloatingActionButton resetFilterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_recipes);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_show_all_recipes));

        // Acitvating the "back- button" on the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Activating search view.
        searchView = findViewById(R.id.searchView);

        // Activating recycler view.
        recyclerView = findViewById(R.id.recipeRecyclerView);

        // Activating vegan switch.
        veganSwitch = findViewById(R.id.veganSwitch);
        veganSwitch.setOnCheckedChangeListener(this);

        // Iterate through recipes database and display all recipes.
        referenceRecipe = FirebaseDatabase.getInstance().getReference("Recipes");
        user = FirebaseAuth.getInstance().getCurrentUser();
        userID = user.getUid();

        // Logo button, which takes the user to the ProfileActivity.
        image = findViewById(R.id.logo);
        image.setOnClickListener(this);

        // Button, which takes the user to the AddRecipeActivity.
        addRecipeBtn = findViewById(R.id.addRecipeBtn);
        addRecipeBtn.setOnClickListener(this);

        // Reset filter button.
        resetFilterBtn = findViewById(R.id.resetFilterBtn);
        resetFilterBtn.setOnClickListener(this);

        // Adding elements to the cuisine- spinner.
        final Spinner recipeCousineSpinner = findViewById(R.id.recipeCousine);
        ArrayAdapter<CharSequence> adapter_cousine = ArrayAdapter.createFromResource(this, R.array.cousine_array, android.R.layout.simple_spinner_item);
        adapter_cousine.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        recipeCousineSpinner.setAdapter(adapter_cousine);

        recipeCousineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cuisine = recipeCousineSpinner.getSelectedItem().toString();
                // If cuisine is equal to the norwegian version replace it with english cuisine name before filtering results.
                String new_cuisine = null;
                if (cuisine.equals("Japansk")) {
                    new_cuisine = cuisine.replace("Japansk", "Japanese");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Fransk")) {
                    new_cuisine = cuisine.replace("Fransk", "French");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Italiensk")) {
                    new_cuisine = cuisine.replace("Italiensk", "Italian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Kinesisk")) {
                    new_cuisine = cuisine.replace("Kinesisk", "Chinese");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Meksikansk")) {
                    new_cuisine = cuisine.replace("Meksikansk", "Mexican");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Norsk")) {
                    new_cuisine = cuisine.replace("Norsk", "Norwegian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Svensk")) {
                    new_cuisine = cuisine.replace("Svensk", "Swedish");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Thai")) {
                    new_cuisine = cuisine.replace("Thai", "Thai");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Tysk")) {
                    new_cuisine = cuisine.replace("Tysk", "German");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Spansk")) {
                    new_cuisine = cuisine.replace("Spansk", "Spanish");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Indisk")) {
                    new_cuisine = cuisine.replace("Indisk", "Indian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Colombiansk")) {
                    new_cuisine = cuisine.replace("Colombiansk", "Colombian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Tyrkisk")) {
                    new_cuisine = cuisine.replace("Tyrkisk", "Turkish");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Arabisk")) {
                    new_cuisine = cuisine.replace("Arabisk", "Arabic");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Libanesisk")) {
                    new_cuisine = cuisine.replace("Libanesisk", "Lebanese");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Marokkansk")) {
                    new_cuisine = cuisine.replace("Marokkansk", "Moroccan");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Koreansk")) {
                    new_cuisine = cuisine.replace("Koreansk", "Korean");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Vietnamesisk")) {
                    new_cuisine = cuisine.replace("Vietnamesisk", "Vietnamese");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Malaysisk")) {
                    new_cuisine = cuisine.replace("Malaysisk", "Malaysian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Russisk")) {
                    new_cuisine = cuisine.replace("Russisk", "Russian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Etiopisk")) {
                    new_cuisine = cuisine.replace("Etiopisk", "Ethiopian");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Polsk")) {
                    new_cuisine = cuisine.replace("Polsk", "Polish");
                    showRecipesByCousine(new_cuisine);
                } else if (cuisine.equals("Tysk")) {
                    new_cuisine = cuisine.replace("Tysk", "German");
                    showRecipesByCousine(new_cuisine);
                }
                if (cuisine.equals("Cuisine") || cuisine.equals("Kjøkken")) {
                    showAllRecipes();
                } else {
                    showRecipesByCousine(cuisine);
                }
            }

            private void showRecipesByCousine(String cuisine) {
                if (veganSwitch.isChecked()) {
                    referenceRecipe.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer counter = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Recipe recipe = snapshot.getValue(Recipe.class);
                                if (recipe.getCuisine().equals(cuisine) && recipe.getVegan().equals(true)) {
                                    recipeId = snapshot.getKey();

                                    FirebaseRecyclerOptions<Recipe> options =
                                            new FirebaseRecyclerOptions.Builder<Recipe>()
                                                    .setQuery(referenceRecipe.child(userID).orderByChild("cuisine").equalTo(cuisine), Recipe.class)
                                                    .build();

                                    counter ++;

                                    adapter = new RecipeRecAdapter(options);
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                            if (counter == 0) {
                                Toast.makeText(ShowAllRecipes.this, R.string.toast_recipe_message, Toast.LENGTH_SHORT).show();
                                // if toast, show nothing
                                recyclerView.setAdapter(null);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

                if (!veganSwitch.isChecked()) {
                    referenceRecipe.child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Integer counter = 0;
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Recipe recipe = snapshot.getValue(Recipe.class);

                                if (recipe.getCuisine().equals(cuisine) && recipe.getVegan().equals(false)) {

                                    recipeId = snapshot.getKey();

                                    counter ++;

                                    FirebaseRecyclerOptions<Recipe> options =
                                            new FirebaseRecyclerOptions.Builder<Recipe>()
                                                    .setQuery(referenceRecipe.child(userID).orderByChild("cuisine").equalTo(cuisine), Recipe.class)
                                                    .build();

                                    adapter = new RecipeRecAdapter(options);
                                    adapter.startListening();
                                    recyclerView.setAdapter(adapter);
                                }
                            }
                            if (counter == 0) {
                                Toast.makeText(ShowAllRecipes.this, R.string.toast_recipe_message, Toast.LENGTH_SHORT).show();
                                // if toast, show nothing
                                recyclerView.setAdapter(null);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            }

            private void showAllRecipes() {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Recipes").child(userId);
                FirebaseRecyclerOptions<Recipe> options =
                        new FirebaseRecyclerOptions.Builder<Recipe>()
                                .setQuery(mAuth, Recipe.class)
                                .build();

                adapter = new RecipeRecAdapter(options);
                adapter.startListening();
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("Nothing selected");

            }
        });


        // Iterate through recipes database and display all recipes.
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Recipes").child(userId);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        FirebaseRecyclerOptions<Recipe> options =
                new FirebaseRecyclerOptions.Builder<Recipe>()
                        .setQuery(mAuth, Recipe.class)
                        .build();

        adapter = new RecipeRecAdapter(options);
        recyclerView.setAdapter(adapter);
        }

        @Override
        protected void onStart() {
            super.onStart();
            adapter.startListening();

            if (searchView != null) {
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String newText) {
                        processSearch(newText);
                        return false;
                    }
                    @Override
                    public boolean onQueryTextChange(String newText) {
                        processSearch(newText);
                        return true;
                    }
                });
            }
        }

        public void processSearch(String searchText) {
            // Force first letter in searchText to always capitalize
            if (searchText.length() > 0) {
                searchText = searchText.substring(0, 1).toUpperCase() + searchText.substring(1);
            }
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Recipes").child(userId);
            FirebaseRecyclerOptions<Recipe> options =
                    new FirebaseRecyclerOptions.Builder<Recipe>()
                            .setQuery(mAuth.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff"), Recipe.class)
                            .build();
            adapter = new RecipeRecAdapter(options);
            adapter.startListening();
            recyclerView.setAdapter(adapter);
        }


    // Method for activating the switch- favorite.
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        // Create a spinner adapter listener
        final Spinner recipeCousineSpinner = findViewById(R.id.recipeCousine);
        if (veganSwitch.isChecked()) {
            veganSwitch.setText(R.string.recipe_vegan);

            // if cousine is set to "All" and vegan is checked, show all vegan recipes
            if (recipeCousineSpinner.getSelectedItem().toString().equals("Cuisine") || recipeCousineSpinner.getSelectedItem().toString().equals("Kjøkken")) {
                referenceRecipe.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = snapshot.getValue(Recipe.class);

                            if (recipe.getVegan().equals(true)) {
                                recipeId = snapshot.getKey();

                                counter ++;

                                // Snapshot through userID childs and display all recipes with snapshot key equal to recipeId
                                FirebaseRecyclerOptions<Recipe> options =
                                        new FirebaseRecyclerOptions.Builder<Recipe>()
                                                .setQuery(referenceRecipe.child(userID).orderByChild("vegan").equalTo(true), Recipe.class)
                                                .build();

                                adapter = new RecipeRecAdapter(options);
                                adapter.startListening();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        if (counter == 0) {
                            Toast.makeText(ShowAllRecipes.this, R.string.toast_recipe_message, Toast.LENGTH_SHORT).show();
                            // if toast, show nothing
                            recyclerView.setAdapter(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                // if cousine is set to something else than "All" and vegan is checked, show all vegan recipes with selected cousine
                referenceRecipe.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String cuisine = recipeCousineSpinner.getSelectedItem().toString();
                        // If cuisine is equal to the norwegian version replace it with english cuisine name before filtering results.
                        String new_cuisine = null;
                        if (Locale.getDefault().toString().equals("nb_NO")) {
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
                            new_cuisine = cuisine;
                        }

                        Integer counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = snapshot.getValue(Recipe.class);

                            if (recipe.getCuisine().equals(new_cuisine) && recipe.getVegan().equals(true)) {
                                recipeId = snapshot.getKey();

                                counter++;

                                // Snapshot through userID childs and display only recipes with snapshot key equal to recipeId
                                FirebaseRecyclerOptions<Recipe> options =
                                        new FirebaseRecyclerOptions.Builder<Recipe>()
                                                .setQuery(referenceRecipe.child(userID).orderByChild("cuisine").equalTo(new_cuisine), Recipe.class)
                                                .build();

                                adapter = new RecipeRecAdapter(options);
                                adapter.startListening();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        if (counter == 0) {
                            Toast.makeText(ShowAllRecipes.this, R.string.toast_recipe_message, Toast.LENGTH_SHORT).show();
                            // if toast, show nothing
                            recyclerView.setAdapter(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        } if (!veganSwitch.isChecked()) {
            veganSwitch.setText(R.string.recipe_not_vegan);
            // if cousine is set to "All" and vegan is not checked, show all recipes which is not vegan.
            if (recipeCousineSpinner.getSelectedItem().toString().equals("Cuisine") || recipeCousineSpinner.getSelectedItem().toString().equals("Kjøkken")) {
                referenceRecipe.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Integer counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = snapshot.getValue(Recipe.class);

                            if (recipe.getVegan().equals(false)) {
                                recipeId = snapshot.getKey();
                                counter ++;

                                // Snapshot through userID childs and display only recipes with snapshot key equal to recipeId
                                FirebaseRecyclerOptions<Recipe> options =
                                        new FirebaseRecyclerOptions.Builder<Recipe>()
                                                .setQuery(referenceRecipe.child(userID).orderByChild("vegan").equalTo(false), Recipe.class)
                                                .build();

                                adapter = new RecipeRecAdapter(options);
                                adapter.startListening();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        if (counter == 0) {
                            Toast.makeText(ShowAllRecipes.this, R.string.toast_recipe_message, Toast.LENGTH_SHORT).show();
                            // if toast, show nothing
                            recyclerView.setAdapter(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            } else {
                // if cousine is set to something else than "All" and vegan is checked, show all vegan recipes with selected cousine
                referenceRecipe.child(userID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        String cuisine = recipeCousineSpinner.getSelectedItem().toString();
                        // If cuisine is equal to the norwegian version replace it with english cuisine name before filtering results.
                        String new_cuisine = null;
                        if (Locale.getDefault().toString().equals("nb_NO")) {
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
                            new_cuisine = cuisine;
                        }

                        Integer counter = 0;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Recipe recipe = snapshot.getValue(Recipe.class);
                            if (recipe.getCuisine().equals(new_cuisine) && recipe.getVegan().equals(false)) {
                                recipeId = snapshot.getKey();

                                counter ++;

                                // Snapshot through userID childs and display only recipes with snapshot key equal to recipeId
                                FirebaseRecyclerOptions<Recipe> options =
                                        new FirebaseRecyclerOptions.Builder<Recipe>()
                                                .setQuery(referenceRecipe.child(userID).orderByChild("cuisine").equalTo(new_cuisine), Recipe.class)
                                                .build();

                                adapter = new RecipeRecAdapter(options);
                                adapter.startListening();
                                recyclerView.setAdapter(adapter);
                            }
                        }
                        if (counter == 0) {
                            Toast.makeText(ShowAllRecipes.this, R.string.toast_recipe_message, Toast.LENGTH_SHORT).show();
                            // if toast, show nothing
                            recyclerView.setAdapter(null);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

        // Code for activating the "back- button".
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
            case R.id.logo:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.addRecipeBtn:
                startActivity(new Intent(this, AddRecipe.class));
                break;
            case R.id.resetFilterBtn:
                this.startActivity(new Intent(v.getContext(), ShowAllRecipes.class));
                this.overridePendingTransition(0, 0);
                break;
        }
    }
}