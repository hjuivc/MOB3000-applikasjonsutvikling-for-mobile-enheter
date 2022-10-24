package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FavoriteRecipes extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecipeRecAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_recipes);

        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_favorite_recipes));

        /**
         * Aktivere tilbake knapp i action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Aktivere search view
         */
        searchView = findViewById(R.id.searchView);

        /**
         * Iterate through recipes database and display all recipes.
         */
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

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
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