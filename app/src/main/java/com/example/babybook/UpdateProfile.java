package com.example.babybook;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;


public class UpdateProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profilePicture;
    private Button updateProfileButton;
    private ProgressBar progressBar;

    private TextInputEditText editTextFirstName, editTextLastName, editTextEmail, etPhoneNumber;
    private TextInputLayout usernameLayout, lastnameLayout, emailLayout, etPhoneNumberLayout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        // Initialize Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false); // Disable title if needed

        // Initialize views
        profilePicture = findViewById(R.id.ivProfilePicture);
        updateProfileButton = findViewById(R.id.profle_update_btn);
        progressBar = findViewById(R.id.profile_progress_bar);

        // Initialize TextInputEditTexts and TextInputLayouts
        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.lastnameEt);
        editTextEmail = findViewById(R.id.editTextEmail);
        etPhoneNumber = findViewById(R.id.etPhoneNumberClinic);

        usernameLayout = findViewById(R.id.usernameLayout);
        lastnameLayout = findViewById(R.id.lastnameLayout);
        emailLayout = findViewById(R.id.emailLayout);
        etPhoneNumberLayout = findViewById(R.id.etPhoneNumberLayout);

        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Show back button
        }



        // Fetch user data
        fetchUserData();

        // Set up button click listener
        updateProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile();
            }
        });
    }

    private void fetchUserData() {
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user ID

            db.collection("parentUsers").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                // Get data from Firestore
                                String firstName = document.getString("firstName");
                                String lastName = document.getString("lastName");
                                String email = document.getString("email");
                                String phoneNumber = document.getString("phoneNumber");

                                // Set data to EditText fields
                                editTextFirstName.setText(firstName);
                                editTextLastName.setText(lastName);
                                editTextEmail.setText(email);
                                etPhoneNumber.setText(phoneNumber);
                            } else {
                                Toast.makeText(UpdateProfile.this, "No such document", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UpdateProfile.this, "Fetch failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProfile() {
        // Show progress bar while updating profile
        progressBar.setVisibility(View.VISIBLE);

        // Retrieve input values
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String email = editTextEmail.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user ID

            // Update Firestore document
            db.collection("doctorUsers").document(userId)
                    .update("firstName", firstName,
                            "lastName", lastName,
                            "email", email,
                            "phoneNumber", phoneNumber)
                    .addOnCompleteListener(task -> {
                        progressBar.setVisibility(View.GONE); // Hide progress bar after update
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Update failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            progressBar.setVisibility(View.GONE); // Hide progress bar if no user is logged in
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
