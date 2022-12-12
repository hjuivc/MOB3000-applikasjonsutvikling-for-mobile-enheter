package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoriteRecipes extends AppCompatActivity implements View.OnClickListener {
    private RecyclerView recyclerView;
    RecipeRecAdapter adapter;
    SearchView searchView;
    private ImageView image;
    private FloatingActionButton addRecipeBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_favorite_recipes));

        // Activating the "back- button" on the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Activating search view
        searchView = findViewById(R.id.searchView);

        // Activating logo
        image = findViewById(R.id.logo);
        image.setOnClickListener(this);

        // Button, which takes the user to the AddRecipeActivity.
        addRecipeBtn = findViewById(R.id.addRecipeBtn);
        addRecipeBtn.setOnClickListener(this);

        // Iterate through recipes database and display all recipes.
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Recipes").child(userId);
        recyclerView = findViewById(R.id.recipeRecyclerView);
        recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        FirebaseRecyclerOptions<Recipe> options =
                new FirebaseRecyclerOptions.Builder<Recipe>()
                        .setQuery(mAuth.orderByChild("favorite").equalTo(true), Recipe.class)
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
        // If the search text is empty, then display all recipes.
        if (searchText.isEmpty()) {
            // Show all favorite recipes again
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Recipes").child(userId);
            recyclerView = findViewById(R.id.recipeRecyclerView);
            recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            FirebaseRecyclerOptions<Recipe> options =
                    new FirebaseRecyclerOptions.Builder<Recipe>()
                            .setQuery(mAuth.orderByChild("favorite").equalTo(true), Recipe.class)
                            .build();

            adapter = new RecipeRecAdapter(options);
            recyclerView.setAdapter(adapter);
            adapter.startListening();

        } else {
            // If the search text is not empty, then display only the recipes that match the search text.
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference mAuth = FirebaseDatabase.getInstance().getReference().child("Recipes").child(userId);
            recyclerView = findViewById(R.id.recipeRecyclerView);
            recyclerView.setLayoutManager(new WrapContentLinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
            FirebaseRecyclerOptions<Recipe> options =
                    new FirebaseRecyclerOptions.Builder<Recipe>()
                            .setQuery(mAuth.orderByChild("name").startAt(searchText).endAt(searchText + "\uf8ff"), Recipe.class)
                            .build();

            adapter = new RecipeRecAdapter(options);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    // Code for activating the "back- button" in the app.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Code for activating the logo.
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logo:
                startActivity(new Intent(this, ProfileActivity.class));
                break;
            case R.id.addRecipeBtn:
                startActivity(new Intent(this, AddRecipe.class));
                break;
        }
    }
}