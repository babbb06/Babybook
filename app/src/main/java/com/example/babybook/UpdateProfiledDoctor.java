package com.example.babybook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class UpdateProfiledDoctor extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextEmail, etPhoneNumber, editTextSpecialization;
    private Button btnUpdateProfile;
    ImageView ivProfilePicture;
    private FirebaseFirestore db;
    private FirebaseAuth auth;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;


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
        ivProfilePicture = findViewById(R.id.ivProfilePicture);


        // Initialize the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Update Profile");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbtn);

        }



        // Initialize Firestore and FirebaseAuth
        db = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Fetch user data
        fetchUserData();

        // Update profile button click listener
        btnUpdateProfile.setOnClickListener(v -> {
            updateProfile();
        });

        //SELECT PROFILE PICTURE

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Photo to Continue"), PICK_IMAGE_REQUEST);
            }
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
                                    String userProfileImageUrl = document.getString("profileImageUrl");


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

                                    // Load the image using Glide
                                    Glide.with(UpdateProfiledDoctor.this)
                                            .load(userProfileImageUrl)
                                            .into(ivProfilePicture);
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

    /*----------------------------TO Update ------------------------------*/
    private void updateProfile() {
        String firstName = editTextFirstName.getText().toString();
        String lastName = editTextLastName.getText().toString();
        String phoneNumber = etPhoneNumber.getText().toString();
        String fullPhoneNumber = "+63" + phoneNumber;
        FirebaseUser currentUser = auth.getCurrentUser();
        String fullName = firstName + " " + lastName;

        if (currentUser != null) {
            String userId = currentUser.getUid(); // Get the current user ID

            if (selectedImageUri != null) {
                // Upload image to Firebase Storage
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("doctor_profile_pictures/" + userId + ".jpg");
                storageRef.putFile(selectedImageUri)
                        .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            String profileImageUrl = uri.toString();
                            // Update Firestore document with new data
                            updateFirestore(userId, firstName, lastName, fullName, fullPhoneNumber, profileImageUrl);
                        }))
                        .addOnFailureListener(e -> Toast.makeText(this, "Image upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                // Update Firestore without image
                updateFirestore(userId, firstName, lastName, fullName, fullPhoneNumber, null);
            }
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }


    private void updateFirestore(String userId, String firstName, String lastName, String fullName, String fullPhoneNumber, String profileImageUrl) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("firstName", firstName);
        updates.put("lastName", lastName);
        updates.put("phoneNumber", fullPhoneNumber);
        updates.put("fullName", fullName);
        if (profileImageUrl != null) {
            updates.put("profileImageUrl", profileImageUrl);
        }

        db.collection("doctorUsers").document(userId)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        updatePosts(userId, fullName);
                        Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, DoctorDashboardActivity.class);
                        finish();
                        startActivity(intent);
                        onBackPressed();
                    } else {
                        Toast.makeText(this, "Update failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updatePosts(String userId, String newFullName) {
        db.collection("posts")
                .whereEqualTo("doctorId", userId)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            db.collection("posts").document(document.getId())
                                    .update("doctorName", newFullName);
                        }
                    }
                });
    }






    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //TO DISPLAY THE SELECTED IMAGE TO IMAGEVIEW
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImageUri);
                // Display the selected image or do any other processing as needed
                // For example, you can set it in an ImageView:
                ivProfilePicture.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
