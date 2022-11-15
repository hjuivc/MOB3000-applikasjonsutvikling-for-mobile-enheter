package com.example.recipeapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    /**
     * Making variables to the different elements in the layout
     */
    private TextView register, forgotPassword;
    private EditText editTextEmail, editTextPassword;
    private Button signIn;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /**
         Adding title.
         */
        this.setTitle(getResources().getString(R.string.activity_login));

        /**
         * Activating the components in layout and connect them to the variables.
         */
        register = findViewById(R.id.registerNewUser);
        register.setOnClickListener(this);

        forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(this);

        signIn = findViewById(R.id.btnLogin);
        signIn.setOnClickListener(this);

        editTextEmail = findViewById(R.id.txtEmail);
        editTextEmail.setOnClickListener(this);

        editTextPassword = findViewById(R.id.txtPassword);
        editTextPassword.setOnClickListener(this);

        progressBar = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * This method being called when the user clicks on the different buttons.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.registerNewUser:
                startActivity(new Intent(this, RegisterUser.class));
                break;
            case R.id.forgotPassword:
                startActivity(new Intent(this, ForgotPassword.class));
                break;
            case R.id.btnLogin:
                userLogin();
                break;
        }
    }

    /**
     * This method gets called when the user clicks on the login button.
     */
    private void userLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (email.isEmpty()) {
            editTextEmail.setError(getText(R.string.email_error2));
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError(getText(R.string.email_error));
            editTextEmail.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            editTextPassword.setError(getText(R.string.password_error));
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError(getText(R.string.minimum_length));
            editTextPassword.requestFocus();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        /**
         * This method checks if the user is registered in the database.
         */
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user.isEmailVerified()) {
                        startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                    } else {
                        user.sendEmailVerification();
                        Toast.makeText(getApplicationContext(), R.string.check_email, Toast.LENGTH_LONG).show();
                    }
                    /**
                     * Sends the user to the profile page if the user is registered.
                     */
                    startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, R.string.login_error_message, Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}