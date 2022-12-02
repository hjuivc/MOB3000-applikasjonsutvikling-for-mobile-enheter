package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Button btnLogout, btnAbout, btnAddRecipe, btnFavoriteRecipes, btnShowAllRecipes;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_profile));

        // Retrieving the user's information from the database.
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        final TextView greetingTextView = findViewById(R.id.greeting);
        final TextView emailTextView = findViewById(R.id.email);
        final TextView fullNameTextView = findViewById(R.id.fullName);
        final TextView ageTextView = findViewById(R.id.age);

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;


                    greetingTextView.setText(getResources().getString(R.string.txtProfile_welcome) + ", " + fullName + "!");
                    emailTextView.setText(getResources().getString(R.string.txtProfile_email) + ": " + email);
                    fullNameTextView.setText(getResources().getString(R.string.txtProfile_name) + ": " + fullName);
                    ageTextView.setText(getResources().getString(R.string.txtProfile_age) + ": " +age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ProfileActivity.this, R.string.error_message, Toast.LENGTH_LONG).show();
            }
        });

        initViews();

        // Show All Recipes.
        btnShowAllRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, ShowAllRecipes.class);
            startActivity(intent);
        });

        // Favorite recipes.
        btnFavoriteRecipes.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, FavoriteRecipes.class);
            startActivity(intent);
        });

        // Add recipe.
        btnAddRecipe.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, AddRecipe.class);
            startActivity(intent);
        });

        // About.
        btnAbout.setOnClickListener(v -> {
            Intent intent = new Intent(ProfileActivity.this, About.class);
            startActivity(intent);
        });

        // Logout button.
        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            Toast.makeText(ProfileActivity.this, R.string.profile_message, Toast.LENGTH_SHORT).show();
        });
    }

    // Method for initializing the buttons.
    private void initViews() {
        btnLogout = findViewById(R.id.btnLogout);
        btnAbout = findViewById(R.id.btnAbout);
        btnAddRecipe = findViewById(R.id.btnAddRecipe);
        btnFavoriteRecipes = findViewById(R.id.btnFavoriteRecipes);
        btnShowAllRecipes = findViewById(R.id.btnShowAllRecipes);
    }
}