package com.example.babybook;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UpdateProfiledDoctor extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextEmail, etPhoneNumber, editTextSpecialization;
    private Button btnUpdateProfile;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile_doctor);

        // Initialize UI elements
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.lastnameEt);
        editTextEmail = findViewById(R.id.editTextEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumberClinic);
        editTextSpecialization = findViewById(R.id.editTextSpecialization);
        btnUpdateProfile = findViewById(R.id.profle_update_btn);


        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
        }



        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Fetch user data
        fetchUserData();

        // Update profile and change password button click listener
        btnUpdateProfile.setOnClickListener(v -> {


        });
    }

    private void fetchUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user ID

            db.collection("doctorUsers").document(userId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    // Get data from Firestore
                                    String firstName = document.getString("firstName");
                                    String lastName = document.getString("lastName");
                                    String email = document.getString("email");
                                    String phoneNumber = document.getString("phoneNumber");
                                    String specialization = document.getString("specialization");

                                    // Remove first 3 characters from phone number if it is long enough
                                    if (phoneNumber.length() > 3) {
                                        phoneNumber = phoneNumber.substring(3);
                                    } else {
                                        phoneNumber = ""; // Or handle this case as needed
                                    }

                                    // Set data to EditText fields
                                    editTextFirstName.setText(firstName);
                                    editTextLastName.setText(lastName);
                                    editTextEmail.setText(email);
                                    etPhoneNumber.setText(phoneNumber);
                                    editTextSpecialization.setText(specialization);
                                } else {
                                    Toast.makeText(UpdateProfiledDoctor.this, "No such document", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(UpdateProfiledDoctor.this, "Fetch failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        // Logic to update profile (excluding password)
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String specialization = editTextSpecialization.getText().toString();
        String fullphoneNumber = "+36" + phoneNumber;
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user ID

            // Update Firestore document
            db.collection("users").document(userId)
                    .update("firstName", firstName,
                            "lastName", lastName,
                            "email", email,
                            "phoneNumber", fullphoneNumber,
                            "specialization", specialization)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Update failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish(); // Close the current activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
