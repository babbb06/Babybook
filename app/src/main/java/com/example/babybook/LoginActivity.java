package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private TextInputLayout passwordLayout, emailLayout;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        passwordLayout = findViewById(R.id.passwordLayout);
        emailLayout = findViewById(R.id.emailLayout);

        Button buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        LinearLayout textViewSignUp = findViewById(R.id.textViewSignUp);
        textViewSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to ChooseUserActivity to decide between parent or doctor registration
                Intent intent = new Intent(LoginActivity.this, ChooseUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            emailLayout.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }else {
            // Clear the error if password is not empty
            emailLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordLayout.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }else {
            // Clear the error if password is not empty
            passwordLayout.setError(null);
        }

        // Sign in user with Firebase Authentication
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();

                            // Redirect user to appropriate dashboard based on their role
                            redirectToDashboard();
                        } else {
                            // If sign in fails, get the exception
                            Exception exception = task.getException();

                            if (exception != null) {
                                // Check if the error is related to wrong password
                                if (exception instanceof FirebaseAuthInvalidCredentialsException) {
                                    Toast.makeText(LoginActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_LONG).show();
                                    editTextPassword.requestFocus();

                                } else if (exception instanceof FirebaseAuthInvalidUserException) {
                                    // Handle case where the user doesn't exist or is disabled
                                    Toast.makeText(LoginActivity.this, "User not found. Please register or check your email.", Toast.LENGTH_LONG).show();
                                } else {
                                    // Generic error message for other exceptions
                                    Toast.makeText(LoginActivity.this, "Authentication failed: " + exception.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    private void redirectToDashboard() {
        // Determine user role and redirect accordingly
        String userId = mAuth.getCurrentUser().getUid();
        db.collection("parentUsers").document(userId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // User is a parent
                                Intent intent = new Intent(LoginActivity.this, ParentDashboardActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                // User is not a parent, check if they are a doctor
                                db.collection("doctorUsers").document(userId).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        // User is a doctor
                                                        Intent intent = new Intent(LoginActivity.this, DoctorDashboardActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        // User role not recognized
                                                        Toast.makeText(LoginActivity.this, "User role not recognized", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Toast.makeText(LoginActivity.this, "Failed to check user role", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Failed to check user role", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
