package com.example.recipeapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity {

    private EditText txtFullname, txtEmail, txtAge;
    private Button btnSave;
    private FirebaseAuth fAuth;
    private DatabaseReference databaseReference;
    private String userID;
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        // Adding title.
        this.setTitle(getResources().getString(R.string.edit_title));

        // Activating the "back- button" on the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        txtFullname = findViewById(R.id.fullNameEditText);
        txtEmail = findViewById(R.id.emailEditText);
        txtAge = findViewById(R.id.ageEditText);
        btnSave = findViewById(R.id.saveButton);

        // Activating logo
        image = findViewById(R.id.logo);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EditProfile.this, ProfileActivity.class));
            }
        });

        fAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        userID = fAuth.getCurrentUser().getUid();

        databaseReference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null) {
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;

                    txtFullname.setText(fullName);
                    txtEmail.setText(email);
                    txtAge.setText(age);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, getResources().getString(R.string.toast_error), Toast.LENGTH_SHORT).show();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullName = txtFullname.getText().toString();
                String email = txtEmail.getText().toString();
                String age = txtAge.getText().toString();

                if (fullName.isEmpty()) {
                    txtFullname.setError(getResources().getString(R.string.error_name));
                    txtFullname.requestFocus();
                } else if (email.isEmpty()) {
                    txtEmail.setError(getResources().getString(R.string.error_email));
                    txtEmail.requestFocus();
                } else if (age.isEmpty()) {
                    txtAge.setError(getResources().getString(R.string.error_age));
                    txtAge.requestFocus();
                } else {
                    databaseReference.child(userID).child("fullName").setValue(fullName);
                    databaseReference.child(userID).child("email").setValue(email);
                    databaseReference.child(userID).child("age").setValue(age);

                    Toast.makeText(EditProfile.this, getResources().getString(R.string.profile_updated), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditProfile.this, ProfileActivity.class));
                }
            }
        });
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
}


