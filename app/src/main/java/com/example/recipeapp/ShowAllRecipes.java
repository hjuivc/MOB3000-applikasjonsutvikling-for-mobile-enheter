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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class ShowAllRecipes extends AppCompatActivity {
    private RecyclerView recyclerView;
    RecipeRecAdapter adapter;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_all_recipes);

        /**
         Legge inn tittel på siden.
         */
        this.setTitle(getResources().getString(R.string.activity_show_all_recipes));

        /**
         * Aktivere tilbake knapp i action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Aktivere search view
         */
        searchView = findViewById(R.id.searchView);

        /**
         * Aktivere recycler view
         */
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recipeRecyclerView);


        /**
         * Iterate through recipes database and display all recipes.
         */
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
                        if (newText.isEmpty()) {
                            processSearch(newText);
                            return false;

                        } else {
                            String formatedSearch = newText.substring(0, 1).toUpperCase() + newText.substring(1);
                            processSearch(formatedSearch);
                            return true;
                        }
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