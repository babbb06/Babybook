package com.example.babybook;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;


import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextFirstName, editTextLastName, editTextEmail, editTextPassword, editTextConfirmPassword;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private static final int PICK_IMAGE_REQUEST = 1;
    private boolean isImageSelected = false;
    private ImageView backBtn, selectedImage;
    private Uri selectedImageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.lastnameEt);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        Button buttonRegister = findViewById(R.id.buttonRegister);
        backBtn = findViewById(R.id.imageView);
        selectedImage = findViewById(R.id.ivSelectedImage);

        ConstraintLayout btnUploadProfile = findViewById(R.id.btnAddImage);

        //SELECT PROFILE PICTURE

        btnUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Profile Photo to Continue"), PICK_IMAGE_REQUEST);
            }
        });



        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        TextView textViewSignIn = findViewById(R.id.textViewSignIn);

        textViewSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate to LoginActivity
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle the back button click here
                onBackPressed(); // or finish() to close the current activity
            }
        });
    }

    private void registerUser() {
        TextView selectProfile = findViewById(R.id.textView4);

        //final String fullName = editTextFirstName.getText().toString().trim();
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String fullName = firstName + " " + lastName;
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString();
        String confirmPassword = editTextConfirmPassword.getText().toString();

        if (TextUtils.isEmpty(firstName)) {
            editTextFirstName.setError("Please enter your first name");
            editTextFirstName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(lastName)) {
            editTextLastName.setError("Please enter your last name");
            editTextLastName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Please enter your email");
            editTextEmail.requestFocus();
            return;
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Please enter a valid email address");
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

        if (!isImageSelected) {
            selectProfile.setError("Select a profile photo");
            Toast.makeText(this, "Select a profile photo", Toast.LENGTH_SHORT).show();
            selectProfile.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Get the user ID
                            String userId = mAuth.getCurrentUser().getUid();

                            // Call the uploadProfilePicture method with fullName and email
                            uploadProfilePicture(userId, fullName, email);

                            Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(RegisterActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });


    }

    private void saveUserData(String fullName, String email, String profileImageUrl) {
        String userId = mAuth.getCurrentUser().getUid();

        Map<String, Object> user = new HashMap<>();
        user.put("ParentID", userId);  // Add the user ID to the database
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("profileImageUrl", profileImageUrl); // Store the profile image URL

        db.collection("parentUsers")  // Adjust collection based on user type
                .document(userId)
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User data saved to Firestore", Toast.LENGTH_SHORT).show();
                            redirectToLogin(); // Redirect to login after registration
                        } else {
                            Toast.makeText(RegisterActivity.this, "Failed to save user data to Firestore", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }


    private void redirectToLogin() {
        // Redirect user to LoginActivity after registration
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Finish the current activity to prevent going back to it via back button
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
                selectedImage.setImageBitmap(bitmap);

                isImageSelected = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // uploadProfilePicture to accept fullName and email
    private void uploadProfilePicture(String userId, String fullName, String email) {
        if (selectedImageUri != null) {
            StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("parent_profile_pictures");
            StorageReference imageRef = storageRef.child(userId + ".jpg");

            imageRef.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        // Image uploaded successfully
                        imageRef.getDownloadUrl().addOnCompleteListener(downloadUrlTask -> {
                            if (downloadUrlTask.isSuccessful()) {
                                String imageUrl = downloadUrlTask.getResult().toString();

                                // After successfully uploading the image, save user data with profile image URL
                                saveUserData(fullName, email, imageUrl);
                            } else {
                                // Handle error while getting the image URL
                            }
                        });
                    })
                    .addOnFailureListener(e -> {
                        // Handle image upload failure
                    });
        }
    }



    /*
    private void updateUserProfileImage(String userId, String imageUrl) {
        // Update the user's profile in Firestore with the image URL
        CollectionReference usersCollection = FirebaseFirestore.getInstance().collection("parentUsers");
        DocumentReference userDocument = usersCollection.document(userId);

        userDocument.update("profileImageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    // Profile image URL updated successfully
                    // You can also navigate to the next screen or perform any other action here
                })
                .addOnFailureListener(e -> {
                    // Handle profile image URL update failure
                });
    }

     */



}
