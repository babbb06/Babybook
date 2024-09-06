package com.example.babybook;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class DoctorRegisterActivity extends AppCompatActivity {

    private static final String TAG = "DoctorRegisterActivity";
    private EditText editTextFullName, editTextEmail, editTextPassword, editTextConfirmPassword,
            editTextSpecialization, editTextClinicAddress;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFullName = findViewById(R.id.editTextFullName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextSpecialization = findViewById(R.id.editTextSpecialization);
        editTextClinicAddress = findViewById(R.id.editTextClinicAddress);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerDoctor();
            }
        });

        // Setup "Sign In" TextView click listener
        TextView textViewSignIn = findViewById(R.id.textViewSignIn);
        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(DoctorRegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerDoctor() {
        final String fullName = editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();
        final String specialization = editTextSpecialization.getText().toString().trim();
        final String clinicAddress = editTextClinicAddress.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            editTextFullName.setError("Please enter your full name");
            editTextFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Please enter your password");
            editTextPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            editTextPassword.setError("Password must be at least 6 characters");
            editTextPassword.requestFocus();
            return;
        }

        if (!password.equals(confirmPassword)) {
            editTextConfirmPassword.setError("Passwords do not match");
            editTextConfirmPassword.requestFocus();
            return;
        }

        // Register doctor with Firebase Authentication
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            Toast.makeText(DoctorRegisterActivity.this, "Doctor Registration successful", Toast.LENGTH_SHORT).show();

                            // Save additional doctor data to Firestore
                            saveDoctorData(fullName, email, specialization, clinicAddress);

                            // Redirect to doctor dashboard
                            redirectToDashboard();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(DoctorRegisterActivity.this, "Doctor Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveDoctorData(String fullName, String email, String specialization, String clinicAddress) {
        // Ensure user is authenticated
        if (mAuth.getCurrentUser() == null) {
            Log.w(TAG, "saveDoctorData: no authenticated user");
            return;
        }

        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> doctor = new HashMap<>();
        doctor.put("DoctorID", userId);  // Add the user ID to the database
        doctor.put("fullName", fullName);
        doctor.put("email", email);
        doctor.put("specialization", specialization);
        doctor.put("clinicAddress", clinicAddress);

        // Add a new document with a generated ID
        db.collection("doctorUsers")
                .document(userId)
                .set(doctor)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "DocumentSnapshot successfully written!");
                            Toast.makeText(DoctorRegisterActivity.this, "Doctor data saved to Firestore", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.w(TAG, "Error writing document", task.getException());
                            Toast.makeText(DoctorRegisterActivity.this, "Failed to save doctor data to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void redirectToDashboard() {
        Intent intent = new Intent(DoctorRegisterActivity.this, DoctorDashboardActivity.class);
        startActivity(intent);
        finish();
    }
}
