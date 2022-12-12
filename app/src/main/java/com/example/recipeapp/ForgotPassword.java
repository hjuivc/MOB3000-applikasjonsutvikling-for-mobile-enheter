package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {

    private EditText editTextEmail;
    private Button resetPassword;
    private ProgressBar progressBar;

    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Adding title.
        this.setTitle(getResources().getString(R.string.activity_forgot_password));

        // Activating "back- button" on the action bar.
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Activating components in the layout and connecting the variables.
        editTextEmail = findViewById(R.id.txtEmail);
        resetPassword = findViewById(R.id.btnForgotPassword);
        progressBar = findViewById(R.id.progressBar);

        auth = FirebaseAuth.getInstance();

        // Adding a listener to the button.
        resetPassword.setOnClickListener(v -> resetPassword());
    }

    // Method for reset password.
    private void resetPassword() {
        String email = editTextEmail.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError(getResources().getString(R.string.email_message_required));
            editTextEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getResources().getString(R.string.email_message_invalid));
            editTextEmail.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);
        auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ForgotPassword.this, R.string.email_message_request, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(ForgotPassword.this, R.string.email_message_error, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    // Code for activating "back- button" on the action bar.
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