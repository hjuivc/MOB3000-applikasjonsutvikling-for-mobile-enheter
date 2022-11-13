package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener {

    /**
     * Making variables for the different elements in the layout
     */
    private TextView registerUser;
    private EditText editTextFullName, editTextAge, editTextEmail, editTextPassword;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        /**
         Adding title.
         */
        this.setTitle(getResources().getString(R.string.activity_register_user));

        /**
         * Activating "back- button" on the action bar.
         */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /**
         * Activating the components in the layout and connecting them to the variables.
         */
        mAuth = FirebaseAuth.getInstance();

        registerUser = findViewById(R.id.btnRegisterUser);
        registerUser.setOnClickListener(this);

        editTextFullName = findViewById(R.id.txtFullName);
        editTextAge = findViewById(R.id.txtAge);
        editTextEmail = findViewById(R.id.txtEmail);
        editTextPassword = findViewById(R.id.txtPassword);

        progressBar = findViewById(R.id.progressBar);
    }

    /**
     * Method for registering a new user.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegisterUser:
                registerUser();
                break;
        }
    }

    /**
     * Method for registering a new user. Gets called when the register button is clicked.
     */
    private void registerUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();
        String fullName = editTextFullName.getText().toString().trim();
        String age = editTextAge.getText().toString().trim();

        if (fullName.isEmpty()) {
            editTextFullName.setError(getResources().getString(R.string.register_user_name));
            editTextFullName.requestFocus();
            return;
        }
        if (age.isEmpty()) {
            editTextAge.setError(getResources().getString(R.string.register_user_age));
            editTextAge.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            editTextEmail.setError(getResources().getString(R.string.register_user_email_required));
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getResources().getString(R.string.register_user_email_required));
            editTextEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            editTextPassword.setError(getResources().getString(R.string.register_user_password_required));
            editTextPassword.requestFocus();
            return;
        }

        /**
         * Firebase Authenticator accepts only passwords with a minimum of 6 characters.
         */
        if (password.length() < 6) {
            editTextPassword.setError(getResources().getString(R.string.register_user_password_required));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, age, email);
                            /**
                             * We are using the Firebase Realtime Database to store the user data.
                             * We use the Firebase Authenticator to save the users data.
                             * The aim would be "Users/UID where UID is the user's unique ID.
                             */

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(RegisterUser.this, R.string.toast_register, Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                                // Redirect to login layout
                                                startActivity(new Intent(RegisterUser.this, ProfileActivity.class));
                                            } else {
                                                Toast.makeText(RegisterUser.this, R.string.toast_register_fail, Toast.LENGTH_LONG).show();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterUser.this, R.string.toast_register_fail, Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }
    /**
     * Code for activating the "back- button".
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
