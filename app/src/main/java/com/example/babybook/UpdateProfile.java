package com.example.babybook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class UpdateProfile extends AppCompatActivity {

    private Toolbar toolbar;
    private ImageView profilePicture;
    private Button updateProfileButton;
    private ProgressBar progressBar;

    private TextInputEditText editTextFirstName, editTextLastName, editTextEmail, etPhoneNumber;
    private TextInputLayout usernameLayout, lastnameLayout, emailLayout, etPhoneNumberLayout;

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private Dialog progressDialog;

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

        //SELECT PROFILE PICTURE

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Photo to Continue"), PICK_IMAGE_REQUEST);
            }
        });

        // Set up button click listener
        updateProfileButton.setOnClickListener(v -> {
            updateProfile();
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

                                // Load the image using Glide
                                Glide.with(UpdateProfile.this)
                                        .load(userProfileImageUrl)
                                        .into(profilePicture);
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
                StorageReference storageRef = FirebaseStorage.getInstance().getReference("parent_profile_pictures/" + userId + ".jpg");
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

        showProgressDialog(this);
        db.collection("parentUsers").document(userId)
                .update(updates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();
                        showMessageDialog("Profile updated successfully!", () -> {
                            startActivity(new Intent(UpdateProfile.this, ParentDashboardActivity.class));
                            finish();
                            hideProgressDialog();
                        });

                    } else {
                        // Toast.makeText(this, "Update failed: " + task.getException(), Toast.LENGTH_SHORT).show();
                        showMessageDialog("Update profile failed. Please Try again.", null);
                    }
                });
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
                profilePicture.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void showProgressDialog(Context context) {
        progressDialog = new Dialog(context);
        progressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setCancelable(false); // Disable dismissing the dialog by tapping outside

        progressDialog.show();
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    private void showMessageDialog(String message, Runnable onOkPressed) {
        // Create a new AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // Set the message
        builder.setMessage(message);

        // Set the "OK" button
        builder.setPositiveButton("OK", (dialog, which) -> {
            dialog.dismiss();
            // Call the provided Runnable
            if (onOkPressed != null) {
                onOkPressed.run();
            }
        });

        // Create and show the dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
